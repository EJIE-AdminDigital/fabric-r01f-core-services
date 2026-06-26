package r01f.cloud.aws.s3.client.api.delegates;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;

import r01f.cloud.aws.s3.api.interfaces.AWSS3ServicesForObjects;
import r01f.cloud.aws.s3.api.interfaces.impl.AWSS3ServicesForObjectsImpl;
import r01f.cloud.aws.s3.model.AWSS3Bucket;
import r01f.cloud.aws.s3.model.AWSS3ObjectCopyResult;
import r01f.cloud.aws.s3.model.AWSS3ObjectDeleteResult;
import r01f.cloud.aws.s3.model.AWSS3ObjectGetRequest;
import r01f.cloud.aws.s3.model.AWSS3ObjectGetResult;
import r01f.cloud.aws.s3.model.AWSS3ObjectHeadResult;
import r01f.cloud.aws.s3.model.AWSS3ObjectKey;
import r01f.cloud.aws.s3.model.AWSS3ObjectMetaDataItem;
import r01f.cloud.aws.s3.model.AWSS3ObjectPutRequest;
import r01f.cloud.aws.s3.model.AWSS3ObjectPutResult;
import r01f.cloud.aws.s3.model.AWSS3OperationSettings;
import r01f.cloud.aws.s3.model.AWSS3Range;
import r01f.mime.MimeType;
import software.amazon.awssdk.services.s3.S3Client;

public class AWSS3ClientAPIDelegateForObjects
  implements AWSS3ServicesForObjects {
///////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
///////////////////////////////////////////////////////////////////////////////////////////
	protected final AWSS3ServicesForObjectsImpl _serviceForObjectsImpl;

///////////////////////////////////////////////////////////////////////////////////////////
// 	CONSTRUTCTOR
///////////////////////////////////////////////////////////////////////////////////////////
	public AWSS3ClientAPIDelegateForObjects(final S3Client s3Client) {
		_serviceForObjectsImpl = new  AWSS3ServicesForObjectsImpl(s3Client);
	}
///////////////////////////////////////////////////////////////////////////////////////////
//	PUT
///////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public AWSS3ObjectPutResult putObject(final AWSS3ObjectPutRequest putRequest) {
		return _serviceForObjectsImpl.putObject(putRequest);
	}
	public AWSS3ObjectPutResult putObject(final AWSS3Bucket bucket,final AWSS3ObjectKey key,
									      final InputStream stream) {
		return _serviceForObjectsImpl.putObject(bucket,key,
												stream,
												null);		// no metadata
	}
	@Override
	public AWSS3ObjectPutResult putObject(final AWSS3Bucket bucket,final AWSS3ObjectKey key,
							   	 	      final InputStream stream,
							   	 	      final Collection<AWSS3ObjectMetaDataItem> customMetadata) {
		return _serviceForObjectsImpl.putObject(bucket,key,
												stream,
												customMetadata);
	}
	@Override
	public AWSS3ObjectPutResult putObject(final AWSS3Bucket bucket,final AWSS3ObjectKey key,
							     	      final byte[] bytes,
							     	      final Collection<AWSS3ObjectMetaDataItem> customMetadata) {
		return _serviceForObjectsImpl.putObject(bucket,key,
												bytes,
												customMetadata);
	}
	public AWSS3ObjectPutResult putObject(final AWSS3Bucket bucket,final AWSS3ObjectKey key,
							     	      final byte[] bytes) {
		return _serviceForObjectsImpl.putObject(bucket,key,
												bytes,
												null);	// no metadta
	}
	public AWSS3ObjectPutResult putObject(final AWSS3Bucket bucket,final AWSS3ObjectKey key,
							   	 	      final File file) {
		return _serviceForObjectsImpl.putObject(bucket,key,
												file,
												null);	// no metadata
	}
	@Override
	public AWSS3ObjectPutResult putObject(final AWSS3Bucket bucket,final AWSS3ObjectKey key,
									      final File file,
									      final Collection<AWSS3ObjectMetaDataItem> customMetadata) {
		return _serviceForObjectsImpl.putObject(bucket,key,
												file,
												customMetadata);
	}
	@Override
	public AWSS3ObjectPutResult putObject(final AWSS3Bucket bucket, final AWSS3ObjectKey key, final File file,
			final Collection<AWSS3ObjectMetaDataItem> customMetadata, final MimeType contentType) {
		return _serviceForObjectsImpl.putObject(bucket,key,
												file,
												customMetadata,
												contentType);
	}
	@Override
	public AWSS3ObjectPutResult putObject(final AWSS3Bucket bucket, final AWSS3ObjectKey key, final byte[] bytes,
			final Collection<AWSS3ObjectMetaDataItem> customMetadata, final MimeType contentType) {
		return _serviceForObjectsImpl.putObject(bucket,key,
												bytes,
												customMetadata,
												contentType);
	}
	@Override
	public AWSS3ObjectPutResult putObject(final AWSS3Bucket bucket, final AWSS3ObjectKey key, final InputStream stream,
			final Collection<AWSS3ObjectMetaDataItem> customMetadata, final MimeType contentType) {
		return _serviceForObjectsImpl.putObject(bucket,key,
												stream,
												customMetadata,
												contentType);
	}
/////////////////////////////////////////////////////////////////////////////////////////
// APPEND
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public AWSS3ObjectPutResult appendToObject(final AWSS3Bucket bucket,final AWSS3ObjectKey key,
									   	 	   final InputStream streamToUpload,
									   	 	   final Collection<AWSS3ObjectMetaDataItem> customMetadata) {
		return _serviceForObjectsImpl.appendToObject(bucket,key,
													 streamToUpload,
													 customMetadata);
	}
/////////////////////////////////////////////////////////////////////////////////////////
// PUT BIG OBJECT
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public AWSS3ObjectPutResult putHugeObject(final AWSS3Bucket bucket,final AWSS3ObjectKey key,
											  final File file,
											  final AWSS3OperationSettings operationSettings) {
		return _serviceForObjectsImpl.putHugeObject(bucket,key,
													 file,
													 operationSettings,
													 null);		// no metadata
	}
	@Override
	public AWSS3ObjectPutResult putHugeObject(final AWSS3Bucket bucket,final AWSS3ObjectKey key,
											  final File file,
											  final AWSS3OperationSettings operationSettings,
											  final Collection<AWSS3ObjectMetaDataItem> customMetadata) {
		return _serviceForObjectsImpl.putHugeObject(bucket,key,
													 file,
													 operationSettings,
													 customMetadata);
	}
	@Override
	public AWSS3ObjectPutResult putHugeObject(final AWSS3Bucket bucket, 
											  final AWSS3ObjectKey key,
											  final File file,
											  final AWSS3OperationSettings operationSettings,
											  final Collection<AWSS3ObjectMetaDataItem> customMetadata,
											  final MimeType contentType) {
		return _serviceForObjectsImpl.putHugeObject(bucket,key,
													 file,
													 operationSettings,
													 customMetadata,
													 contentType);		
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	HEAD
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public AWSS3ObjectHeadResult headObject(final AWSS3Bucket bucket,final AWSS3ObjectKey key) {
		return _serviceForObjectsImpl.headObject(bucket,key);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	GET
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public AWSS3ObjectGetResult getObject(final AWSS3ObjectGetRequest getRequest) {
		return _serviceForObjectsImpl.getObject(getRequest);
	}

	@Override
	public AWSS3ObjectGetResult getObject(final AWSS3Bucket bucket,final AWSS3ObjectKey key) {
		return _serviceForObjectsImpl.getObject(bucket,key);
	}
	@Override
	public AWSS3ObjectGetResult getObject(final AWSS3Bucket bucket,final AWSS3ObjectKey key, final AWSS3Range range) {
		return _serviceForObjectsImpl.getObject(bucket,key,range);
	}
	@Override
	public void getHugeObject(final AWSS3Bucket bucket,final AWSS3ObjectKey key,
							  final AWSS3OperationSettings operationSettings) {
		// TODO Auto-generated method stub

	}
/////////////////////////////////////////////////////////////////////////////////////////
//	DELETE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public AWSS3ObjectDeleteResult deleteObject(final AWSS3Bucket bucket,final AWSS3ObjectKey key) {
		return _serviceForObjectsImpl.deleteObject(bucket,key);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	COPY
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public AWSS3ObjectCopyResult copyObject(final AWSS3Bucket srcBucket,final AWSS3ObjectKey srcKey,
											final AWSS3Bucket dstBucket,final AWSS3ObjectKey dstKey) {
		return _serviceForObjectsImpl.copyObject(srcBucket,srcKey,
												 dstBucket,dstKey);
	}

}
