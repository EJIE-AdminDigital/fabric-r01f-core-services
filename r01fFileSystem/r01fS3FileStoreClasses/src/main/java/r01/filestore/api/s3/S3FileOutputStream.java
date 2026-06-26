package r01.filestore.api.s3;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.lang3.StringUtils;

import r01f.cloud.aws.s3.client.api.AWSS3ClientAPI;
import r01f.cloud.aws.s3.model.AWSS3Bucket;
import r01f.cloud.aws.s3.model.AWSS3ObjectKey;
import r01f.file.FileNameAndExtension;
import r01f.types.Pair;

public class S3FileOutputStream 
     extends OutputStream {
	
	private final AWSS3ClientAPI _client;
    private final AWSS3Bucket _bucket;
    private final AWSS3ObjectKey _s3Key;
    
    private final File tempFile;
    private final FileOutputStream fileOutputStream;

    
    public S3FileOutputStream(final AWSS3ClientAPI client,final AWSS3Bucket bucket,final AWSS3ObjectKey s3Key) throws IOException {
    	this._client = client;
        this._bucket = bucket;
        this._s3Key = s3Key;
        
        Pair<String, String> thefileName = _createTempFileName(s3Key);
        this.tempFile = File.createTempFile(thefileName.getA(),thefileName.getB());
        this.fileOutputStream = new FileOutputStream(tempFile);
    }
    
	@Override
    public void write(final int b) throws IOException {
        fileOutputStream.write(b);
    }

    @Override
    public void close() throws IOException {
        try {
            fileOutputStream.close();
            _client.forObjects()
	            		.putObject(_bucket, _s3Key, tempFile, null);
        } finally {
            tempFile.delete();
        }
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	private Pair<String,String> _createTempFileName(final AWSS3ObjectKey s3Key) {
    	FileNameAndExtension fileNameExt = FileNameAndExtension.of(s3Key.toString().replaceAll("/", "-"));    	
    	String theFileNameExt = fileNameExt.getExtension();
    	return new Pair(fileNameExt.getNameWithoutExtension(),
    					StringUtils.isEmpty(theFileNameExt) ? ".tmp" : "." + theFileNameExt);
    }

}
