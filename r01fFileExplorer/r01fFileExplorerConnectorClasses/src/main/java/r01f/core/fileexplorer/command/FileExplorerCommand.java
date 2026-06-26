package r01f.core.fileexplorer.command;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import r01f.core.fileexplorer.command.response.FileExplorerCommandResponseObject;
import r01f.core.fileexplorer.command.response.FileExplorerOptions;
import r01f.types.Path;

/**
 * All file explorer commands implements this interface
 */
public interface FileExplorerCommand {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	public FileExplorerOptions getOptionsFor(final Path path);
	
	public FileExplorerCommandResponseObject execute(final HttpServletRequest request) throws ServletException,
																							  IOException;
	
	public void execute(final HttpServletRequest request,final HttpServletResponse response) throws ServletException,
																									IOException;
}
