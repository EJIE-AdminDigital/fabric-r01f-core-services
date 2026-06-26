package r01f.core.fileexplorer.command;

import java.io.IOException;
import java.util.Collection;

import com.google.common.collect.Lists;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import r01f.core.fileexplorer.FileExplorerConstants;
import r01f.core.fileexplorer.FileExplorerItemPathHash;
import r01f.core.fileexplorer.FileExplorerStorage;
import r01f.core.fileexplorer.FileExplorerVolume;
import r01f.core.fileexplorer.command.response.FileExplorerCommandResponseForDuplicate;
import r01f.core.fileexplorer.command.response.FileExplorerCommandResponseObject;
import r01f.core.fileexplorer.command.response.FileExplorerFileStoreItem;
import r01f.file.FileProperties;
import r01f.objectstreamer.Marshaller;
import r01f.types.Path;

/**
 * see: https://github.com/Studio-42/elFinder/wiki/Client-Server-API-2.1#duplicate
 * 
 * Creates a duplicate for directories / files. 
 * Copy name is generated as follows: 
 * 		basedir_name_filecopy+serialnumber.extension (if any)
 * 
 * Arguments:
 * 		- cmd: duplicate
 * 		- targets[] : An array of hashes for the directories or files to be duplicated
 * Response:
 *		- added (Array) Information about File/Directory of the duplicate.			 
 */
public class FileExplorerCommandForDuplicate 
	 extends FileExplorerCommandBase 
  implements FileExplorerCommand {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
	public FileExplorerCommandForDuplicate(final FileExplorerStorage storage,
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
		// get the path hash of every folder or file
		Collection<FileExplorerItemPathHash> pathsHashes = FileExplorerItemPathHash.multipleFrom(req);
		
		// duplicat
		FileExplorerCommandResponseObject cmdResponse = this.execute(pathsHashes);
		return cmdResponse;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EXPOSED FOR TESTING PURPOSES
/////////////////////////////////////////////////////////////////////////////////////////
	public FileExplorerCommandResponseForDuplicate execute(final FileExplorerItemPathHash... pathsHashes) throws IOException {
		return this.execute(Lists.newArrayList(pathsHashes));
	}
	public FileExplorerCommandResponseForDuplicate execute(final Collection<FileExplorerItemPathHash> pathsHashes) throws IOException {
		Collection<FileExplorerFileStoreItem> outDuplicatedItems = Lists.newArrayList();
		
		for (FileExplorerItemPathHash srcPathHash : pathsHashes) {
			// get the volume for the hash
			FileExplorerVolume vol = _storage.getVolumeFor(srcPathHash);
			
			// duplicate file (beware of collisions)
			Path srcItemRelPath = srcPathHash.getRelativePathFromRoot();
			FileProperties srcItem = vol.getItemProperties(srcItemRelPath);
			String dstItemName = _fileNameBeingAwareOfNameCollision(srcItemRelPath.getLastPathElement(),srcItem.isFolder(),	// the file or folder name
																    // the names of the existing files/folders at the destination folder
																    // (used to guess the item name in case of name collision)
																    _itemsNames(vol.listChildren(srcItemRelPath.getParentFolderPath())),
																    FileExplorerConstants.DEFAULT_SUFFIX_FOR_NAME_COLLISION,1);
			Path dstPath = srcItemRelPath.getParentFolderPath()
								  .joinedWith(dstItemName);
			_createAndCopy(srcItemRelPath,dstPath,
						   vol);
			
			// get the duplicated file properties
			FileProperties dstFile = vol.getItemProperties(dstPath);
			outDuplicatedItems.add(FileExplorerFileStoreItem.itemBuilderForVolume(vol)
	 												  	 .forItem(dstFile)
	 												  	 .build());
		}
		// return response
		FileExplorerCommandResponseForDuplicate outResponse = new FileExplorerCommandResponseForDuplicate();
		outResponse.setAdded(outDuplicatedItems);
		return outResponse;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	COPY
/////////////////////////////////////////////////////////////////////////////////////////	
	private void _createAndCopy(final Path srcPath,final Path dstPath,
							    final FileExplorerVolume vol) throws IOException {
		FileProperties srcFile = vol.getItemProperties(srcPath);
		if (srcFile.isFolder()) {
			_createAndCopyFolder(srcPath,dstPath,
								 vol);
		} else {
			_createAndCopyFile(srcPath,dstPath,
							   vol);
		}
	}
	private static void _createAndCopyFile(final Path srcPath,final Path dstPath,
										   final FileExplorerVolume vol) throws IOException {
		vol.copyFile(srcPath, dstPath, false);
	}
	private static void _createAndCopyFolder(final Path srcPath,final Path dstPath,
									  		 final FileExplorerVolume vol) throws IOException {
		vol.copyFolder(srcPath, dstPath, null, false);
	}
}
