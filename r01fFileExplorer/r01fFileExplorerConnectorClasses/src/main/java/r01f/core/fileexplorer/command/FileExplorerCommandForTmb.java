package r01f.core.fileexplorer.command;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import r01f.core.fileexplorer.FileExplorerConstants;
import r01f.core.fileexplorer.FileExplorerItemPathHash;
import r01f.core.fileexplorer.FileExplorerStorage;
import r01f.core.fileexplorer.command.response.FileExplorerCommandResponseForTmb;
import r01f.core.fileexplorer.command.response.FileExplorerCommandResponseObject;
import r01f.objectstreamer.Marshaller;
import r01f.servlet.HttpRequestParamsWrapper;
import r01f.types.url.Url;
import r01f.types.url.UrlQueryStringParam;

/**
 * see: https://github.com/Studio-42/elFinder/wiki/Client-Server-API-2.1#tmb
 * 
 * Background command. 
 * Creates thumbnails for images that do not have them. 
 * Number of thumbnails created at a time is specified in the connector configuration option tmbAtOnce. 
 * Default is 5.
 * 
 * Arguments:
 * 		- cmd: tmb
 * 		- target: a hash path of the file whose thumbnail should be created
 * 		or
 * 		- target[]: an array of hash path of the files whose thumbnails should be created
 * Response:
 *		- images (object)
 *				{
 *				    "images" : {                                   
 *				        "a696..967": "http://site/tmbs/a696..967.png"	// path hash - thumbnail url
 *						"b674..442": "http://site/tmbs/b674..442.png"	// path hash - thumbnail url
 *						,,,
 *				    }
 *				}			 
 */
public class FileExplorerCommandForTmb 
	 extends FileExplorerCommandBase 
  implements FileExplorerCommand {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
	public FileExplorerCommandForTmb(final FileExplorerStorage storage,
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
		HttpRequestParamsWrapper reqWrap = new HttpRequestParamsWrapper(req);
		
		String businessObjOid = reqWrap.getMandatoryParameter(FileExplorerConstants.REQ_PARAMETER_BUSINESS_OBJ_OID)
									   .asString();
		
		String businessObjTargetPath = reqWrap.getParameter(FileExplorerConstants.REQ_PARAMETER_BUSINESS_OBJ_TARGET_PATH)
											  .asString()
											  .orDefault("");
		
		// get the images path hashes
		Collection<FileExplorerItemPathHash> imgsPathHashes = req.getParameter(FileExplorerConstants.REQ_PARAMETER_TARGET) != null
																		? List.of(FileExplorerItemPathHash.from(req))
																		: FileExplorerItemPathHash.multipleFrom(req);

		// create the returned list
		FileExplorerCommandResponseObject cmdResponse = this.execute(imgsPathHashes,
																	 Url.from(req.getRequestURL()),
																	 businessObjOid,
																	 businessObjTargetPath);
		return cmdResponse;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EXPOSED FOR TESTING PURPOSES
/////////////////////////////////////////////////////////////////////////////////////////
	public FileExplorerCommandResponseForTmb execute(final Collection<FileExplorerItemPathHash> filesPathHashes,
													 final Url baseUrl,
													 final String businessObjOid, final String businessObjTargetPath) throws IOException {
		FileExplorerCommandResponseForTmb outResponse = new FileExplorerCommandResponseForTmb();
		for (FileExplorerItemPathHash pathHash : filesPathHashes) {
			Url thumbUrl = baseUrl.joinWith(UrlQueryStringParam.of(FileExplorerConstants.REQ_PARAMETER_CMD,
																   FileExplorerCommandFactory.TMB_CREATE.getCode()),
											UrlQueryStringParam.of(FileExplorerConstants.REQ_PARAMETER_TARGET,
																   pathHash.asString()),
											UrlQueryStringParam.of(FileExplorerConstants.REQ_PARAMETER_BUSINESS_OBJ_OID,
																   businessObjOid),
											UrlQueryStringParam.of(FileExplorerConstants.REQ_PARAMETER_BUSINESS_OBJ_TARGET_PATH,
																   businessObjTargetPath));
			outResponse.addImageThumb(pathHash,thumbUrl);
		}
		return outResponse;
	}
}
