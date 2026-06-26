package r01.model.oids;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.cloud.aws.s3.model.AWSS3Bucket;
import r01f.cloud.aws.s3.model.AWSS3ObjectKey;

@Accessors(prefix="_")
public class S3File {
	
	@Getter protected final	AWSS3Bucket _bucket;
	@Getter protected final	AWSS3ObjectKey _objKey;
	
/////////////////////////////////////////////////////////////////////////////////
// CONSTRUCTOR :
////////////////////////////////////////////////////////////////////////////////
	public S3File(final AWSS3Bucket bucket,
			      final	AWSS3ObjectKey objKey) {
		_bucket = bucket;
		_objKey = objKey;
	}

}
