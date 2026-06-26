package r01f.core.fileexplorer.command;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.stream.Streams;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import r01f.core.fileexplorer.FileExplorerItemPathHash;
import r01f.core.fileexplorer.FileExplorerStorage;
import r01f.core.fileexplorer.FileExplorerVolume;
import r01f.core.fileexplorer.command.response.FileExplorerCommandResponseForLs;
import r01f.core.fileexplorer.command.response.FileExplorerCommandResponseObject;
import r01f.core.fileexplorer.command.response.FileExplorerFileStoreItem;
import r01f.file.FileProperties;
import r01f.objectstreamer.Marshaller;
import r01f.types.Path;
import r01f.util.types.collections.CollectionUtils;

/**
 * see: https://github.com/Studio-42/elFinder/wiki/Client-Server-API-2.1#ls
 * 
 * Return a list of item names in the target directory.
 * 
 * Arguments:
 * 		- cmd: ls
 * 		- target: hash of directory
 *		- intersect[]: An array of the item names for presence check.
 * Response:
 *		- list : (Object) item names list with hash as key. 
 *				 Return only duplicate files if the intersect[] is specified.
 *			{
 *			   "list": {
 *				   "l1_Zm9sZGVy": "folder", 
 *				   "l1_aW1hZ2UuanBn": "image.jpg", 
 *				   "l1_dGV4dC50eHQ": "text.txt"
 *			   }
 *			}			 
 */
public class FileExplorerCommandForLs 
	 extends FileExplorerCommandBase 
  implements FileExplorerCommand {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
	public FileExplorerCommandForLs(final FileExplorerStorage storage,
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
		FileExplorerItemPathHash folderPathHash = FileExplorerItemPathHash.from(req);
		
		// create the returned list
		FileExplorerCommandResponseObject cmdResponse = this.execute(folderPathHash);
		return cmdResponse;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EXPOSED FOR TESTING PURPOSES
/////////////////////////////////////////////////////////////////////////////////////////
	public FileExplorerCommandResponseForLs execute(final FileExplorerItemPathHash folderPathHash) throws IOException {
		// find the volume
		FileExplorerVolume vol = _storage.getVolumeFor(folderPathHash);
		
		// create the returned list
		Path folderRelPath = folderPathHash.getRelativePathFromRoot();
		Collection<FileProperties> items = vol.listChildren(folderRelPath);
		Map<String,String> itemsMap = CollectionUtils.hasData(items) 
										? Streams.of(items)
												 .map(item -> FileExplorerFileStoreItem.itemBuilderForVolume(vol)
														 							   .forItem(item)
														 							   .build())
												 .collect(Collectors.toMap(item -> item.getHash(),	// key
														 				   item -> item.getName()))	// value
										: Collections.emptyMap();
		// return response
		FileExplorerCommandResponseForLs outResponse = new FileExplorerCommandResponseForLs();
		outResponse.setList(itemsMap);
		return outResponse;
	}
}
