package r01f.core.fileexplorer.command;

import java.io.IOException;
import java.util.Collection;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import r01f.core.fileexplorer.FileExplorerItemPathHash;
import r01f.core.fileexplorer.FileExplorerStorage;
import r01f.core.fileexplorer.FileExplorerVolume;
import r01f.core.fileexplorer.command.response.FileExplorerCommandResponseForTree;
import r01f.core.fileexplorer.command.response.FileExplorerCommandResponseObject;
import r01f.core.fileexplorer.command.response.FileExplorerFileStoreItem;
import r01f.objectstreamer.Marshaller;
import r01f.types.Path;

/**
 * see: https://github.com/Studio-42/elFinder/wiki/Client-Server-API-2.1#tree
 * 
 * Return folder's subfolders.
 * 
 * Arguments:
 * 		- cmd: tree
 * 		- target: folder's hash
 * Response:
 *		- tree: (Array) Folders list. Information about File/Directory
 */
public class FileExplorerCommandForTree 
	 extends FileExplorerCommandBase 
  implements FileExplorerCommand {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
	public FileExplorerCommandForTree(final FileExplorerStorage storage,
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
		FileExplorerItemPathHash folderRelPathHash = FileExplorerItemPathHash.from(req);

		// create the returned list
		FileExplorerCommandResponseObject cmdResponse = this.execute(folderRelPathHash);
		return cmdResponse;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EXPOSED FOR TESTING PURPOSES
/////////////////////////////////////////////////////////////////////////////////////////
	public FileExplorerCommandResponseForTree execute(final FileExplorerItemPathHash folderPathHash) throws IOException {
		// find the volume
		FileExplorerVolume vol = _storage.getVolumeFor(folderPathHash);
		
		// find child folders
		Path folderRelPath = folderPathHash.getRelativePathFromRoot();
		Collection<FileExplorerFileStoreItem> childFolders = _childFoldersOf(folderRelPath,vol);

		// return response
		FileExplorerCommandResponseForTree outResponse = new FileExplorerCommandResponseForTree();
		outResponse.setTree(childFolders);
		return outResponse;
	}
}
