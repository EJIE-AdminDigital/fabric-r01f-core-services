package r01f.core.fileexplorer.command;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import r01f.core.fileexplorer.FileExplorerConstants;
import r01f.core.fileexplorer.FileExplorerItemPathHash;
import r01f.core.fileexplorer.FileExplorerStorage;
import r01f.core.fileexplorer.FileExplorerVolume;
import r01f.core.fileexplorer.command.response.FileExplorerCommandResponseForMkdir;
import r01f.core.fileexplorer.command.response.FileExplorerCommandResponseObject;
import r01f.core.fileexplorer.command.response.FileExplorerFileStoreItem;
import r01f.file.FileProperties;
import r01f.objectstreamer.Marshaller;
import r01f.types.Path;

/**
 * (see https://github.com/Studio-42/elFinder/wiki/Client-Server-API-2.1#mkdir)
 * 
 * Create a new directory.
 * 
 * Arguments:
 *		- cmd: mkdir
 *		- target: hash of target directory,
 *		- name: New directory name
 *		- dirs[] : array of new directories path (requests at pre-flight of folder upload)
 *	Response:
 *		- added: (Array) Array with a single object - a new directory. Information about File/Directory
 *		- hashes: (Object) Object of path in the dirs[] as a key to the corresponding hash value.
 */
public class FileExplorerCommandForMkdir 
	 extends FileExplorerCommandBase 
  implements FileExplorerCommand {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
	public FileExplorerCommandForMkdir(final FileExplorerStorage storage,
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
		// get parent folder hash
		FileExplorerItemPathHash parentFolderPathHash = FileExplorerItemPathHash.from(req);
		
		// the new folder name
		String newFolderName = req.getParameter(FileExplorerConstants.REQ_PARAMETER_NAME);
		
		// create the folder
		FileExplorerCommandResponseObject cmdResponse = this.execute(parentFolderPathHash,newFolderName);
		return cmdResponse;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EXPOSED FOR TESTING PURPOSES
/////////////////////////////////////////////////////////////////////////////////////////
	public FileExplorerCommandResponseForMkdir execute(final FileExplorerItemPathHash parentFolderPathHash,final String newFolderName) throws IOException {
		// get the volume
		FileExplorerVolume vol = _storage.getVolumeFor(parentFolderPathHash);
		
		Path parentFolderRelPath = parentFolderPathHash.getRelativePathFromRoot();
		Path newFolderRelPath = parentFolderRelPath.joinedWith(newFolderName);		
		vol.createFolder(newFolderRelPath);
		
		// get the created folder properties
		FileProperties folder = vol.getItemProperties(newFolderRelPath);

		// return 
		FileExplorerCommandResponseForMkdir outResponse = new FileExplorerCommandResponseForMkdir();
		outResponse.addAddedItem(FileExplorerFileStoreItem.itemBuilderForVolume(vol)
											     	 .forItem(folder)
											     	 .build());
		return outResponse;
	}
}
