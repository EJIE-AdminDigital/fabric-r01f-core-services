package r01f.core.fileexplorer;

import java.io.IOException;
import java.io.InputStream;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import r01f.file.FileProperties;
import r01f.mime.MimeType;
import r01f.mime.MimeTypes;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class FileExplorerMimeTypeDetector {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Gets mime type from the given input stream.
	 * @return the mime type.
	 * @throws IOException if the stream can not be read.
	 */
	public static MimeType detect(final InputStream inputStream) throws IOException {
		return MimeTypes.from(inputStream);
	}
	/**
	 * Gets mime type from the given file path.
	 *
	 * @return the mime type.
	 * @throws IOException if the file can not be read.
	 */
	public static MimeType detect(final FileProperties fileProperties) throws IOException {
		MimeType outMimeType = null;
		if (fileProperties.isFolder()) {
			outMimeType = MimeType.from("directory");
		} else {
			outMimeType = MimeTypes.fromFileSimpleName(fileProperties.getPath()
																	 .getFileNameAssumingLastElementIsAFile());
		}
		return outMimeType;
	}
}
