package r01f.cloud.aws.s3.model;

import java.io.InputStream;
import java.util.Collection;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.mime.MimeType;
import r01f.mime.MimeTypes;
import r01f.util.types.collections.Lists;


@Accessors(prefix="_")
public class AWSS3ObjectPutRequest
	 extends AWSS3RequestBase<AWSS3ObjectPutRequest> {
/////////////////////////////////////////////////////////////////////////////////////////
//FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter @Setter private transient InputStream _streamToUpload;
	@Getter @Setter private  Collection<AWSS3ObjectMetaDataItem> _customMetadata;
	@Getter @Setter private MimeType _contentType = MimeTypes.OCTECT_STREAM;	
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR / BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public AWSS3ObjectPutRequest(final AWSS3Bucket bucket,final AWSS3ObjectKey key,
                                 final InputStream streamToUpload,
                                 final MimeType contentType,
                                 final AWSS3ObjectMetaDataItem... customMetadata ) {
		this(bucket,key,streamToUpload,Lists.newArrayList(customMetadata),contentType);
	}
	public AWSS3ObjectPutRequest(final AWSS3Bucket bucket,final AWSS3ObjectKey key,
                                 final InputStream streamToUpload,
                                 final AWSS3ObjectMetaDataItem... customMetadata ) {
		this(bucket,key,streamToUpload, Lists.newArrayList(customMetadata));
	}
	public AWSS3ObjectPutRequest(final AWSS3Bucket bucket,final AWSS3ObjectKey key,
						         final InputStream streamToUpload) {
		super(bucket,key);
		_streamToUpload = streamToUpload;
	}
	public AWSS3ObjectPutRequest(final AWSS3Bucket bucket,final AWSS3ObjectKey key,
						         final InputStream streamToUpload,
						         final MimeType contentType) {
		super(bucket,key);
		_streamToUpload = streamToUpload;
		_contentType = contentType;
	}
	public AWSS3ObjectPutRequest(final AWSS3Bucket bucket,final AWSS3ObjectKey key,
			                     final InputStream streamToUpload,
			                     final Collection<AWSS3ObjectMetaDataItem> customMetadata) {
		super(bucket,key);
		_streamToUpload = streamToUpload;
		_customMetadata = customMetadata;
	}
	public AWSS3ObjectPutRequest(final AWSS3Bucket bucket,final AWSS3ObjectKey key,
			                     final InputStream streamToUpload,
			                     final Collection<AWSS3ObjectMetaDataItem> customMetadata,
			                     final MimeType contentType) {
		super(bucket,key);
		_streamToUpload = streamToUpload;
		_customMetadata = customMetadata;
		_contentType = contentType;
	}

}
