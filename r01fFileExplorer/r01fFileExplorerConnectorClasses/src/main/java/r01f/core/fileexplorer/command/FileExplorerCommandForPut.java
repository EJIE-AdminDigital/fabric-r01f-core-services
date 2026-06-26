package r01f.core.fileexplorer.command;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import r01f.core.fileexplorer.FileExplorerConstants;
import r01f.core.fileexplorer.FileExplorerItemPathHash;
import r01f.core.fileexplorer.FileExplorerStorage;
import r01f.core.fileexplorer.FileExplorerVolume;
import r01f.core.fileexplorer.command.response.FileExplorerCommandResponseForPut;
import r01f.core.fileexplorer.command.response.FileExplorerCommandResponseObject;
import r01f.core.fileexplorer.command.response.FileExplorerFileStoreItem;
import r01f.file.FileProperties;
import r01f.objectstreamer.Marshaller;
import r01f.types.Path;

/**
 * see: https://github.com/Studio-42/elFinder/wiki/Client-Server-API-2.1#put
 * 
 * Stores contents data in a file.
 * 
 * Arguments:
 * 		- cmd: put
 * 		- target: hash of the file
 *		- content: new contents of the file
 *		- encoding: character encoding at the time of saving 
 *				    (Text data will be sent by UTF-8) 
 *					or "scheme" for URL of contents or Data URI scheme
 * 
 * content of file data other than text file is sent as string data
 * of Data URI Scheme or URL of new contents with param encoding=scheme.
 *
 * Response:
 *		- changed: (Array) of files that were successfully uploaded. Information about File/Directory			 
 */
public class FileExplorerCommandForPut 
	 extends FileExplorerCommandBase 
  implements FileExplorerCommand {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
	public FileExplorerCommandForPut(final FileExplorerStorage storage,
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
		// get the content
		String content = req.getParameter(FileExplorerConstants.REQ_PARAMETER_CONTENT);
		
		// get hash of the folder
		FileExplorerItemPathHash filePathHash = FileExplorerItemPathHash.from(req);
		
		// execute
		FileExplorerCommandResponseObject cmdResponse = this.execute(filePathHash,
									   						  content);
		return cmdResponse;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EXPOSED FOR TESTING PURPOSES
/////////////////////////////////////////////////////////////////////////////////////////
	public FileExplorerCommandResponseForPut execute(final FileExplorerItemPathHash filePathHash,
							  				  		 final String content) throws IOException {
		Path fileRelPath = filePathHash.getRelativePathFromRoot();
		
		// find the volume
		FileExplorerVolume vol = _storage.getVolumeFor(filePathHash);

//		try (OutputStream os = vol.openOutputStream(fileRelPath)) {
//			IOStreams.write(content,os,FileExplorerConstants.UTF8_ENCODING);
//		}
		try (InputStream data = IOUtils.toInputStream(content,FileExplorerConstants.UTF8_ENCODING)) {
			vol.write(fileRelPath, data);
		}
		
		// target file properties
		FileProperties file = vol.getItemProperties(fileRelPath);
		
		// return 
		FileExplorerCommandResponseForPut outResponse = new FileExplorerCommandResponseForPut();
		outResponse.addChangedItem(FileExplorerFileStoreItem.itemBuilderForVolume(vol)
						 						   			.forItem(file)
						 						   			.build());
		return outResponse;
	}
}
