package r01f.cloud.aws.s3.api.interfaces.impl;

import java.util.Collection;

import lombok.extern.slf4j.Slf4j;
import r01f.cloud.aws.s3.api.interfaces.AWSS3ServicesForBuckets;
import r01f.cloud.aws.s3.model.AWSS3BucketID;
import r01f.util.types.collections.CollectionUtils;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteBucketRequest;
import software.amazon.awssdk.services.s3.model.ListBucketsRequest;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;

@Slf4j
public class AWSS3ServicesForBucketsImpl
	 extends AWSS3ServicesBaseImpl
  implements AWSS3ServicesForBuckets {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AWSS3ServicesForBucketsImpl(final S3Client s3Client)  {
		super(s3Client);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CREATE / DELETE BUCKET
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean existBucket(final AWSS3BucketID bucket) {
		log.warn("existBucket  bucket {}?",
				 bucket);
		ListBucketsRequest req = ListBucketsRequest.builder()
												   .build();
		ListBucketsResponse res = _s3Client.listBuckets(req);
		Collection<Bucket> buckets = res.buckets();
		boolean exists = false;
		if (CollectionUtils.hasData(buckets)) {
			for (Bucket b : buckets) {
				if (b.name().equals(bucket.getId())) {
					exists = true;
					break;
				}
			}
		}
		return exists;
	}
	@Override
	public void createBucket(final AWSS3BucketID bucket) {
		log.warn("Create  bucket {}",
				 bucket);
		CreateBucketRequest req = CreateBucketRequest.builder()
													 .bucket(bucket.getId())
													 .build();
		_s3Client.createBucket(req);
	}
	@Override
	public void deleteBucket(final AWSS3BucketID bucketName) {
		log.warn("Delete  bucket {}",
				 bucketName);
		DeleteBucketRequest req = DeleteBucketRequest.builder()
													 .bucket(bucketName.getId())
													 .build();
		_s3Client.deleteBucket(req);
	}
}
