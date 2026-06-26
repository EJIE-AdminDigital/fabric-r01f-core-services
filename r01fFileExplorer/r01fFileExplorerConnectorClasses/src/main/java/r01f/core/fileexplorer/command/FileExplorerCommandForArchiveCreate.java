package r01f.core.fileexplorer.command;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import r01f.core.fileexplorer.FileExplorerConstants;
import r01f.core.fileexplorer.FileExplorerItemPathHash;
import r01f.core.fileexplorer.FileExplorerStorage;
import r01f.core.fileexplorer.FileExplorerVolume;
import r01f.core.fileexplorer.command.response.FileExplorerCommandResponseForArchiveCreate;
import r01f.core.fileexplorer.command.response.FileExplorerCommandResponseObject;
import r01f.core.fileexplorer.command.response.FileExplorerFileStoreItem;
import r01f.file.FileProperties;
import r01f.file.util.Folders;
import r01f.file.util.ZipFiles;
import r01f.file.util.ZipFiles.ThrowingInputStreamFromPathSupplier;
import r01f.mime.MimeType;
import r01f.mime.MimeTypes;
import r01f.objectstreamer.Marshaller;
import r01f.patterns.ThrowingFunction;
import r01f.types.Path;
import r01f.util.types.Strings;

/**
 * see: https://github.com/Studio-42/elFinder/wiki/Client-Server-API-2.1#archive
 * 
 * Packs directories / files into an archive.
 * 
 * Arguments:
 * 		- cmd: archive
 *		- type: mime-type for the archive
 * 		- name: file name of the archive to create
 *		- target: hash of the directory where the created zip file is created
 *		- targets[]: an array of hashes of the directories / files to archive
 * Response:
 *		- added: (Array) Information about File/Directory of the archive			 
 */
@Slf4j
public class FileExplorerCommandForArchiveCreate 
	 extends FileExplorerCommandBase 
  implements FileExplorerCommand {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
	public FileExplorerCommandForArchiveCreate(final FileExplorerStorage storage,
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
		// get the type of the archive file
		String archiveMimeTypeStr = req.getParameter(FileExplorerConstants.REQ_PARAMETER_TYPE);
		MimeType archiveMimeType = MimeTypes.forName(archiveMimeTypeStr);
		if (archiveMimeType.isNOT(MimeTypes.APPLICATION_ZIP)) throw new IllegalArgumentException(archiveMimeType + " is NOT supported!! Just " + MimeTypes.APPLICATION_ZIP + " is supported!");
		
		// path hash of the files / folder to be added to the zip file
		Collection<FileExplorerItemPathHash> itemsToBeArchivedPathsHashes = FileExplorerItemPathHash.multipleFrom(req);
		
		// path hash of the folder where the archive file is created 
		FileExplorerItemPathHash targetFolderPathHash = FileExplorerItemPathHash.from(req);

		// name of the archive file
		String archiveFileName = req.getParameter(FileExplorerConstants.REQ_PARAMETER_NAME);
		
		// execute
		FileExplorerCommandResponseObject outResponse = this.execute(itemsToBeArchivedPathsHashes,
									   								 targetFolderPathHash,
									   								 archiveFileName,
									   								 req);
		return outResponse;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EXPOSED FOR TESTING PURPOSES
/////////////////////////////////////////////////////////////////////////////////////////
	public FileExplorerCommandResponseForArchiveCreate execute(final Collection<FileExplorerItemPathHash> itemsToBeArchivedPathsHashes,
							  				  		 		   final FileExplorerItemPathHash targetFolderPathHash,
							  				  		 		   final String archiveFileName,
							  				  		 		   final HttpServletRequest req) throws IOException {
		// volume & the file store apis
		FileExplorerVolume vol = _storage.getVolumeFor(targetFolderPathHash.getVolumeId());
		
		// create an Stream of files to be zipped
		Stream<Path> filesToBeArchivedStream = itemsToBeArchivedPathsHashes.stream()
													   					   .flatMap(ThrowingFunction.unchecked(
															   							   itemPathHash -> {
															   						   			Path itemRelPathFromRoot = itemPathHash.getRelativePathFromRoot();
															   						   			Path itemAbsPath = vol.getAbsolutePathOf(itemPathHash.getRelativePathFromRoot()); 	// beware! this is ABSOLUTE path
															   						   			FileProperties item = vol.getItemProperties(itemRelPathFromRoot);													   						   			
															   						   			Stream<Path> outPathStream = null;
															   						   			if (item.isFile()) {
															   						   				// a file
															   						   				outPathStream = Stream.of(itemAbsPath);
															   						   			} else {
															   						   				// a folder: recurse!!
															   						   				outPathStream = Folders.createStreamOfFilesAt(itemAbsPath)
															   						   									   .doNOTUserFileFilter()
															   						   									   .withoutFolderDepthLimit()
															   						   									   .using(vol.getFsMediator().getFileStoreAPI(),vol.getFsMediator().getFileStoreFilerAPI())
															   						   									   .build();
															   						   			}
															   						   			return outPathStream;
															   							   }));	
		
		// create the ZIP file
		Path archiveContainerFolderRelPath = vol.absolutePathOf(targetFolderPathHash);
		Path archiveFileRelPath = archiveContainerFolderRelPath.joinedWith(archiveFileName);
		
		String reqid = req.getParameter("reqid")!=null ? req.getParameter("reqid") : "noreqid";
		
		Path localPath = Path.forId(vol.getTmpDir()).joinedWith(Strings.concat(reqid, 
																			   "_", 
																			   FileExplorerItemPathHash.relativePathFromRootHashOf(archiveFileRelPath).asString())); // Unique
		Files.createDirectories(localPath.asJavaNIOPath());
		
		ZipFiles.createZipFileAt(localPath)
				.withFiles(filesToBeArchivedStream)
				.trimPathsInsideZipFile(archiveContainerFolderRelPath)
				.usingSourceInputStreamSupplier(// InputStreamSupplier BEWARE! vol requires relative paths from root
												//							   but zipfiles uses absolute paths
												ThrowingInputStreamFromPathSupplier.ignored(path -> vol.openInputStream(vol.getRelativePathFromRootOf(path))))
				.useParallelZipArchiverUsingDefaultExecutorService()
				.storeTempFilesAtDefaultSystemLocation()
				.start();
		
		try (InputStream fullFileIS = Files.newInputStream(localPath.asJavaNIOPath(), StandardOpenOption.READ)) {
			vol.write(archiveFileRelPath, fullFileIS);
        }
		try { Files.deleteIfExists(localPath.asJavaNIOPath()); } catch (Exception ignore) {}
		
		// return as json
		FileProperties archiveFile = vol.getItemProperties(archiveFileRelPath);
		FileExplorerCommandResponseForArchiveCreate outResponse = new FileExplorerCommandResponseForArchiveCreate();																
		outResponse.setAdded(List.of(FileExplorerFileStoreItem.itemBuilderForVolume(vol)
								 							  .forItem(archiveFile)
								 							  .build()));
		return outResponse;
	}
}
