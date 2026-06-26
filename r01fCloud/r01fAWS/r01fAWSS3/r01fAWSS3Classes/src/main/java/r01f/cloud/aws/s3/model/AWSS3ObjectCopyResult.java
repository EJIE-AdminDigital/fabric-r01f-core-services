package r01f.cloud.aws.s3.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.util.types.Strings;
import software.amazon.awssdk.services.s3.model.CopyObjectResponse;

/**
 * A successfully copy object.
 */
@Accessors(prefix="_")
public class AWSS3ObjectCopyResult
     extends AWSS3RequestResultBase<AWSS3ObjectCopyResult> {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter @Setter private AWSS3ObjectVersionID _versionId;
	@Getter @Setter private AWSS3Bucket _dstBucket;
	@Getter @Setter private AWSS3ObjectKey _dstKey;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR / BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public AWSS3ObjectCopyResult(final AWSS3Bucket bucket,final AWSS3ObjectKey key) {
		super(bucket,key,
			  AWSS3RequestedOperation.COPY);
	}
	public static AWSS3CopyResultBuilderStep fromCopyObjectResponseOn(final AWSS3Bucket bucket,final AWSS3ObjectKey key) {
		AWSS3ObjectCopyResult res = new AWSS3ObjectCopyResult(bucket,key);
		return res.new AWSS3CopyResultBuilderStep();
	}
	@NoArgsConstructor(access=AccessLevel.PRIVATE)
	public class AWSS3CopyResultBuilderStep {
		
		public AWSS3ObjectCopyResult to(final CopyObjectResponse copyRes,final AWSS3Bucket destinationBucket,final AWSS3ObjectKey destinationKey) {
			if (Strings.isNOTNullOrEmpty(copyRes.versionId())) _versionId = AWSS3ObjectVersionID.forId(copyRes.versionId());
			_dstBucket = destinationBucket;
			_dstKey = destinationKey;
			
			return AWSS3ObjectCopyResult.this;
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
