package r01f.cloud.aws.s3.events.listener;


import r01f.cloud.aws.s3.model.AWSS3BucketID;
import r01f.cloud.aws.s3.model.AWSS3ObjectGetResult;
import r01f.cloud.aws.s3.model.AWSS3ObjectKey;

public interface AWSS3ObjectRetriever {

	 public AWSS3ObjectGetResult retrieveObject(final AWSS3BucketID bucket,final AWSS3ObjectKey key);
	
}
