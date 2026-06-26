package r01f.core.fileexplorer.command;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import r01f.core.fileexplorer.FileExplorerConstants;
import r01f.core.fileexplorer.FileExplorerItemPathHash;
import r01f.core.fileexplorer.FileExplorerStorage;
import r01f.core.fileexplorer.FileExplorerVolume;
import r01f.core.fileexplorer.command.response.FileExplorerCommandResponseForRename;
import r01f.core.fileexplorer.command.response.FileExplorerCommandResponseObject;
import r01f.core.fileexplorer.command.response.FileExplorerFileStoreItem;
import r01f.file.FileProperties;
import r01f.objectstreamer.Marshaller;
import r01f.types.Path;

/**
 * see: https://github.com/Studio-42/elFinder/wiki/Client-Server-API-2.1#rename
 * 
 * Renaming a directory/file
 * 
 * Arguments:
 * 		- cmd: rename
 * 		- target: hash directory/file being renamed
 * 		- name: New name of the directory/file
 * Response:
 *		- added : (Array) array of file and directory objects renamed. Information about File/Directory
 *		- removed : (Array) array of file and directory 'hashes' that were successfully removed			 
 */
public class FileExplorerCommandForRename 
     extends FileExplorerCommandBase 
  implements FileExplorerCommand {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
	public FileExplorerCommandForRename(final FileExplorerStorage storage,
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
		// get hash of the folder
		FileExplorerItemPathHash itemPathHash = FileExplorerItemPathHash.from(req);
		
		// rename
		String newName = req.getParameter(FileExplorerConstants.REQ_PARAMETER_NAME);
		FileExplorerCommandResponseObject cmdResponse = this.execute(itemPathHash,newName);
		return cmdResponse;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	RENAME
/////////////////////////////////////////////////////////////////////////////////////////
	public FileExplorerCommandResponseForRename execute(final FileExplorerItemPathHash itemPathHash,final String newName) throws IOException {
		// find the volume
		FileExplorerVolume vol = _storage.getVolumeFor(itemPathHash);
		
		// Rename
		Path oldItemRelPath = itemPathHash.getRelativePathFromRoot();
		Path newItemRelPath = _createNewItemRelPath(oldItemRelPath, newName);

		vol.rename(oldItemRelPath, newItemRelPath);
		
		FileProperties newItemProps = vol.getItemProperties(newItemRelPath);

		// return result
		FileExplorerCommandResponseForRename outResult = new FileExplorerCommandResponseForRename();
		outResult.addAddedItem(FileExplorerFileStoreItem.itemBuilderForVolume(vol)
											     		.forItem(newItemProps)
											     		.build());
		outResult.addRemovedItem(itemPathHash);
		return outResult;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	PRIVATE METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	private Path _createNewItemRelPath(Path oldPath, String newName) {
		if (_isRootItem(oldPath)) {
			return Path.forId(newName);
		} else {
			return oldPath.getParentFolderPath()
						  .joinedWith(newName);
		}
	}
	private boolean _isRootItem(Path itemRelPath) {
		return null == itemRelPath.getParentFolderPath();
	}
}
