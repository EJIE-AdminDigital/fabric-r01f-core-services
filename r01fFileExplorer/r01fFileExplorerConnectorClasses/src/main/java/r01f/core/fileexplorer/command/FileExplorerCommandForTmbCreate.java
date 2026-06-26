package r01f.core.fileexplorer.command;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import javax.imageio.ImageIO;

import com.mortennobel.imagescaling.DimensionConstrain;
import com.mortennobel.imagescaling.ResampleOp;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import r01f.core.fileexplorer.FileExplorerConstants;
import r01f.core.fileexplorer.FileExplorerItemPathHash;
import r01f.core.fileexplorer.FileExplorerStorage;
import r01f.core.fileexplorer.FileExplorerVolume;
import r01f.core.fileexplorer.command.response.FileExplorerCommandResponseObject;
import r01f.file.FileProperties;
import r01f.mime.MimeType;
import r01f.mime.MimeTypes;
import r01f.objectstreamer.Marshaller;
import r01f.types.Path;

/**
 * This is NOT an standard command
 * 
 * Creates thumbnails for an image
 * 
 * Arguments:
 * 		- cmd: tmb
 * 		- target: the hash of the image whos thumbnail should be created
 * Response:
 *		- the image			 
 */
@Slf4j
public class FileExplorerCommandForTmbCreate 
	 extends FileExplorerCommandBase 
  implements FileExplorerCommand {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
	public FileExplorerCommandForTmbCreate(final FileExplorerStorage storage,
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
		throw new UnsupportedOperationException("do not use this!!!");
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public void execute(final HttpServletRequest request,final HttpServletResponse response) throws ServletException,
																									IOException {
		// get the images path hashes
		FileExplorerItemPathHash filePathHash = FileExplorerItemPathHash.from(request);

		// volume
		FileExplorerVolume vol = _storage.getVolumeFor(filePathHash);
		
		// img relative path from root
		Path filePath = filePathHash.getRelativePathFromRoot();
		
		// check if it's an image
		FileProperties fileProps = vol.getItemProperties(filePath);
		MimeType fileMimeType = vol.getMimeType(fileProps);
		if (MimeTypes.wrap(fileMimeType)
					 .isImage()) {
			try (InputStream is = vol.openInputStream(filePath)) {
				// set thumb expiration
				Instant now = Instant.now();
				response.setHeader("Last-Modified",FileExplorerConstants.DATETIME_FORMATTER.format(now));
				response.setHeader("Expires",FileExplorerConstants.DATETIME_FORMATTER.format(now.plus(2,ChronoUnit.YEARS)));
				
				// write the image
				_writeImageThumbnail(is,response.getOutputStream());
			}
		} else {
			log.info("[File Explorer] (thumbnail create): {} is NOT an image (mime-type={})",
					 filePath,fileMimeType);
		}
	}
	private static void _writeImageThumbnail(final InputStream originalImgIS,final OutputStream thumbOS) throws IOException {
		// configure resample operation
		ResampleOp resampleOp = new ResampleOp(DimensionConstrain.createMaxDimension(80,-1));	// width / height
		resampleOp.setNumberOfThreads(4);
		
		// do resample
		BufferedImage image = ImageIO.read(originalImgIS);
		BufferedImage buffImg = resampleOp.filter(image,	// src BufferedImage to be filtered
												  null);	// dst BufferedImage in which to store the result
		
		// write to the destination os
		ImageIO.write(buffImg,"png",
					  thumbOS);
	}
}
