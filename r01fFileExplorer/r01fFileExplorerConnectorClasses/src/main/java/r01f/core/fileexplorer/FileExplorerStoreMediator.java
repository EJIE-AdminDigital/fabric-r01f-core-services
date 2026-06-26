package r01f.core.fileexplorer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01.filestore.api.s3.S3FileStoreAPI;
import r01.filestore.api.s3.S3FileStoreFilerAPI;
import r01f.core.fileexplorer.config.FileExplorerStoreConfig;
import r01f.core.fileexplorer.config.FileExplorerStoreConfigForHDFS;
import r01f.core.fileexplorer.config.FileExplorerStoreConfigForS3;
import r01f.file.FileNameAndExtension;
import r01f.file.FileProperties;
import r01f.filestore.api.FileFilter;
import r01f.filestore.api.FileStoreAPI;
import r01f.filestore.api.FileStoreFilerAPI;
import r01f.filestore.api.FileStoreType;
import r01f.filestore.api.hdfs.HDFSFileStoreAPI;
import r01f.filestore.api.hdfs.HDFSFileStoreFilerAPI;
import r01f.filestore.api.local.LocalFileStoreAPI;
import r01f.filestore.api.local.LocalFileStoreFilerAPI;
import r01f.mime.MimeType;
import r01f.mime.MimeTypes;
import r01f.types.Path;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;
import r01f.util.types.collections.Lists;

@Slf4j
@Accessors(prefix="_")
public class FileExplorerStoreMediator {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter protected final FileStoreAPI _fileStoreAPI;
	@Getter protected final FileStoreFilerAPI _fileStoreFilerAPI;
	
	@Getter protected final Path _storeRootPath;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public FileExplorerStoreMediator(final FileExplorerStoreConfig storeConfig) throws IOException {
		this(_createFileStoreAPIUsing(storeConfig),_createFileStoreFilerAPIUsing(storeConfig),
			 storeConfig.getFsRootFullPath());
	}
	public FileExplorerStoreMediator(final FileStoreAPI fileStoreAPI,final FileStoreFilerAPI fileStoreFilerAPI,
									 final Path storeRootPath) {
		_fileStoreAPI = fileStoreAPI;
		_fileStoreFilerAPI = fileStoreFilerAPI;
		
		_storeRootPath = storeRootPath;
	}
	private static FileStoreAPI _createFileStoreAPIUsing(final FileExplorerStoreConfig storeConfig) throws IOException {
		FileStoreAPI fileStoreApi = null;
		if (storeConfig.getType().is(FileStoreType.LOCAL)) {
			fileStoreApi = new LocalFileStoreAPI();
		}
		else if (storeConfig.getType().is(FileStoreType.HDFS)) {
			FileExplorerStoreConfigForHDFS hdfsConf = (FileExplorerStoreConfigForHDFS)storeConfig;
			fileStoreApi = new HDFSFileStoreAPI(hdfsConf.getHdfsConf(),
												hdfsConf.getHdfsCredentialsRefreshPeriod());
		}
		else if (storeConfig.getType().is(FileStoreType.S3)) {
			FileExplorerStoreConfigForS3 s3Conf = (FileExplorerStoreConfigForS3)storeConfig;
			fileStoreApi = new S3FileStoreAPI(s3Conf.getS3Config(),s3Conf.getS3BucketConfig());
		}
		else {
			throw new IllegalStateException(storeConfig.getType() + " is NOT a supported file-store type");
		}
		return fileStoreApi;
	}
	private static FileStoreFilerAPI _createFileStoreFilerAPIUsing(final FileExplorerStoreConfig storeConfig) throws IOException {
		FileStoreFilerAPI fileStoreFilerApi = null;
		if (storeConfig.getType().is(FileStoreType.LOCAL)) {
			fileStoreFilerApi = new LocalFileStoreFilerAPI();
		}
		else if (storeConfig.getType().is(FileStoreType.HDFS)) {
			FileExplorerStoreConfigForHDFS hdfsConf = (FileExplorerStoreConfigForHDFS)storeConfig;
			fileStoreFilerApi = new HDFSFileStoreFilerAPI(hdfsConf.getHdfsConf(),
														  hdfsConf.getHdfsCredentialsRefreshPeriod());
		}
		else if (storeConfig.getType().is(FileStoreType.S3)) {
			FileExplorerStoreConfigForS3 s3Conf = (FileExplorerStoreConfigForS3)storeConfig;
			fileStoreFilerApi = new S3FileStoreFilerAPI(s3Conf.getS3Config(),s3Conf.getS3BucketConfig());
		}
		else {
			throw new IllegalStateException(storeConfig.getType() + " is NOT a supported file-store type");
		}
		return fileStoreFilerApi;
	}
	public static FileExplorerStoreMediator createUsing(final FileExplorerStoreConfig storeConfig) throws IOException {
		return new FileExplorerStoreMediator(storeConfig);
	}
	public static FileExplorerStoreMediator createUsing(final FileStoreAPI fileStoreAPI,final FileStoreFilerAPI fileStoreFilerAPI,
														final Path storeRootPath) {
		return new FileExplorerStoreMediator(fileStoreAPI,fileStoreFilerAPI,
											 storeRootPath);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	STORE TYPE
/////////////////////////////////////////////////////////////////////////////////////////
	public FileStoreType getStoreType() {
		return _fileStoreAPI.getStoreType();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	OPERATIONS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Creates a new and empty file.
	 * @param path the path to the file to create.
	 * @throws IOException if something goes wrong.
	 */
	public void createFile(final Path path) throws IOException {
		log.trace("[file explorer]: create file {}",path);
		_fileStoreAPI.writeToFile(new ByteArrayInputStream(new byte[0]),
								  path,
								  true);		// overwrite
	}
	/**
	 * Creates a folder by creating all nonexistent parent directories first.
	 * @param path the directory to create
	 * @throws IOException if something goes wrong.
	 */
	public void createFolder(final Path path) throws IOException {
		log.trace("[file explorer]: create folder {}",path);
		_fileStoreFilerAPI.createFolder(path);
	}
	/**
	 * Deletes a file if it exists.
	 * @param path the path to the file to delete.
	 * @throws IOException if something goes wrong.
	 */
	public void deleteFile(final Path path) throws IOException {
		log.trace("[file explorer]: delete file {}",path);
		_fileStoreAPI.deleteFile(path);
	}
	public void deleteFolder(final Path path) throws IOException {
		log.trace("[file explorer]: delete folder {}",path);
		_fileStoreFilerAPI.deleteFolder(path);
	}
	/**
	 * Renames the origin path denoted by the new destination path.
	 * @param srcPath origin path to be renamed.
	 * @param dstPath The new destination path for the named path
	 * @throws IOException if something goes wrong.
	 */
	public void rename(final Path srcPath,final Path dstPath) throws IOException {
		log.trace("[file explorer]: rename {} to {}",srcPath,dstPath);
		FileProperties src = this.getFileProperties(srcPath);
		if (!src.isFolder()) {
			_fileStoreAPI.renameFile(srcPath,
									 dstPath);
		} else {
			FileNameAndExtension newName = new FileNameAndExtension(dstPath.getLastPathElement());
			_fileStoreFilerAPI.renameFolder(srcPath,
											newName);
		}
	}
	/**
	 * Tests whether a file exists.
	 * @param path the path to the file to test.
	 * @return true if the file exists; false if the file does not exist or its
	 * existence cannot be determined.
	 */
	public boolean existsFile(final Path path) throws IOException {
		log.trace("[file explorer]: exists file at {}",path);
		return _fileStoreAPI.existsFile(path);
	}
	/**
	 * Tests whether a file exists.
	 * @param path the path to the file to test.
	 * @return true if the file exists; false if the file does not exist or its
	 * existence cannot be determined.
	 */
	public boolean existsFolder(final Path path) throws IOException {
		log.trace("[file explorer]: exists folder at {}",path);
		return _fileStoreFilerAPI.existsFolder(path);
	}
	/**
	 * Copies a file to another
	 * @param srcFileId
	 * @param dstFileId
	 * @param overwrite false, if file exists don't copy
	 * 					true, if file exists overwrite
	 * @return
	 * @throws IOException
	 */
	public boolean copyFile(final Path srcPath,
							final Path dstPath,
							final boolean overwrite) throws IOException {
		log.trace("[file explorer]: Copying from {} to {}", srcPath, dstPath);
		return _fileStoreAPI.copyFile(srcPath, dstPath, overwrite);
	}
	/**
	 * Copies a folder
	 * @param srcPath
	 * @param dstPath
	 * @param fileFilter
	 * @return boolean
	 * @throws IOException
	 */
	public boolean copyFolder(final Path srcPath,
							  final Path dstPath,
							  final FileFilter fileFilter,
							  final boolean overwrite) throws IOException {
		log.trace("[file explorer]: Copying from {} to {}", srcPath, dstPath);
		return _fileStoreFilerAPI.copyFolder(srcPath, dstPath, fileFilter, overwrite);
	}
	/**
	 * Copies a folder
	 * @param srcPath
	 * @param dstPath
	 * @return boolean
	 * @throws IOException
	 */
	public boolean moveFolder(final Path srcPath,
							  final Path dstPath,
							  final boolean overwrite) throws IOException {
		log.trace("[file explorer]: Moving from {} to {}", srcPath, dstPath);
		return _fileStoreFilerAPI.moveFolder(srcPath, dstPath, overwrite);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	META-DATA
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns file properties
	 * @param path the path to the file.
	 * @return file properties from file
	 * @throws IOException if something goes wrong.
	 */
	public FileProperties getFileProperties(final Path path) throws IOException {
		log.trace("[file explorer]: get file properties of file at {}",path);
		FileProperties props = null;
		props = _fileStoreAPI.getFileProperties(path);
		return props;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	LIST
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Tests if the given directory has child folders.
	 * @param path path to the directory to test.
	 * @return true if it has child folders, otherwise false.
	 * @throws IOException if something goes wrong.
	 */
	public boolean hasChildFolder(final Path path) throws IOException {
		log.trace("[file explorer]: has child folder {}",path);
		return _fileStoreFilerAPI.hasSubfolder(path);
	}
	/**
	 * Gets children FileProperties from the give directory appling the given filter.
	 * @param path path to the directory.
	 * @return the children paths from the give directory.
	 * @throws IOException if something goes wrong.
	 */
	public Collection<FileProperties> listChildren(final Path path) throws IOException {
		return this.listChildren(path,
								 null);		// no filter
	}
	/**
	 * Gets children FileProperties from the give directory appling the given filter.
	 * @param path path to the directory.
	 * @param filter the filter to be applied
	 * @return the children paths from the give directory.
	 * @throws IOException if something goes wrong.
	 */
	public Collection<FileProperties> listChildren(final Path path,
										 		   final FileFilter fileFilter) throws IOException {
		log.trace("[file explorer]: list children file properties {}",path);

		// list the children
		FileProperties[] children = _fileStoreFilerAPI.listFolderContents(path,
																		  fileFilter,	// filter
																		  false);		// recursive	
		// transform to a collection of FileProperties
		return List.of(children);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	CONTENT
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Opens a file, returning an input stream to read from the file.
	 * @param path the path to the file to open.
	 * @return a new input stream.
	 * @throws IOException if something goes wrong.
	 */
	public InputStream openInputStream(final Path path) throws IOException {
		log.trace("[file explorer]: open input stream {}",path);
		return _fileStoreAPI.readFromFile(path);
	}
	/**
	 * Opens or creates a file, returning an output stream that may be used to write bytes to the file.
	 * @param path the path to the file to open or create.
	 * @return a new output stream
	 * @throws IOException if something goes wrong.
	 */
	public OutputStream openOutputStream(final Path path) throws IOException {
		log.trace("[file explorer]: open output stream {}",path);
		return _fileStoreAPI.getFileOutputStreamForWriting(path,
														   true);	// overwrite
	}
	/**
	 * Opens or creates a file, returning an output stream that may be used to write bytes to the file.
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public OutputStream openOutputStreamForAppending(final Path path) throws IOException {
		log.trace("[file explorer]: open output stream {}",path);
		return _fileStoreAPI.getFileOutputStreamForAppending(path);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	READ / WRITE
/////////////////////////////////////////////////////////////////////////////////////////
//	/**
//	 * Opens a file, returning an input stream to read from the file.
//	 * @param path the path to the file to open.
//	 * @return a new input stream.
//	 * @throws IOException if something goes wrong.
//	 */
//	public InputStream read(final Path path) throws IOException {
//		log.trace("[file explorer]: open input stream {}",path);
//		return _fileStoreAPI.readFromFile(path);
//	}
	/**
	 * Create or overwrite file
	 * @param path the path to the file to create.
	 * @param srcIS inputStream to the file
	 * @return a new output stream
	 * @throws IOException if something goes wrong.
	 */
	public void write(final Path path,
					  final InputStream srcIS) throws IOException {
		log.trace("[file explorer]: overwrite the file {}",path);
		_fileStoreAPI.writeToFile(srcIS,path,true);	// overwrite
	}
	/**
	 * Append to existing file.
	 * @param path the path to the file to append.
	 * @param srcIS inputStream to the file
	 * @return a new output stream
	 * @throws IOException if something goes wrong.
	 */
	public void append(final Path path,
					   final InputStream srcIS) throws IOException {
		log.trace("[file explorer]: append to the file {}",path);
		_fileStoreAPI.appendToFile(srcIS,path);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	SEARCH
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Searches a given path to get the given target.
	 * @param path the path to be search.
	 * @param query the target.
	 * @return a list of the found paths that contains the target string.
	 * @throws IOException if something goes wrong.
	 */
	public Collection<FileProperties> search(final Path startingPath,
											 final String query,final Collection<MimeType> mimes) throws IOException {
		log.trace("[file explorer]: search {} for query={} / mime-types={}",
				  startingPath,query,mimes);
		Collection<FileProperties> outResultFiles = Lists.newArrayList();
		_search(startingPath,
				query,mimes,
				outResultFiles);
		return outResultFiles;
	}
	private void _search(final Path startingPath,
						 final String query,final Collection<MimeType> mimes,
						 final Collection<FileProperties> outFiles) throws IOException {
		log.trace("\t-recurse {}",startingPath);
		
		Collection<FileProperties> folderChild = this.listChildren(startingPath);
		for (FileProperties file : folderChild) {
			if (file.isFolder()) {
				// BEWARE!! recursion!
				_search(file.getPath(),
						query,mimes,
						outFiles);	
			} else if (Strings.isNOTNullOrEmpty(query) && !query.equals(".")
					&& file.getPath()
						   .getFileNameWithExtension()
						   .asString().contains(query.toLowerCase())) {
				outFiles.add(file);
			} else if (CollectionUtils.hasData(mimes)) {
				MimeType fileMime = MimeTypes.fromFileSimpleName(file.getPath().getFileName());
				if (mimes.contains(fileMime)) outFiles.add(file);
			}
		}
	}
}
