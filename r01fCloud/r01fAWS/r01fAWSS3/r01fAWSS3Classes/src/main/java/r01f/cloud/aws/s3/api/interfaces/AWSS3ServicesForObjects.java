package r01f.cloud.aws.s3.api.interfaces;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;

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


public interface AWSS3ServicesForObjects {
/////////////////////////////////////////////////////////////////////////////////////////
//  PUT
/////////////////////////////////////////////////////////////////////////////////////////
	public AWSS3ObjectPutResult putObject(final AWSS3ObjectPutRequest putRequest);

	public AWSS3ObjectPutResult putObject(final AWSS3Bucket bucket,final AWSS3ObjectKey key,
							     	      final File file,
							     	      final Collection<AWSS3ObjectMetaDataItem> customMetadata);

	public AWSS3ObjectPutResult putObject(final AWSS3Bucket bucket,final AWSS3ObjectKey key,
							     	      final byte[] bytes,
							     	      final Collection<AWSS3ObjectMetaDataItem> customMetadata);

	public AWSS3ObjectPutResult putObject(final AWSS3Bucket bucket,final AWSS3ObjectKey key,
							     	      final InputStream stream,
							     	      final Collection<AWSS3ObjectMetaDataItem> customMetadata);
	
	public AWSS3ObjectPutResult putObject(final AWSS3Bucket bucket,final AWSS3ObjectKey key,
							     	      final File file,
							     	      final Collection<AWSS3ObjectMetaDataItem> customMetadata,
							     	      final MimeType contentType);

	public AWSS3ObjectPutResult putObject(final AWSS3Bucket bucket,final AWSS3ObjectKey key,
							     	      final byte[] bytes,
							     	      final Collection<AWSS3ObjectMetaDataItem> customMetadata,
							     	      final MimeType contentType);

	public AWSS3ObjectPutResult putObject(final AWSS3Bucket bucket,final AWSS3ObjectKey key,
							     	      final InputStream stream,
							     	      final Collection<AWSS3ObjectMetaDataItem> customMetadata,
							     	      final MimeType contentType);

/////////////////////////////////////////////////////////////////////////////////////////
// APPEND
/////////////////////////////////////////////////////////////////////////////////////////
	public AWSS3ObjectPutResult appendToObject(final AWSS3Bucket bucket,final AWSS3ObjectKey key,
									   	 	   final InputStream streamToUpload,
									   	 	   final Collection<AWSS3ObjectMetaDataItem> customMetadata);

/////////////////////////////////////////////////////////////////////////////////////////
// PUT BIG OBJECT
/////////////////////////////////////////////////////////////////////////////////////////
	public AWSS3ObjectPutResult putHugeObject(final AWSS3Bucket bucket,final AWSS3ObjectKey key,
				    					  	  final File file,
				    					  	  final AWSS3OperationSettings operationSettings);
    					  	  
    public AWSS3ObjectPutResult putHugeObject(final AWSS3Bucket bucket,final AWSS3ObjectKey key,
				    					  	  final File file,
				    					  	  final AWSS3OperationSettings operationSettings,
				    					  	  final Collection<AWSS3ObjectMetaDataItem> customMetadata);
    
    public AWSS3ObjectPutResult putHugeObject(final AWSS3Bucket bucket,final AWSS3ObjectKey key,
				    					  	  final File file,
				    					  	  final AWSS3OperationSettings operationSettings,
				    					  	  final Collection<AWSS3ObjectMetaDataItem> customMetadata,
											  final MimeType contentType);
    
/////////////////////////////////////////////////////////////////////////////////////////
//  GET
/////////////////////////////////////////////////////////////////////////////////////////
    public AWSS3ObjectGetResult getObject(final AWSS3ObjectGetRequest getRequest);

	public AWSS3ObjectGetResult getObject(final AWSS3Bucket bucket,final AWSS3ObjectKey key);
	
	public AWSS3ObjectGetResult getObject(final AWSS3Bucket bucket,final AWSS3ObjectKey key, final AWSS3Range range);

	public void getHugeObject(final AWSS3Bucket bucket,final AWSS3ObjectKey key,
							  final AWSS3OperationSettings operationSettings);
/////////////////////////////////////////////////////////////////////////////////////////
//	HEAD
/////////////////////////////////////////////////////////////////////////////////////////
	public AWSS3ObjectHeadResult headObject(final AWSS3Bucket bucket,final AWSS3ObjectKey key);
/////////////////////////////////////////////////////////////////////////////////////////
//	DELETE
/////////////////////////////////////////////////////////////////////////////////////////
	public AWSS3ObjectDeleteResult deleteObject(final AWSS3Bucket bucket,final AWSS3ObjectKey key);
/////////////////////////////////////////////////////////////////////////////////////////
//	COPY
/////////////////////////////////////////////////////////////////////////////////////////
	public AWSS3ObjectCopyResult copyObject(final AWSS3Bucket srcBucket,final AWSS3ObjectKey srcKey,
											final AWSS3Bucket dstBucket,final AWSS3ObjectKey dstKey);
}
