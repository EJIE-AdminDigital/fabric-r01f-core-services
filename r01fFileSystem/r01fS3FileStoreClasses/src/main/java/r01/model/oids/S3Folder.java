package r01.model.oids;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.cloud.aws.s3.model.AWSS3Bucket;
import r01f.cloud.aws.s3.model.AWSS3FolderPath;

@Accessors(prefix="_")
public class S3Folder {
	
	@Getter protected final	AWSS3Bucket _bucket;
	@Getter protected final	AWSS3FolderPath _path;
	
/////////////////////////////////////////////////////////////////////////////////
// CONSTRUCTOR :
////////////////////////////////////////////////////////////////////////////////
	public S3Folder(final AWSS3Bucket bucket,
			        final AWSS3FolderPath path) {
		_bucket = bucket;
		_path = path;
	}
}
