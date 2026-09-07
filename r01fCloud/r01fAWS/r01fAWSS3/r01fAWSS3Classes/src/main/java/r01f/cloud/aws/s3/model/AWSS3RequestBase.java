package r01f.cloud.aws.s3.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Request Base
 */
@Accessors(prefix="_")
public abstract class AWSS3RequestBase<SELF_TYPE extends AWSS3RequestBase<SELF_TYPE>> {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter @Setter protected AWSS3BucketID _bucket;
	@Getter @Setter protected AWSS3ObjectKey _key;
	@Getter @Setter protected AWSS3Range _range;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AWSS3RequestBase(final AWSS3BucketID bucket,final AWSS3ObjectKey key) {
		_bucket = bucket;
		_key = key;
	}
	
	public AWSS3RequestBase(final AWSS3BucketID bucket,final AWSS3ObjectKey key, final AWSS3Range range) {
		_bucket = bucket;
		_key = key;
		_range = range;
	}
}
