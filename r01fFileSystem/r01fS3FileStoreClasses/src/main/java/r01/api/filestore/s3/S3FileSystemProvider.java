package r01.api.filestore.s3;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.cloud.aws.s3.client.api.AWSS3BucketConfig;
import r01f.cloud.aws.s3.client.api.AWSS3ClientAPI;
import r01f.cloud.aws.s3.client.api.AWSS3ClientConfig;

@Accessors(prefix="_")
class S3FileSystemProvider {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The S3 config
	 */
	@Getter
	final AWSS3ClientConfig _s3Config;
	/**
	 * The S3 api
	 */
	@Getter
	final AWSS3ClientAPI _s3Api;
	/**
	 * The S3 bucket config
	 */
	@Getter
	final AWSS3BucketConfig _s3BucketConfig;
	 
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	 S3FileSystemProvider(final AWSS3ClientConfig _clientConfig) {
		 _s3Config = _clientConfig;
		 _s3Api = new AWSS3ClientAPI(_clientConfig);
		 _s3BucketConfig = null;
	 }
	 
	 S3FileSystemProvider(final AWSS3ClientConfig _clientConfig, 
			 			  final AWSS3BucketConfig _bucketConfig) {
		 _s3Config = _clientConfig;
		 _s3Api = new AWSS3ClientAPI(_clientConfig);
		 _s3BucketConfig = _bucketConfig;
	 }
}
