package r01f.core.fileexplorer.command;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import r01f.core.fileexplorer.FileExplorerItemPathHash;
import r01f.core.fileexplorer.FileExplorerStorage;
import r01f.core.fileexplorer.FileExplorerVolume;
import r01f.core.fileexplorer.command.response.FileExplorerCommandResponseForGet;
import r01f.core.fileexplorer.command.response.FileExplorerCommandResponseObject;
import r01f.io.util.StringPersistenceUtils;
import r01f.objectstreamer.Marshaller;
import r01f.types.Path;

/**
 * see: https://github.com/Studio-42/elFinder/wiki/Client-Server-API-2.1#get
 * 
 * Returns the content as String (As UTF-8)
 * 
 * Arguments:
 * 		- cmd: get
 * 		- target: hash of the file
 * 		- current : hash of the directory where the file is stored
 *		- conv : instructions for character encoding conversion of the text file
 *					                          1 : auto detect encoding (Return false as content in response data when failed)
 *					                          0 : auto detect encoding (Return { "doconv" : "unknown" } as response data when failed)
 *					Original Character encoding : original character encoding as specified by the user
 * Response:
 *		- content: file contents (UTF-8 String or Data URI Scheme String) or false
 *		- encoding: (Optional) Detected original character encoding (Require when converting from encoding other than UTF-8)
 *		- doconv: (Optional) "doconv":"unknown" is returned to ask the user for the original encoding if automatic conversion to UTF-8 is not possible when requested with conv = 0.
 *
 * 		For files other than text files, content must be returned as string data of Data URI Scheme.
 *			{
 *			    "content": "Hello world!" // contents of the file 
 *			}		 
 */
public class FileExplorerCommandForGet 
	 extends FileExplorerCommandBase 
  implements FileExplorerCommand {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	public static final String ENCODING = "utf-8";
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
	public FileExplorerCommandForGet(final FileExplorerStorage storage,
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
		// get hash of the file
		FileExplorerItemPathHash filePathHash = FileExplorerItemPathHash.from(req);
		
		
		// return the file contents
		FileExplorerCommandResponseObject cmdResponse = this.execute(filePathHash);
		return cmdResponse;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EXPOSED FOR TESTING PURPOSES
/////////////////////////////////////////////////////////////////////////////////////////
	public FileExplorerCommandResponseForGet execute(final FileExplorerItemPathHash filePathHash) throws IOException {
		// file path 
		Path fileRelPath = filePathHash.getRelativePathFromRoot();
		
		// find the volume
		FileExplorerVolume vol = _storage.getVolumeFor(filePathHash);
		
		// get
		FileExplorerCommandResponseForGet outResponse = new FileExplorerCommandResponseForGet();
		try (InputStream is = vol.openInputStream(fileRelPath)) {
			String content = StringPersistenceUtils.load(is,
													     Charset.forName(ENCODING)); 
			outResponse.setContent(content);
		}
		return outResponse;
	}
}
