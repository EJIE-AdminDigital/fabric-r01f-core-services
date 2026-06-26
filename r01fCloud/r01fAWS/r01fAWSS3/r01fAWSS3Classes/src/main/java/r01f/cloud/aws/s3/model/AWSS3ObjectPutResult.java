package r01f.cloud.aws.s3.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.types.url.Url;
import r01f.util.types.Strings;
import software.amazon.awssdk.services.s3.model.AbortMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;


@Accessors(prefix="_")
public class AWSS3ObjectPutResult
	 extends AWSS3RequestResultBase<AWSS3ObjectPutResult> {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter @Setter private AWSS3ObjectVersionID _versionId;
	@Getter @Setter private AWSS3ObjectETag _eTag;
	@Getter @Setter private Url _location;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR / BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public AWSS3ObjectPutResult(final AWSS3Bucket bucket,final AWSS3ObjectKey key) {
		super(bucket,key,
			  AWSS3RequestedOperation.PUT);
	}
	public static AWSS3ObjectPutResultBuilderInputStreamStep fromPutObjectResponseOn(final AWSS3Bucket bucket,final AWSS3ObjectKey key) {
		AWSS3ObjectPutResult res = new AWSS3ObjectPutResult(bucket,key);
		return res.new AWSS3ObjectPutResultBuilderInputStreamStep();
	}
	@NoArgsConstructor(access=AccessLevel.PRIVATE)
	public class AWSS3ObjectPutResultBuilderInputStreamStep {

		public AWSS3ObjectPutResult with(final PutObjectResponse putRes) {
			if (Strings.isNOTNullOrEmpty(putRes.versionId())) _versionId = AWSS3ObjectVersionID.forId(putRes.versionId());
			if (Strings.isNOTNullOrEmpty(putRes.eTag())) _eTag = AWSS3ObjectETag.forId(putRes.eTag());

			return AWSS3ObjectPutResult.this;
		}
		public AWSS3ObjectPutResult with(final CompleteMultipartUploadResponse multipartUploadRes) {
			if (Strings.isNOTNullOrEmpty(multipartUploadRes.versionId())) _versionId = AWSS3ObjectVersionID.forId(multipartUploadRes.versionId());
			if (Strings.isNOTNullOrEmpty(multipartUploadRes.eTag())) _eTag = AWSS3ObjectETag.forId(multipartUploadRes.eTag());
			if (Strings.isNOTNullOrEmpty(multipartUploadRes.location())) _location = Url.from(multipartUploadRes.location());
			return AWSS3ObjectPutResult.this;
		}
		public AWSS3ObjectPutResult with(final AbortMultipartUploadResponse abortMultipartUploadRes) {
			return AWSS3ObjectPutResult.this;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	DEBUG
/////////////////////////////////////////////////////////////////////////////////////////
 	@Override
	public CharSequence debugInfo() {
 		StringBuilder dbg = new StringBuilder(super.debugInfo());
 		return dbg;
	}
}
