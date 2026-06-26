package r01f.core.fileexplorer.command;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import r01f.core.fileexplorer.FileExplorerItemPathHash;
import r01f.core.fileexplorer.FileExplorerStorage;
import r01f.core.fileexplorer.FileExplorerVolume;
import r01f.core.fileexplorer.command.response.FileExplorerCommandResponseForDim;
import r01f.core.fileexplorer.command.response.FileExplorerCommandResponseObject;
import r01f.objectstreamer.Marshaller;
import r01f.types.Path;

/**
 * see: https://github.com/Studio-42/elFinder/wiki/Client-Server-API-2.1#dim
 * 
 * Returns the dimensions of an image/video
 * 
 * Arguments:
 * 		- cmd: dim
 * 		- target: hash of file,
 *		- substitute: pixel that requests substitute image (optional) - API >= 2.1030
 * Response:
 *		- dim: The dimensions of the media in the format {width}x{height} (e.g. "640x480").
 *		- url: The URL of requested substitute image. (optional)		 
 */
public class FileExplorerCommandForDim 
	 extends FileExplorerCommandBase 
  implements FileExplorerCommand {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
	public FileExplorerCommandForDim(final FileExplorerStorage storage,
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
		// get the hash of the file
		FileExplorerItemPathHash relPathHash = FileExplorerItemPathHash.from(req);
		
		// find the image dimensions
		FileExplorerCommandResponseObject cmdResponse = this.execute(relPathHash);
		return cmdResponse;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EXPOSED FOR TESTING PURPOSES
/////////////////////////////////////////////////////////////////////////////////////////
	public FileExplorerCommandResponseForDim execute(final FileExplorerItemPathHash filePathHash) throws IOException {
		// find the volume
		FileExplorerVolume vol = _storage.getVolumeFor(filePathHash);
		
		// guess the image dimensions
		Path fileRelPath = filePathHash.getRelativePathFromRoot();
		BufferedImage image = ImageIO.read(vol.openInputStream(fileRelPath));

		// return as json
		FileExplorerCommandResponseForDim outResponse = new FileExplorerCommandResponseForDim();
		outResponse.setDim(image.getWidth(),image.getHeight());
		return outResponse;
	}
}
