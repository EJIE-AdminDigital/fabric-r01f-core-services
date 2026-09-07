package r01.api.filestore.s3;

import java.io.IOException;

import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.cloud.aws.s3.model.AWSS3ObjectHeadResult;
import r01f.file.FileProperties;
import r01f.file.FilePropertiesBase;
import r01f.mime.MimeTypes;
import r01f.objectstreamer.annotations.MarshallType;

@MarshallType(as="s3FileProperties")
@Accessors(prefix="_")
@Slf4j
public class S3FileProperties 
	 extends FilePropertiesBase {

	private static final long serialVersionUID = 8307814506615873731L;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
    public static FileProperties from(final AWSS3ObjectHeadResult headObj,
    								  final r01f.types.Path objPath) throws IOException {		
		// transform into FileProperties
		FileProperties outProperties = new S3FileProperties();
		outProperties.setPath(objPath);		
		outProperties.setFolder(headObj.getMimeType().toString().equals(MimeTypes.APPLICATION_XDIRECTORY.asString()));
		outProperties.setSize(headObj.getContentLength());
		
		// En S3 no existe el concepto de creación y modificación, cada vez que se sube el mismo objeto se crean nuevas versiones del objeto
		// por lo tanto establecemos el mismo TS
		outProperties.setModificationTimeStamp(headObj.getLastModified() != null ? headObj.getLastModified().toEpochMilli() : null);
		outProperties.setCreateTimeStamp(headObj.getLastModified() != null ? headObj.getLastModified().toEpochMilli() : null);
		
		//TODO qué hacemos con el resto de atributos
		
		return outProperties;
    }
    public static FileProperties fromOrNull(final AWSS3ObjectHeadResult headObj,
    										final r01f.types.Path objPath) {
		FileProperties outProps = null;
		try {
			outProps = S3FileProperties.from(headObj,objPath);
		} catch (IOException ioEx) {
			log.error("Error creating a {} from {}: {}",
					  S3FileProperties.class,objPath,
					  ioEx.getMessage(),ioEx);
		}
		return outProps;
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  OTHER
/////////////////////////////////////////////////////////////////////////////////////////

}
