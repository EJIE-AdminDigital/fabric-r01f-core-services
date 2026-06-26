package r01f.core.fileexplorer.command;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import r01f.core.fileexplorer.FileExplorerItemPathHash;
import r01f.core.fileexplorer.FileExplorerStorage;
import r01f.core.fileexplorer.FileExplorerVolume;
import r01f.core.fileexplorer.command.response.FileExplorerCommandResponseObject;
import r01f.file.FileProperties;
import r01f.io.IOStreams;
import r01f.mime.MimeType;
import r01f.objectstreamer.Marshaller;
import r01f.types.Path;
import r01f.util.types.Strings;

/**
 * see: https://github.com/Studio-42/elFinder/wiki/Client-Server-API-2.1#file
 * 
 * Output file into browser. 
 * This command applies to download and preview actions.
 * (see the GET command that returns the CONTENT of the file)
 * 
 * Arguments:
 * 		- cmd: file
 * 		- target: target file hash
 *		- download: if download=1 send headers to force download file instead of opening it in the browser
 *		- cpath (API >= 2.1.39): Sets a temporary cookie up until download starts. 
 *								 If this parameter is specified, the connector must set the cookie name as "elfdl" + Request ID 
 *								 (The value is irrelevant, maybe "1").
 *								 This cookie is deleted by the client at the start of download.
 *
 *		May need to set:
 *			- Content-Disposition > should have 'inline' for preview action or 'attachments' for download
 *			- Content-Location
 *			- Content-Transfer-Encoding. 
 * Response:
 *		- an inputstream for the file			 
 */
public class FileExplorerCommandForFile 
	 extends FileExplorerCommandBase 
  implements FileExplorerCommand {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public static final String STREAM = "1";
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
	public FileExplorerCommandForFile(final FileExplorerStorage storage,
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
		throw new UnsupportedOperationException(FileExplorerCommandForFile.class.getSimpleName() + " does NOT support this method!");
	}
	@Override
	public void execute(final HttpServletRequest req,final HttpServletResponse resp) throws ServletException,
																							IOException {
		// get hash of the file
		FileExplorerItemPathHash filePathHash = FileExplorerItemPathHash.from(req);
		
		// find the volume
		FileExplorerVolume vol = _storage.getVolumeFor(filePathHash);
		
		// get file data
		Path filePath = filePathHash.getRelativePathFromRoot();
		FileProperties file = vol.getItemProperties(filePath);
		MimeType mime = vol.getMimeType(file);
		long size = file.getSize();
			
		// compose the response
		resp.setCharacterEncoding("utf-8");
		resp.setContentType(mime.asString());
		
		boolean download = STREAM.equals(req.getParameter("download"));
		if (download) {
			resp.setHeader("Content-Disposition",
						   "attachments; " + _composeAttachmentsContentDispositionHeader(filePath.getFileNameAssumingLastElementIsAFile(),
								   									   					 req.getHeader("USER-AGENT")));
			resp.setHeader("Content-Transfer-Encoding",
						   "binary");
		}
		resp.setContentLength((int)size);
		
		OutputStream out = resp.getOutputStream();
		try (InputStream is = vol.openInputStream(filePath)) {
			IOStreams.copy(is,out,
						   false);	// do NOT close when finish
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	private String _composeAttachmentsContentDispositionHeader(final String fileName,
										  	 				   final String userAgent) throws UnsupportedEncodingException {
		String outAttachmentsHeader = null; 
		if (Strings.isNOTNullOrEmpty(userAgent)) {
			String theUserAgent = userAgent;
			theUserAgent = theUserAgent.toLowerCase();
			if (theUserAgent.contains("msie")) {
				outAttachmentsHeader = Strings.customized("filename=\"{}\"",
														  URLEncoder.encode(fileName,"UTF8"));
			} else if (userAgent.contains("opera")) {
				outAttachmentsHeader = Strings.customized("filename*=UTF-8''{}",
														  URLEncoder.encode(fileName,"UTF8")); 
			} else if (userAgent.contains("safari")) {
				outAttachmentsHeader = Strings.customized("filename=\"{}\"",
														  new String(fileName.getBytes("UTF-8"),"ISO8859-1"));
			} else if (userAgent.contains("mozilla")) {
				outAttachmentsHeader = Strings.customized("filename*=UTF-8''{}",
														  URLEncoder.encode(fileName,"UTF8"));
			}
		} else {
			outAttachmentsHeader = Strings.customized("filename=\"{}\"",
													  URLEncoder.encode(fileName,"UTF8"));
		}
		return outAttachmentsHeader;
	}
}
