package r01f.core.fileexplorer.command;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import r01f.core.fileexplorer.FileExplorerConstants;
import r01f.core.fileexplorer.FileExplorerItemPathHash;
import r01f.core.fileexplorer.FileExplorerStorage;
import r01f.core.fileexplorer.FileExplorerVolume;
import r01f.core.fileexplorer.command.response.FileExplorerCommandResponseForMkfile;
import r01f.core.fileexplorer.command.response.FileExplorerCommandResponseObject;
import r01f.core.fileexplorer.command.response.FileExplorerFileStoreItem;
import r01f.file.FileProperties;
import r01f.objectstreamer.Marshaller;
import r01f.types.Path;

/**
 * (https://github.com/Studio-42/elFinder/wiki/Client-Server-API-2.1#mkfile)
 * 
 * Create a new file.
 * 
 * Arguments:
 *		- cmd: mkfile
 *		- target: hash of target directory,
 *		- name: New file name
 *	Response:
 *		- added: (Array) Array with a single object - a new directory. Information about File/Directory
 */
public class FileExplorerCommandForMkfile 
	 extends FileExplorerCommandBase 
  implements FileExplorerCommand {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
	public FileExplorerCommandForMkfile(final FileExplorerStorage storage,
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
		// get the parent folder hash
		FileExplorerItemPathHash parentFolderPathHash = FileExplorerItemPathHash.from(req);
		
		// create the file
		String newFileName = req.getParameter(FileExplorerConstants.REQ_PARAMETER_NAME);
		FileExplorerCommandResponseObject cmdResponse = this.execute(parentFolderPathHash,newFileName);
		return cmdResponse;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EXPOSED FOR TESTING PURPOSES
/////////////////////////////////////////////////////////////////////////////////////////
	public FileExplorerCommandResponseForMkfile execute(final FileExplorerItemPathHash parentFolderPathHash,final String newFileName) throws IOException {
		// get the volume
		FileExplorerVolume vol = _storage.getVolumeFor(parentFolderPathHash);
		
		// mkfile
		Path parentFolderRelPath = parentFolderPathHash.getRelativePathFromRoot();
		Path newFileRelPath = parentFolderRelPath.joinedWith(newFileName);
		vol.createFile(newFileRelPath);
		
		// get the created file properties
		FileProperties file = vol.getItemProperties(newFileRelPath);
		
		// return 
		FileExplorerCommandResponseForMkfile outResponse = new FileExplorerCommandResponseForMkfile();
		outResponse.addAddedItem(FileExplorerFileStoreItem.itemBuilderForVolume(vol)
											  		 .forItem(file)
											  		 .build());
		return outResponse;
	}
}
