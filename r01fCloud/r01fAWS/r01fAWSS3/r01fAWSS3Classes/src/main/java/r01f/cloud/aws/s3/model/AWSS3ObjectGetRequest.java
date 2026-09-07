package r01f.cloud.aws.s3.model;

import lombok.experimental.Accessors;


@Accessors(prefix="_")
public class AWSS3ObjectGetRequest
	 extends AWSS3RequestBase<AWSS3ObjectGetRequest> {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR / BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public AWSS3ObjectGetRequest(final AWSS3BucketID bucket,final AWSS3ObjectKey key) {
		super(bucket,key);
	}
	
	public AWSS3ObjectGetRequest(final AWSS3BucketID bucket,final AWSS3ObjectKey key, final AWSS3Range range) {
		super(bucket,key,range);
	}
}
