package r01f.core.fileexplorer.command;

import java.io.IOException;
import java.util.Collection;

import org.apache.commons.compress.utils.Lists;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import r01f.core.fileexplorer.FileExplorerConstants;
import r01f.core.fileexplorer.FileExplorerItemPathHash;
import r01f.core.fileexplorer.FileExplorerStorage;
import r01f.core.fileexplorer.FileExplorerVolume;
import r01f.core.fileexplorer.command.response.FileExplorerCommandResponseForParents;
import r01f.core.fileexplorer.command.response.FileExplorerCommandResponseObject;
import r01f.core.fileexplorer.command.response.FileExplorerFileStoreItem;
import r01f.file.FileProperties;
import r01f.objectstreamer.Marshaller;
import r01f.types.Path;

/**
 * see: https://github.com/Studio-42/elFinder/wiki/Client-Server-API-2.1#parents
 * 
 * Returns all parent folders and their's first level (at least) subfolders and own(target) stat.
 * This command is invoked when a folder is reloaded or there is no data to display the tree of the target folder in the client. 
 * Data provided by 'parents' command should enable the correct drawing of tree hierarchy directories.
 * 
 * Arguments:
 * 		- cmd: parents
 * 		- target: hash of directory
 *		- until : until to hash (stop when this hash is reached) (API >= 2.1024)
 * Response:
 *		- tree : (Array) Folders list. Information about File/Directory
 * 
 * Example:
 * 		Given this folder hierarchy:
 * 		<pre>
 * 			/root1            * (because it's in the ancestor path)
 *             /dir1          * (because it's in the ancestor path)
 *               /dir11       * (because it's in the ancestor path)
 *                 /dir111    *            
 *               /dir12       * (because it's a subfolder of dir11)
 *                 /dir121
 *             /dir2          * (because it's a subfolder of root1) 
 *               /dir22
 *               /dir23
 *                 /dir231
 *          /root2
 * 		</pre>
 * 		When 'dir111' is reloaded, 'parents' data should return	
 *			- dir111 parent directories > dir11 > dir1 > root1
 *			- for each parent directory, its subdirectories (no more depth is needed)
 *
 *		This way, client-side component will render the following reloaded hierarchy
 *		<pre>
 *         /root1  -----------+
 *           /dir1            |
 *             /dir11         | these are the parents
 *               /dir111------+
 *             /dir12 <-- child of dir11
 *           /dir2    <-- child of root1
 *         /root2
 *     </pre>
 */
public class FileExplorerCommandForParents 
	 extends FileExplorerCommandBase 
  implements FileExplorerCommand {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
	public FileExplorerCommandForParents(final FileExplorerStorage storage,
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
		// get hash of the FILE
		FileExplorerItemPathHash folderPathHash = FileExplorerItemPathHash.from(req);
		FileExplorerItemPathHash untilParentFolderPathHash = req.getParameter(FileExplorerConstants.REQ_PARAMETER_UNTIL) != null
																		? FileExplorerItemPathHash.from(req,FileExplorerConstants.REQ_PARAMETER_UNTIL)
																		: null;
		// create the returned data
		FileExplorerCommandResponseObject cmdResponse = this.execute(folderPathHash,
									   						  		 untilParentFolderPathHash);
		return cmdResponse;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EXPOSED FOR TESTING PURPOSES
/////////////////////////////////////////////////////////////////////////////////////////
	public FileExplorerCommandResponseForParents execute(final FileExplorerItemPathHash folderPathHash,
							  				  			 final FileExplorerItemPathHash untilParentFolderPathHash) throws IOException {
		// everything is at the SAME vol
		FileExplorerVolume vol = _storage.getVolumeFor(folderPathHash);
		
		// out files
		Collection<FileExplorerFileStoreItem> folders = Lists.newArrayList();
		
		// go up in the folder hierarchy
		boolean isRoot = false;
		boolean untilParentFolderReached = false;
		FileExplorerItemPathHash currFolderRelPathHash = folderPathHash;
		Path currFolderRelPath = folderPathHash.getRelativePathFromRoot();
		do {
			isRoot = currFolderRelPath == null
				  || vol.isRoot(currFolderRelPath);
			untilParentFolderReached = untilParentFolderPathHash != null ? untilParentFolderPathHash.is(currFolderRelPathHash)
					 							   						 : false;
			if (!isRoot && !untilParentFolderReached) {
				Path currFolderParentRelPath = currFolderRelPath.getParentFolderPath();
				
				if (currFolderParentRelPath != null) {
					FileProperties currFolderParent = vol.getItemProperties(currFolderParentRelPath);
					if (!currFolderParent.isFolder()) throw new IllegalStateException(currFolderParent + " is NOT a folder!!");
					FileExplorerFileStoreItem currFolderItem = FileExplorerFileStoreItem.itemBuilderForVolume(vol)
																						.forItem(currFolderParent)
																						.build();
					// add the item
					folders.add(currFolderItem);
					
					// add the item child folders
					folders.addAll(_childFoldersOf(currFolderParentRelPath,
												   vol));
				}
				
				// next up!
				currFolderRelPath = currFolderParentRelPath;
			}
		} while (!isRoot && !untilParentFolderReached);
		
		// return 
		FileExplorerCommandResponseForParents outResponse = new FileExplorerCommandResponseForParents();
		outResponse.setTree(folders);
		return outResponse; 
	}
}
