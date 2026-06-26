package r01f.core.fileexplorer.command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import r01f.core.fileexplorer.FileExplorerConstants;
import r01f.core.fileexplorer.FileExplorerItemPathHash;
import r01f.core.fileexplorer.FileExplorerStorage;
import r01f.core.fileexplorer.FileExplorerVolume;
import r01f.core.fileexplorer.command.response.FileExplorerCommandResponseForPaste;
import r01f.core.fileexplorer.command.response.FileExplorerCommandResponseObject;
import r01f.core.fileexplorer.command.response.FileExplorerFileStoreItem;
import r01f.file.FileProperties;
import r01f.objectstreamer.Marshaller;
import r01f.types.Path;
import r01f.util.types.Strings;

/**
 * see: https://github.com/Studio-42/elFinder/wiki/Client-Server-API-2.1#paste
 * 
 * Copies or moves a directory / files
 * 
 * Arguments:
 * 		- cmd: paste
 * 		- dst: hash of the directory to which the files will be copied / moved (the destination)
 *		- targets[]: An array of hashes for the files to be copied / moved
 *		- cut: 1 if the files are moved, missing if the files are copied
 *		- suffix: Suffixes during rename (default is "~")
 *		- renames[]: Filename list of rename request
 * Response:
 *		- added: (Array) array of file and directory objects pasted. Information about File/Directory
 *		- removed : (Array) array of file and directory 'hashes' that were successfully deleted
 * Caution
 * 		- If the file name of the rename list exists in the directory, 
 * 		  the command should rename the file to "filename + suffix"
 *		- The command should stop copying at the first error. 
 *		  It is not allowed to overwrite files / directories with the same name. 
 *		  But the behavior of this command depends on some options on connector 
 *		  (if the user uses the default one). 
 *		  Please, take look the options:
 *				- https://github.com/Studio-42/elFinder/wiki/Connector-configuration-options-2.1#copyoverwrite
 * 				- https://github.com/Studio-42/elFinder/wiki/Connector-configuration-options-2.1#copyjoin
 */
@Slf4j
public class FileExplorerCommandForPaste 
	 extends FileExplorerCommandBase 
  implements FileExplorerCommand {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
	public FileExplorerCommandForPaste(final FileExplorerStorage storage,
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
		// Parameters
		Collection<FileExplorerItemPathHash> copiedItemsPathHashes = FileExplorerItemPathHash.multipleFrom(req);
		
		FileExplorerItemPathHash dstFolderPathHash = FileExplorerItemPathHash.from(req,FileExplorerConstants.REQ_PARAMETER_DESTINATION);
		
		String suffixForNameCollision = req.getParameter(FileExplorerConstants.REQ_PARAMETER_SUFFIX);
		if (Strings.isNullOrEmpty(suffixForNameCollision)) suffixForNameCollision = FileExplorerConstants.DEFAULT_SUFFIX_FOR_NAME_COLLISION;
		
		boolean cut = _toBoolean(req.getParameter(FileExplorerConstants.REQ_PARAMETER_CUT));
		
		// create the returned data
		FileExplorerCommandResponseObject cmdResponse = this.execute(copiedItemsPathHashes,
									   						  dstFolderPathHash,
									   						  suffixForNameCollision,
									   						  cut);
		return cmdResponse;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EXPOSED FOR TESTING PURPOSES
/////////////////////////////////////////////////////////////////////////////////////////
	public FileExplorerCommandResponseForPaste execute(final Collection<FileExplorerItemPathHash> copiedItemsPathHashes,
									  				   final FileExplorerItemPathHash dstFolderPathHash,
									  				   // the destination file/folder name might change because a file/folder
									  				   // with the same name might exist at the destination
									  				   final String suffixForNameCollision,
									  				   // remove the source file/folder?
									  				   final boolean cut) throws IOException {
		// the volume that contains the destination folder
		// ... and the files & folders contained at the destination folder
		FileExplorerVolume dstVol = _storage.getVolumeFor(dstFolderPathHash);
		Path dstFolderRelPath = dstFolderPathHash.getRelativePathFromRoot();
		
		// the output files moved 
		Collection<FileExplorerFileStoreItem> addedItems = new ArrayList<>();
		Collection<FileExplorerItemPathHash> removedItemsHashes = new ArrayList<>();

		for (FileExplorerItemPathHash srcHash : copiedItemsPathHashes) {
			_doCopy(srcHash, 
					FileExplorerItemPathHash.from(dstVol,dstFolderRelPath),
					suffixForNameCollision,
					cut,
					// accumulates copied & cut files/folders
					addedItems,removedItemsHashes);
		}
		
		// return 
		FileExplorerCommandResponseForPaste outResponse = new FileExplorerCommandResponseForPaste();
		outResponse.setAdded(addedItems);
		outResponse.setRemovedItemsPathHashes(removedItemsHashes);
		return outResponse;
	}
	private void _doCopy(final FileExplorerItemPathHash srcItemHash,
						 final FileExplorerItemPathHash dstFolderHash,
						 // the destination file/folder name might change because a file/folder
						 // with the same name might exist at the destination
						 final String suffixForNameCollision,
						 final boolean cut,
						 // the collections that accumulates the added & removed items
						 final Collection<FileExplorerFileStoreItem> addedItems,
						 final Collection<FileExplorerItemPathHash> removedItemsHashes) throws IOException {
		// source data
		FileExplorerVolume srcVol = _storage.getVolumeFor(srcItemHash);
		Path srcItemRelPath = srcItemHash.getRelativePathFromRoot();
		FileProperties srcItem = srcVol.getItemProperties(srcItemRelPath);
		
		// destination
		FileExplorerVolume dstVol = _storage.getVolumeFor(dstFolderHash);
		Path dstFolderRelPath = dstFolderHash.getRelativePathFromRoot();
		
		// [1] - guess the destination file / folder name
		// 		 (beware of collisions)
		String dstItemName = _fileNameBeingAwareOfNameCollision(srcItemRelPath.getLastPathElement(),srcItem.isFolder(),	// the file or folder name
															    // the names of the existing files/folders at the destination folder
															    // (used to guess the item name in case of name collision)
															    _itemsNames(dstVol.listChildren(dstFolderRelPath)),
															    suffixForNameCollision,1);
		// [2] - Create a new file / folder
		// 		 (note that if it's a folder, it's content is NOT copied
		//		  since this might lead to problems where the folder structure is dense)
		Path dstItemRelPath = dstFolderRelPath.joinedWith(dstItemName);
		log.debug("[File explorer] (paste) src={} dst={}",
				  srcItemRelPath,dstItemRelPath);
		if (srcItem.isFile()) {
			if (cut) {
				dstVol.copyFile(srcItemRelPath, dstItemRelPath, dstVol.existsFile(dstItemRelPath));
				dstVol.deleteFile(srcItemRelPath);
				// new item removed
				removedItemsHashes.add(srcItemHash);
			} else {
				dstVol.copyFile(srcItemRelPath, dstItemRelPath, dstVol.existsFile(dstItemRelPath));
			}
		} else {
			if (cut) {
				dstVol.moveFolder(srcItemRelPath, dstItemRelPath, dstVol.existsFile(dstItemRelPath));
				// new item removed
				removedItemsHashes.add(srcItemHash);
			} else {
				dstVol.copyFolder(srcItemRelPath, dstItemRelPath, null, dstVol.existsFile(dstItemRelPath));
			}
		}
		// new item copied
		FileProperties dstFile = dstVol.getItemProperties(dstItemRelPath);
		addedItems.add(FileExplorerFileStoreItem.itemBuilderForVolume(dstVol)
												 .forItem(dstFile)
												 .build());
	}
}
