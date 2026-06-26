package r01f.core.fileexplorer.command;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Splitter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.core.fileexplorer.FileExplorerConstants;
import r01f.core.fileexplorer.FileExplorerItemPathHash;
import r01f.core.fileexplorer.FileExplorerStorage;
import r01f.core.fileexplorer.FileExplorerVolume;
import r01f.core.fileexplorer.command.response.FileExplorerCommandResponseForUpload;
import r01f.core.fileexplorer.command.response.FileExplorerCommandResponseObject;
import r01f.core.fileexplorer.command.response.FileExplorerFileStoreItem;
import r01f.debug.Debuggable;
import r01f.file.FileNameAndExtension;
import r01f.file.FileProperties;
import r01f.io.util.StringPersistenceUtils;
import r01f.objectstreamer.Marshaller;
import r01f.servlet.HttpServletRequestUtils;
import r01f.servlet.HttpServletRequestUtils.HttpMultipartPOSTPartContentDisposition;
import r01f.types.Path;
import r01f.types.Range;
import r01f.util.types.StringSplitter;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

/**
 * see: https://github.com/Studio-42/elFinder/wiki/Client-Server-API-2.1#upload
 * 
 * Process file upload requests. 
 * Client may request the upload of multiple files at once.
 * 
 * HTTP POST
 * =========
 * Arguments
 *	  - cmd: upload
 *
 *	  Base params
 *	  - target: hash of the directory to upload
 *	  - upload[]: array of multipart files to upload
 *	  - upload_path[]: (optional) array of target directory hash for each of the upload[] files
 *	  - name[]: (optional) array of files name for each of the upload[] files
 *	  - mtime[]: (optional) array of files UNIX time stamp for each of the upload[] files
 *
 *	  For chunked uploads
 *	  - range: the data range: [start byte],[chunk length],[total bytes] > used to detect when the upload has finished
 *			   ie: range value=1073726468,15356,1073741824
 *	  - chunk: chunk data:  [file name].{part num}_{total parts}.part
 *			   ie: chunk value=testfile.org-1GB- Corrupt.zip.514_514.part
 *
 *	  In the event of name collision
 *	  - hashes[hash]: (optional) array of hashes of files at the target folder where the file is being uploaded
 *	  - renames[]: (optional) array of rename request filenames
 *	  - suffix: (optional) rename suffix in case of collision
 *	  - overwrite : (optional) Flag to overwrite or save another name. 
 *						- If "0" > if a file with the same name exists, upload file with different name (see suffix) 
 *						- If "1" > if a file with the same name exists, overwrite the file
 * Response:
 *		- added : (Array) of files that were successfully uploaded. Information about File/Directory
 * 		- _chunkmerged: (String) file name of server side.　
 * 						ONLY when the upload of all the chunk file has been completed.
 * 		- _name: (String) uploading file name. 
 * 				 ONLY when the upload of all the chunk file has been completed.
 * 
 *		If the files could not be uploaded, also return warning.
 * 		- warning: (Array) of error messages like a errors	
 * 
 * 
 * Chunk merge request 
 * (When received parameters _chunkmerged, _name)
 * ==============================================
 * Extra arguments:
 * 		- upload[]: Value of _name
 * 		- chunk: Value of _chunkmerged
 * Response:
 * 		- added: (Array) of files that were successfully uploaded. Information about File/Directory	 
 */
@Slf4j
public class FileExplorerCommandForUpload 
	 extends FileExplorerCommandBase  
  implements FileExplorerCommand {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
	public FileExplorerCommandForUpload(final FileExplorerStorage storage,
										final Marshaller marshaller) {
		super(storage,
			  marshaller);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public FileExplorerCommandResponseObject execute(final HttpServletRequest req) throws ServletException,
																						  IOException {
		// [0] - Get post-ed data
		FileExplorerUploadData uploadData = new FileExplorerUploadData(req);
		log.info("[File Explorer] (upload): {}",
				 uploadData.debugInfo());
		
		// [1] - Process each uploaded file
		Collection<FileExplorerFileStoreItem> uploadedFileProperties = new ArrayList<>(uploadData.getNumberOfUploadedFiles());
		if (CollectionUtils.hasData(uploadData.getUploadedItems())) {
			for (FileExplorerUploadedItemData item : uploadData.getUploadedItems()) {
				// guess the volume
				FileExplorerVolume dstVol = item.getDstFolderVolume() != null ? item.getDstFolderVolume()
																			  : uploadData.getDstBaseFolderVolume();
				
				if (dstVol == null) throw new IllegalStateException("[File Explorer]: not enough info to guess the volume to upload the file to");
				
				// compose the destination file path
				Path dstFileBasePath = item.getDstFolderRelPathFromRoot() != null ? item.getDstFolderRelPathFromRoot()
																				  : uploadData.getDstBaseFolderRelPathFromRoot() != null ? uploadData.getDstBaseFolderRelPathFromRoot()
																									   									 : null;
				if (dstFileBasePath == null) throw new IllegalStateException("[File Explorer]: could not guess the path where the uploaded file must be written");
				
				String dstFileName = "";
				if (Strings.isNOTNullOrEmpty(item.getDstFileName())) {
					dstFileName = _sanitizeFileName(item.getDstFileName());	// sanitize the file name
				} else {
					dstFileName = _sanitizeFileName(uploadData.getChunkOfFileName());
				}
				
				Path dstFilePath = dstFileBasePath.joinedWith(dstFileName);
				Path tmpPath = Path.forId(dstVol.getTmpDir()).joinedWith(FileExplorerItemPathHash.relativePathFromRootHashOf(dstVol.getRootPath().joinedWith(dstFilePath))); // Unique
				if(uploadData.getTotalChunks() == 0 
						&& _isLast(uploadData,item)
						&& !Files.exists(tmpPath.asJavaNIOPath())) {
					// 1 chunk
					try (InputStream chunkIS = item.getUploadedFileIS()) {
	                    dstVol.write(dstFilePath, chunkIS);
	                }
					FileProperties fileProps = dstVol.getItemProperties(dstFilePath);
						uploadedFileProperties.add(FileExplorerFileStoreItem.itemBuilderForVolume(dstVol)
															 	 			.forItem(fileProps)
															 	 			.build());
				} else {
					// multiple chunk
					final boolean isFirst = (uploadData.getTotalChunks() == 1 || (uploadData.getCurrChunk() == 0 && uploadData.getTotalChunks() > 0));
					if (isFirst) {
						// first chunk!!
	                    try { 
	                    	Files.deleteIfExists(tmpPath.asJavaNIOPath());
	                    } catch (Exception ignore) {}
	                }
					// normal chunk
					try (InputStream chunkIS = item.getUploadedFileIS()) {
	                    _appendToTemp(tmpPath.asJavaNIOPath(), chunkIS);
	                }
					if (_isLast(uploadData,item)) {
						// last chunk!!
						try (InputStream fullFileIS = Files.newInputStream(tmpPath.asJavaNIOPath(), StandardOpenOption.READ)) {
	                        dstVol.write(dstFilePath, fullFileIS);
	                    }
						FileProperties fileProps = dstVol.getItemProperties(dstFilePath);
						uploadedFileProperties.add(FileExplorerFileStoreItem.itemBuilderForVolume(dstVol)
															 	 			.forItem(fileProps)
															 	 			.build());
	                    try { Files.deleteIfExists(tmpPath.asJavaNIOPath()); } catch (Exception ignore) {}
					} else {	
						FileExplorerFileStoreItem itemaux = new FileExplorerFileStoreItem();
						itemaux.setSize(item.getUploadedFileIS().readAllBytes().length);
						uploadedFileProperties.add(itemaux);
					}
				}
			}
		}
		// [99] - Return
		FileExplorerCommandResponseForUpload outResponse = new FileExplorerCommandResponseForUpload();
		
		outResponse.setAdded(uploadedFileProperties);
	
		if (uploadData.getCurrChunk() > 0 && uploadData.getTotalChunks() > 0
		 && uploadData.getCurrChunk() == uploadData.getTotalChunks()) {
			outResponse.setUploadedFileName(_sanitizeFileName(uploadData.getChunkOfFileName()));
			outResponse.setUploadedFileNameAtServer(_sanitizeFileName(uploadData.getChunkOfFileName()));
		}
		if (log.isDebugEnabled()) log.debug("[File Explorer] (upload): response\n" +
										    "{}",
										    _marshaller.forWriting()
													   .toJson(outResponse));
		return outResponse;
	}
	private boolean _isLast(FileExplorerUploadData uploadData, FileExplorerUploadedItemData item) {
		if (Strings.isNOTNullOrEmpty(item.getDstFileName())) {
			if(uploadData.getTotalChunks() == 0) {
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}
	private void _appendToTemp(java.nio.file.Path tmp, InputStream in) throws IOException {
	    Files.createDirectories(tmp.getParent());
	    try (OutputStream out = Files.newOutputStream(tmp,
	            									  Files.exists(tmp) ? StandardOpenOption.APPEND : StandardOpenOption.CREATE)) {
	        in.transferTo(out);
	    }
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	private static final Pattern HASH_ARRAY_PATTERN = Pattern.compile("hashes\\[([^]]+)\\]");
	private static final Pattern CHUNK_INFO_PATTERN = Pattern.compile("(.+)\\.(\\d+)_(\\d+)\\.part");
	
	@Accessors(prefix="_")
	private class FileExplorerUploadData
	   implements Debuggable {
		// the request id
		@Getter private String _requestId;
		
		// the base folder path
		@Getter private FileExplorerItemPathHash _dstBaseFolderPathHash;
		
		// in case of collision
		@Getter private Map<FileExplorerItemPathHash,String> _filesAtSameDstFolder;
		@Getter private String _suffixInCaseOfCollision;
		@Getter private boolean _overwrite;
		
		// chunked uploads
		@Getter private String _chunkId;
		@Getter private String _chunkOfFileName;
		@Getter private int _currChunk;
		@Getter private int _totalChunks;
		@Getter private Range<Integer> _byteRange;
		@Getter private int _fileSizeInBytes;
		
		// data:
		//		- all file(s) if they are small enough to be sent in a single part
		//		- a single file's PART if it's very big
		@Getter private final List<FileExplorerUploadedItemData> _uploadedItems;
		
				private final StringBuilder _dbg;
		
		FileExplorerUploadData(final HttpServletRequest req) throws ServletException,
																	IOException {
			// [0] - Read each part			
			String cmd = null;
			List<FileExplorerItemPathHash> dstFolderPathHashForEachUploadedFile = new ArrayList<>();
			List<String> nameForEachUploadedFile = new ArrayList<>();
			List<Long> timeStampForEachUploadedFile = new ArrayList<>();
			List<InputStream> uploadedFilesInputStreams = new ArrayList<>();
			
			_dbg = new StringBuilder();
			int i=0;
			for (Part part : req.getParts()) {
				HttpMultipartPOSTPartContentDisposition partInfo = HttpServletRequestUtils.parseHttpMultiPartPOSTPartContentDispositionHeader(part);
				
				// GENERAL-------------
				if (partInfo.getFieldName().equals(FileExplorerConstants.REQ_PARAMETER_CMD)) {
					// command
					cmd = StringPersistenceUtils.load(part.getInputStream());
					if (!cmd.equals("upload")) throw new IllegalArgumentException("[File Explorer]: received cmd=" + cmd + " but expected 'upload'");
					
					_dbg.append("-part i=").append(i).append("> ").append("cmd=").append(cmd);
				}
				else if (partInfo.getFieldName().equals(FileExplorerConstants.REQ_PARAMETER_UPLOAD_REQID)) {
					// request id
					_requestId = StringPersistenceUtils.load(part.getInputStream());
					
					_dbg.append("-part i=").append(i).append("> ").append("requestId=").append(_requestId);	
				}
				else if (partInfo.getFieldName().equals(FileExplorerConstants.REQ_PARAMETER_UPLOAD_TARGET)) {
					// base folder hash
					_dstBaseFolderPathHash = FileExplorerItemPathHash.from(StringPersistenceUtils.load(part.getInputStream()));
					
					_dbg.append("-part i=").append(i).append("> ").append("dstBaseFolderPath=").append(_dstBaseFolderPathHash);					
				} 
				
				// CONFLICT ------------
				else if (partInfo.getFieldName().startsWith("hashes[")) {
					// other files at the same folder as the one where the file is being uploaded
					FileExplorerItemPathHash otherFileAtSameFolderPathHash = null;
					String otherFileAtSameFolderName = null;
					Matcher m = HASH_ARRAY_PATTERN.matcher(partInfo.getFieldName());
					if (m.find()) { 
						otherFileAtSameFolderPathHash = FileExplorerItemPathHash.from(m.group(1));
						otherFileAtSameFolderName = StringPersistenceUtils.load(part.getInputStream());
					}
					if (otherFileAtSameFolderPathHash != null
					 && Strings.isNOTNullOrEmpty(otherFileAtSameFolderName)) {
						if (_filesAtSameDstFolder == null) _filesAtSameDstFolder = new HashMap<>();
						_filesAtSameDstFolder.put(otherFileAtSameFolderPathHash,otherFileAtSameFolderName);
					}
					_dbg.append("-part i=").append(i).append("> ").append("fileAtSameDstFolder=").append(otherFileAtSameFolderName);
				} 
				else if (partInfo.getFieldName().equals(FileExplorerConstants.REQ_PARAMETER_SUFFIX)) {
					_suffixInCaseOfCollision = StringPersistenceUtils.load(part.getInputStream());
					
					_dbg.append("-part i=").append(i).append("> ").append("suffix in case of collision=").append(_suffixInCaseOfCollision);
				} 
				else if (partInfo.getFieldName().equals(FileExplorerConstants.REQ_PARAMETER_UPLOAD_OVERWRITE)) {	
					_overwrite = _toBoolean(StringPersistenceUtils.load(part.getInputStream()));
					
					_dbg.append("-part i=").append(i).append("> ").append("overwrite=").append(_overwrite);
				}
				
				// ITEM ----------------
				else if (partInfo.getFieldName().equals(FileExplorerConstants.REQ_PARAMETER_UPLOAD_PATHS)) {
					// item: destination folder (from base) 
					FileExplorerItemPathHash dstFolderPathHashForItem = FileExplorerItemPathHash.from(StringPersistenceUtils.load(part.getInputStream()));
					dstFolderPathHashForEachUploadedFile.add(dstFolderPathHashForItem);
					
					_dbg.append("-part i=").append(i).append("> ").append("dstFolderPathHash=").append(dstFolderPathHashForItem.getRelativePathFromRoot());
				} 
				else  if (partInfo.getFieldName().equals(FileExplorerConstants.REQ_PARAMETER_UPLOAD_NAMES)) {
					// item: destination file name
					String nameForUploadedFile = StringPersistenceUtils.load(part.getInputStream());
					nameForEachUploadedFile.add(nameForUploadedFile);
					
					_dbg.append("-part i=").append(i).append("> ").append("name for item=").append(nameForUploadedFile);
				} 
				else  if (partInfo.getFieldName().equals(FileExplorerConstants.REQ_PARAMETER_UPLOAD_TIMESTAMPS)) {
					// item: destination file timestamp
					String tsForUploadedFileStr = StringPersistenceUtils.load(part.getInputStream());
					long tsForUploadedFile = Strings.isNOTNullOrEmpty(tsForUploadedFileStr) 
										  && !tsForUploadedFileStr.equalsIgnoreCase("UNDEFINED") ? Long.parseLong(tsForUploadedFileStr) : -1;
					timeStampForEachUploadedFile.add(tsForUploadedFile);
					
					_dbg.append("-part i=").append(i).append("> ").append("timestamp for item=").append(tsForUploadedFile);
				}
				
				// CHUNK -------------
				else if (partInfo.getFieldName().equals(FileExplorerConstants.REQ_PARAMETER_UPLOAD_CHUNK_ID)) {
					// chunk id
					_chunkId = StringPersistenceUtils.load(part.getInputStream());
					
					_dbg.append("-part i=").append(i).append("> ").append("chunkId=").append(_chunkId);	
				} 
				else if (partInfo.getFieldName().equals(FileExplorerConstants.REQ_PARAMETER_UPLOAD_RANGE)) {
					// chunk range [start byte],[Chunk length],[total bytes]
					String chunkRangeStr = StringPersistenceUtils.load(part.getInputStream());
					Integer[] start_end_total = StringSplitter.using(Splitter.on(","))
														  	  .at(chunkRangeStr)
														  	  .stream()
														  	  .map(Integer::valueOf)
														  	  .toArray(Integer[]::new);
					_byteRange = Range.closed(start_end_total[0],start_end_total[0] + start_end_total[1]);
					_fileSizeInBytes = start_end_total[2];
					
					_dbg.append("-part i=").append(i).append("> ").append("range=").append(_byteRange).append(" of ").append(_fileSizeInBytes);
				} 
				else if (partInfo.getFieldName().equals(FileExplorerConstants.REQ_PARAMETER_UPLOAD_CHUNK)) {
					// chunk data: [file name].{part num}_{total parts}.part
					String chunkInfoStr = StringPersistenceUtils.load(part.getInputStream());
					Matcher m = CHUNK_INFO_PATTERN.matcher(chunkInfoStr);
					if (m.find()) {
						_chunkOfFileName = m.group(1);
						_currChunk = Integer.valueOf(m.group(2));
						_totalChunks = Integer.valueOf(m.group(3));
						
						nameForEachUploadedFile.add(_chunkOfFileName);
						
						_dbg.append("-part i=").append(i).append("> ").append("chunk=").append(_currChunk).append(" of ").append(_totalChunks).append(" file=").append(_chunkOfFileName);
					} else {
						// last chunk: just contains the file name
						_chunkOfFileName = chunkInfoStr;
						_dbg.append("-part i=").append(i).append("> ").append("last chunk of ").append(_chunkOfFileName);
					}
				}
				
				// ITEM STREAM -------
				else if (partInfo.isFilePart()
					  || partInfo.getFieldName().equals(FileExplorerConstants.REQ_PARAMETER_UPLOAD_FILES)) {	// upload[]
					// item: original file name & stream
					String originalFileName = partInfo.getFileName();
					nameForEachUploadedFile.add(originalFileName);
					
					uploadedFilesInputStreams.add(part.getInputStream());
					
					_dbg.append("-part i=").append(i).append("> ").append("stream for item=").append(originalFileName);
				}
				
				// UNKNOWN PART ------
				else {
					log.warn("[File Explorer] (upload) {} request parameter is NOT SUPPORTED! -ignored-",
							 partInfo.getFieldName());
					_dbg.append("-part i=").append(i).append("> UNSUPPORTED > ").append(" fieldName=").append(partInfo.getFieldName()).append(" value=").append(StringPersistenceUtils.load(part.getInputStream()));
				}
				i++;	// next part
				_dbg.append("\n");
			}
			
			// [1] - Accumulate to a FileExplorerUploadedItemData Collection
			_uploadedItems = new ArrayList<>(uploadedFilesInputStreams.size());
			for (int j=0; j < uploadedFilesInputStreams.size(); j++) {
				_uploadedItems.add(new FileExplorerUploadedItemData(// file stream
																	uploadedFilesInputStreams.get(j),
																	// file name (in chunked uploads the filename at the chunk is always "blob"
																	//			  ...so use the field _chunkOfFileName instead)
																	_chunkId != null ? _chunkOfFileName
																					 : CollectionUtils.of(nameForEachUploadedFile)
																					 				  .pickElementAtOrNull(j),
																	// file time-stamp
																	CollectionUtils.of(timeStampForEachUploadedFile)
																				   .pickElementAtOrNull(j),
																	// destination folder hash
																	CollectionUtils.of(dstFolderPathHashForEachUploadedFile)
																				   .pickElementAtOrNull(j)));
			}
		}
		public int getNumberOfUploadedFiles() {
			return _uploadedItems != null ? _uploadedItems.size() : 0;
		}
		public FileExplorerVolume getDstBaseFolderVolume() {
			return _dstBaseFolderPathHash != null ? _storage.getVolumeFor(_dstBaseFolderPathHash) : null;
		}
		public Path getDstBaseFolderRelPathFromRoot() {
			return _dstBaseFolderPathHash != null ? _dstBaseFolderPathHash.getRelativePathFromRoot() : null;
		}
		@Override
		public CharSequence debugInfo() {
			StringBuilder dbg = new StringBuilder();
			dbg.append(Strings.customized("chunk {}/{} file={} range={} of {} > {} parts received:\n",
										  _currChunk,_totalChunks,
										  _chunkOfFileName,
										  _byteRange,_fileSizeInBytes,
										  _uploadedItems != null ? _uploadedItems.size() : 0))
			   .append(_dbg).append("\n");
			if (_uploadedItems != null) {
				for (FileExplorerUploadedItemData uploadedItem : _uploadedItems) {
					dbg.append("\t- ").append(uploadedItem.debugInfo()).append("\n");
				}
			}
			return dbg;
		}
	}
	@Accessors(prefix="_")
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	private class FileExplorerUploadedItemData
	   implements Debuggable {
		
		@Getter private final InputStream _uploadedFileIS;
		@Getter private final String _dstFileName;
		@Getter private final Long _dstTimeStamp;
		@Getter private final FileExplorerItemPathHash _dstFolderPathHash;
		
		public FileExplorerVolume getDstFolderVolume() {
			return _dstFolderPathHash != null ? _storage.getVolumeFor(_dstFolderPathHash) : null;
		}
		public Path getDstFolderRelPathFromRoot() {
			return _dstFolderPathHash != null ? _dstFolderPathHash.getRelativePathFromRoot() : null;
		}
		@Override
		public CharSequence debugInfo() {
			StringBuilder dbg = new StringBuilder();
			dbg.append(Strings.customized("fileName={} written to folder={} with timeStamp={}",
										  _dstFileName,
										  _dstFolderPathHash != null ? _dstFolderPathHash.getRelativePathFromRoot() : null,
										  _dstTimeStamp));
			return dbg;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Sanitizes the file name removing illegal chars
	 * @param fileNameAndExtensionStr
	 * @return
	 */
	private String _sanitizeFileName(final String fileNameAndExtensionStr) {
		if (fileNameAndExtensionStr == null) return null;
		if (_storage.getFileNameSanitizer() == null) return fileNameAndExtensionStr;	// no sanitizer
		
		FileNameAndExtension fileNameAndExtension = FileNameAndExtension.of(fileNameAndExtensionStr);
		String sanitizedFileName = _storage.getFileNameSanitizer()
										   .sanitizeName(fileNameAndExtension.getNameWithoutExtension());
		String sanitizedExtension = _storage.getFileNameSanitizer()
											.sanitizeExtension(fileNameAndExtension.getExtension());
		return Strings.isNOTNullOrEmpty(sanitizedExtension) ? sanitizedFileName + "." + sanitizedExtension
															: sanitizedFileName;
	}
}
