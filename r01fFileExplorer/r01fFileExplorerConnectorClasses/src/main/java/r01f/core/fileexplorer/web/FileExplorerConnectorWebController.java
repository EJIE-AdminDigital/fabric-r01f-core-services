package r01f.core.fileexplorer.web;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import r01f.core.fileexplorer.FileExplorerConstants;
import r01f.core.fileexplorer.FileExplorerStorage;
import r01f.core.fileexplorer.FileExplorerVolume;
import r01f.core.fileexplorer.command.FileExplorerCommandFactory;
import r01f.core.fileexplorer.command.FileExplorerCommandForUpload;
import r01f.filestore.api.FileNameSanitizer;
import r01f.filestore.api.FileNameSanitizerByDefault;
import r01f.objectstreamer.Marshaller;
import r01f.servlet.HttpServletRequestUtils;
import r01f.util.types.Strings;

@Slf4j
public class FileExplorerConnectorWebController {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final Marshaller _marshaller;
	private final FileExplorerStorage _fileExplorerStorage;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public FileExplorerConnectorWebController(final Marshaller marshaller,
											  final FileExplorerStorage storage) {
		_marshaller = marshaller;
		_fileExplorerStorage = storage;
	}
	public FileExplorerConnectorWebController(final Marshaller marshaller,
											  final FileExplorerVolume... vols) {
		this(marshaller,
			 new FileNameSanitizerByDefault(),
			 vols);
	}
	public FileExplorerConnectorWebController(final Marshaller marshaller,
											  final Collection<FileExplorerVolume> vols) {
		this(marshaller,
			 new FileNameSanitizerByDefault(),
			 vols);
	}
	public FileExplorerConnectorWebController(final Marshaller marshaller,
											  final FileNameSanitizer fileNameSanitizer,
											  final FileExplorerVolume... vols) {
		this(marshaller,
			 fileNameSanitizer,
			 List.of(vols));
	}
	public FileExplorerConnectorWebController(final Marshaller marshaller,
											  final FileNameSanitizer fileNameSanitizer,
											  final Collection<FileExplorerVolume> vols) {
		_marshaller = marshaller;
		
		FileExplorerStorage fsStorage = new FileExplorerStorage(vols);
		fsStorage.setFileNameSanitizer(fileNameSanitizer);	// set file name sanitizer (used when uploading files)
		_fileExplorerStorage = fsStorage;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public void execute(final HttpServletRequest request,final HttpServletResponse response) throws ServletException,
																									IOException {
		if (HttpServletRequestUtils.isMultiPartPOST(request)) {
			// POST Multi-Part > always UPLOAD
			FileExplorerCommandForUpload uploadCmd = FileExplorerCommandFactory.UPLOAD
																			   .createCommandUsing(_fileExplorerStorage,_marshaller);
			log.debug("[File Explorer] (web controller) > cmd=upload");
			
			// execute
			uploadCmd.execute(request,response);
		} else {
			// get the command
			String cmd = request.getParameter(FileExplorerConstants.REQ_PARAMETER_CMD);
			log.debug("[File Explorer] (web controller) > cmd={}",cmd);
			
			if (Strings.isNullOrEmpty(cmd)
			 || FileExplorerCommandFactory.isNOTSupportedCommand(cmd)) { 
				log.warn("[File Explorer] (web controller): cmd={} is NOT supported",cmd);
			} else {
				// execute
				FileExplorerCommandFactory.createCommandFor(cmd)
									   	  .using(_fileExplorerStorage,_marshaller)
									   	  .execute(request,response);
			}
		}
	}
}