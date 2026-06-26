package r01f.core.fileexplorer.command;

import java.io.IOException;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import r01f.core.fileexplorer.FileExplorerItemPathHash;
import r01f.core.fileexplorer.FileExplorerStorage;
import r01f.core.fileexplorer.FileExplorerVolume;
import r01f.core.fileexplorer.command.response.FileExplorerCommandResponseForInfo;
import r01f.core.fileexplorer.command.response.FileExplorerCommandResponseObject;
import r01f.core.fileexplorer.command.response.FileExplorerFileStoreItem;
import r01f.file.FileProperties;
import r01f.mime.MimeType;
import r01f.objectstreamer.Marshaller;
import r01f.types.Path;

/**
 * see: https://github.com/Studio-42/elFinder/wiki/Client-Server-API-2.1#info
 * 
 * Returns information about [places] nodes (the [places] "folder" on the left node tree)
 * 
 * Arguments:
 * 		- cmd: info
 * 		- target[]: array of hashed paths of the nodes
 * Response:
 *		- files: (Array of data) places directories info data Information about File/Directory			 
 */
public class FileExplorerCommandForInfo 
	 extends FileExplorerCommandBase 
  implements FileExplorerCommand {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
	public FileExplorerCommandForInfo(final FileExplorerStorage storage,
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
		Collection<FileExplorerItemPathHash> nodesPathHashes = FileExplorerItemPathHash.multipleFrom(req);
		
		// create the returned list
		FileExplorerCommandResponseObject cmdResponse = this.execute(nodesPathHashes);
		return cmdResponse;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EXPOSED FOR TESTING PURPOSES
/////////////////////////////////////////////////////////////////////////////////////////
	public FileExplorerCommandResponseForInfo execute(final Collection<FileExplorerItemPathHash> nodesPathHashes) throws IOException {
		
		Collection<FileExplorerFileStoreItem> FileExplorerFileStoreItems = nodesPathHashes.stream()
																						   .map(this::_handleFileProcessing)
																						   .filter(Objects::nonNull)
																						   .collect(Collectors.toList());
		
		
		// return response
		FileExplorerCommandResponseForInfo outResponse = new FileExplorerCommandResponseForInfo();
		outResponse.setItems(FileExplorerFileStoreItems);
		return outResponse;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	private  FileExplorerFileStoreItem _handleFileProcessing(final FileExplorerItemPathHash filePathHash) {
	    try {
	        FileExplorerVolume vol = _storage.getVolumeFor(filePathHash);
	        Path filePath = filePathHash.getRelativePathFromRoot();
	        FileProperties file = vol.getItemProperties(filePath);
	        MimeType mime = vol.getMimeType(file);
	        long size = file.getSize();
	        return FileExplorerFileStoreItem.itemBuilderForVolume(vol)
	                                        .forItem(file)
	                                        .build();
	    } catch (IOException e) {
	        System.err.println("Error al obtener propiedades del archivo: " + e.getMessage());
	        return null; // O algún otro valor por defecto si prefieres
	    }
	}
}
