package r01f.core.fileexplorer.command;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import r01f.core.fileexplorer.FileExplorerConstants;
import r01f.core.fileexplorer.FileExplorerItemPathHash;
import r01f.core.fileexplorer.FileExplorerStorage;
import r01f.core.fileexplorer.FileExplorerVolume;
import r01f.core.fileexplorer.command.response.FileExplorerCommandResponseForArchiveExtract;
import r01f.core.fileexplorer.command.response.FileExplorerCommandResponseObject;
import r01f.core.fileexplorer.command.response.FileExplorerFileStoreItem;
import r01f.file.FileNameAndExtension;
import r01f.file.FileProperties;
import r01f.file.util.ZipFiles;
import r01f.objectstreamer.Marshaller;
import r01f.patterns.ThrowingFunction;
import r01f.types.Path;
import r01f.util.types.collections.CollectionUtils;

/**
 * see: https://github.com/Studio-42/elFinder/wiki/Client-Server-API-2.1#extract
 * 
 * Unpacks an archive.
 * 
 * Arguments:
 * 		- cmd: extract
 *		- target: hash of the archive file
 *		- makedir: "1" to extract to new directory
 * Response:
 *		- added: (Array) Information about File/Directory of extracted items			 
 */
public class FileExplorerCommandForArchiveExtract 
	 extends FileExplorerCommandBase 
  implements FileExplorerCommand {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
	public FileExplorerCommandForArchiveExtract(final FileExplorerStorage storage,
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
		// get hash of the archive file
		FileExplorerItemPathHash archiveFilePathHash = FileExplorerItemPathHash.from(req);
		
		// create a new directory 
		boolean makeDir = _toBoolean(req.getParameter(FileExplorerConstants.REQ_PARAMETER_MAKEDIR));

		// create the returned list
		FileExplorerCommandResponseObject outResponse = this.execute(archiveFilePathHash,
																	 makeDir);
		return outResponse;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EXPOSED FOR TESTING PURPOSES
/////////////////////////////////////////////////////////////////////////////////////////
	public FileExplorerCommandResponseForArchiveExtract execute(final FileExplorerItemPathHash archiveFilePathHash,
							  				  		 			final boolean makeDir) throws IOException {
		FileExplorerVolume vol = _storage.getVolumeFor(archiveFilePathHash);
		
		// unzip
		Path archiveFileRelPath = archiveFilePathHash.getRelativePathFromRoot();
		InputStream zipFileIS = vol.openInputStream(archiveFileRelPath);
		
		// the folder where the file is going to be extracted
		FileNameAndExtension archiveFileName = archiveFileRelPath.getFileNameWithExtension();
		Path extractToFolderRelPath = makeDir ? archiveFileRelPath.getParentFolderPath().joinedWith(archiveFileName.getNameWithoutExtension())
										   	  : archiveFileRelPath.getParentFolderPath();
		if (!vol.existsFile(extractToFolderRelPath)) {	// the folder does NOT exists
			vol.createFolder(extractToFolderRelPath);	// ensure the folder exists
		} else {
			// the folder exists: find a new file name
			Collection<String> archiveContainerFolderChild = vol.listChildren(archiveFileRelPath.getParentFolderPath())
																.stream()
																.map(itemProps -> itemProps.getPath().getLastPathElement())
																.toList();
			String folderName = _fileNameBeingAwareOfNameCollision(extractToFolderRelPath.getLastPathElement(),true,	// folder
																   archiveContainerFolderChild,
																   FileExplorerConstants.DEFAULT_SUFFIX_FOR_NAME_COLLISION,0);
			
			extractToFolderRelPath = archiveFileRelPath.getParentFolderPath().joinedWith(folderName);
			vol.createFolder(extractToFolderRelPath);	// ensure the folder exists
		}
		
		// extract the file
		Collection<Path> extractedFilesPaths = ZipFiles.using(vol.getFsMediator()
																 .getFileStoreAPI(),
															  vol.getFsMediator()
															  	 .getFileStoreFilerAPI())
													   .unzip(zipFileIS,vol.getAbsolutePathOf(extractToFolderRelPath));	// BEWARE! absolute path
		
		// create folder if it's necessary
		if (CollectionUtils.hasData(extractedFilesPaths)) {
			extractedFilesPaths.stream()
							   .map(Path::getFolderPath)
							   .distinct()
							   .forEach(t -> {
									try {
										vol.createFolder(t);
									} catch (IOException e) {
										e.printStackTrace();
									}
							});
		}
		// the list of the extracted files
		Collection<FileProperties> extractedFiles = makeDir ? List.of(vol.getItemProperties(extractToFolderRelPath))
															: CollectionUtils.hasData(extractedFilesPaths)
																? extractedFilesPaths.stream()
																					 .map(ThrowingFunction.unchecked(absPath -> {
																						 								Path relPath = vol.getRelativePathFromRootOf(absPath);
																						 								return vol.getItemProperties(relPath);	 
																					 								}))
																					 .toList()
																: Collections.emptyList();
		// return as json
		FileExplorerCommandResponseForArchiveExtract outResponse = new FileExplorerCommandResponseForArchiveExtract();
		outResponse.setAdded(FileExplorerFileStoreItem.itemBuilderForVolume(vol)
				 						  			  .forItems(extractedFiles)
				 						  			  .build());
		return outResponse;
	}
}
