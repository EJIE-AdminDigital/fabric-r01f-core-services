package r01f.core.fileexplorer.command;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import r01f.core.fileexplorer.FileExplorerItemPathHash;
import r01f.core.fileexplorer.FileExplorerStorage;
import r01f.core.fileexplorer.FileExplorerVolume;
import r01f.core.fileexplorer.command.response.FileExplorerCommandResponseForSize;
import r01f.core.fileexplorer.command.response.FileExplorerCommandResponseObject;
import r01f.file.FileProperties;
import r01f.objectstreamer.Marshaller;
import r01f.types.Path;

/**
 * see: https://github.com/Studio-42/elFinder/wiki/Client-Server-API-2.1#size
 * 
 * Returns the size of a directory or file.
 * 
 * Arguments:
 * 		- cmd: size
 * 		- targets[]: hash paths of the nodes
 * Response:
 *		- size: The total size for all the supplied targets.
 *		- fileCnt: The total counts of the file for all the supplied targets. (Optional to API >= 2.1025)
 *		- dirCnt: The total counts of the directory for all the supplied targets. (Optional to API >= 2.1025)
 *		- sizes: An object of each target size information. (Optional to API >= 2.1030)			 
 */
public class FileExplorerCommandForSize 
	 extends FileExplorerCommandBase
  implements FileExplorerCommand {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
	public FileExplorerCommandForSize(final FileExplorerStorage storage,
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

		// get the size
		FileExplorerCommandResponseObject cmdResponse = this.execute(itemsPathHashes);
		return cmdResponse;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EXPOSED FOR TESTING PURPOSES
/////////////////////////////////////////////////////////////////////////////////////////
	public FileExplorerCommandResponseForSize execute(final FileExplorerItemPathHash... itemsPathHashes) throws IOException {
		return this.execute(Lists.newArrayList(itemsPathHashes));
	}
	public FileExplorerCommandResponseForSize execute(final Collection<FileExplorerItemPathHash> itemsPathHashes) throws IOException {
		// size
		long size = 0;
		int fileCnt = 0;
		int dirCnt = 0;
		Map<FileExplorerItemPathHash,Long> sizeByItem = Maps.newHashMapWithExpectedSize(itemsPathHashes.size());
		
		for (FileExplorerItemPathHash itemPathHash : itemsPathHashes) {
			FileExplorerVolume vol = _storage.getVolumeFor(itemPathHash);
			Path itemRelPath = itemPathHash.getRelativePathFromRoot();
			FileProperties fProps = vol.getItemProperties(itemRelPath);
			
			size += fProps.getSize();
			sizeByItem.put(itemPathHash,fProps.getSize());
			if (fProps.isFile()) fileCnt++;
			if (fProps.isFolder()) dirCnt++;
		}
		
		// return result
		FileExplorerCommandResponseForSize outResult = new FileExplorerCommandResponseForSize();
		outResult.setSize(size);
		outResult.setSizes(_transformMap(sizeByItem));
		outResult.setFileCnt(fileCnt);
		outResult.setDirCnt(dirCnt);
		return outResult;
	}
	private Map<String,Long> _transformMap(final Map<FileExplorerItemPathHash,Long> map) {
		Map<String,Long> outTransformed = Maps.newHashMapWithExpectedSize(map.size());
		for (Map.Entry<FileExplorerItemPathHash,Long> me : map.entrySet()) {
			outTransformed.put(me.getKey().asString(),me.getValue());
		}
		return outTransformed;
	}
}
