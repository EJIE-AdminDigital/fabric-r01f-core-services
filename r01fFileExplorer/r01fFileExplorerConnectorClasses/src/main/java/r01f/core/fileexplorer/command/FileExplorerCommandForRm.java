package r01f.core.fileexplorer.command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import com.google.common.collect.Lists;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import r01f.core.fileexplorer.FileExplorerItemPathHash;
import r01f.core.fileexplorer.FileExplorerStorage;
import r01f.core.fileexplorer.FileExplorerVolume;
import r01f.core.fileexplorer.command.response.FileExplorerCommandResponseForRm;
import r01f.core.fileexplorer.command.response.FileExplorerCommandResponseObject;
import r01f.file.FileProperties;
import r01f.objectstreamer.Marshaller;
import r01f.types.Path;
import r01f.util.types.collections.CollectionUtils;

/**
 * see: https://github.com/Studio-42/elFinder/wiki/Client-Server-API-2.1#rm
 * 
 * Recursively removes files and directories.
 * 
 * Arguments:
 * 		- cmd: rm
 * 		- targets[]: (Array) array of file and directory hashes to delete
 * Response:
 *		- removed: (Array) array of file and directory 'hashes' that were successfully deleted			 
 */
@Slf4j
public class FileExplorerCommandForRm 
	 extends FileExplorerCommandBase 
  implements FileExplorerCommand {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
	public FileExplorerCommandForRm(final FileExplorerStorage storage,
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
		// get hash for the given files/folders
		Collection<FileExplorerItemPathHash> itemsPathHashes = FileExplorerItemPathHash.multipleFrom(req);
		
		// delete
		FileExplorerCommandResponseObject cmdResponse = this.execute(itemsPathHashes);
		return cmdResponse;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	FOR TESTING PURPOSES
/////////////////////////////////////////////////////////////////////////////////////////
	public FileExplorerCommandResponseForRm execute(final FileExplorerItemPathHash... itemsPathHashes) {
		return this.execute(Lists.newArrayList(itemsPathHashes));
	}
	public FileExplorerCommandResponseForRm execute(final Collection<FileExplorerItemPathHash> itemsPathHashes) {
		Collection<FileExplorerItemPathHash> removed = new ArrayList<>();
		for (FileExplorerItemPathHash itemPathHash : itemsPathHashes) {
			FileExplorerVolume vol = _storage.getVolumeFor(itemPathHash);
			Path itemRelPath = itemPathHash.getRelativePathFromRoot();
			_delete(vol,
					itemRelPath, 
					removed);
		}
		// return result
		FileExplorerCommandResponseForRm outResponse = new FileExplorerCommandResponseForRm();
		outResponse.setRemovedItemsPathHashes(removed);
		return outResponse;
	}
	private void _delete(final FileExplorerVolume vol,
						 final Path itemRelPath,
						 // accumulates the removed files / folders
						 final Collection<FileExplorerItemPathHash> removed) {
		try {
			FileProperties item = vol.getItemProperties(itemRelPath);
			if (item.isFile()) {
				vol.deleteFile(itemRelPath);
			} else {
				// BEWARE!!! recursion
				Collection<FileProperties> childItems = vol.listChildren(itemRelPath);
				if (CollectionUtils.hasData(childItems)) {
					for (FileProperties childItem : childItems) {
						_delete(vol,
								vol.getRelativePathFromRootOf(childItem.getPath()),
								removed);
					}
				}
				// remove the folder
				vol.deleteFolder(itemRelPath);
			}
			removed.add(FileExplorerItemPathHash.from(vol.getId(),itemRelPath));
		} catch (IOException ioEx) {
			log.error("could NOT delete file/folder at {}: {}",
					  itemRelPath,
					  ioEx.getMessage(),ioEx);
		}
	}
}
