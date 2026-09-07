package r01f.cloud.aws.s3.client.api.delegates;

import r01f.cloud.aws.s3.api.interfaces.AWSS3ServicesForBuckets;
import r01f.cloud.aws.s3.api.interfaces.impl.AWSS3ServicesForBucketsImpl;
import r01f.cloud.aws.s3.model.AWSS3BucketID;
import software.amazon.awssdk.services.s3.S3Client;

public class AWSS3ClientAPIDelegateForBuckets
  implements AWSS3ServicesForBuckets {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////	
	protected final AWSS3ServicesForBucketsImpl _serviceForBucketsImpl;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AWSS3ClientAPIDelegateForBuckets(final S3Client s3Client) {
		_serviceForBucketsImpl = new  AWSS3ServicesForBucketsImpl(s3Client);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public void createBucket(final AWSS3BucketID bucket) {
		_serviceForBucketsImpl.createBucket(bucket);
	}
	@Override
	public void deleteBucket(final AWSS3BucketID bucket) {
		_serviceForBucketsImpl.deleteBucket(bucket);
	}
	@Override
	public boolean existBucket(final AWSS3BucketID bucket) {
		return _serviceForBucketsImpl.existBucket(bucket);
	}
}
