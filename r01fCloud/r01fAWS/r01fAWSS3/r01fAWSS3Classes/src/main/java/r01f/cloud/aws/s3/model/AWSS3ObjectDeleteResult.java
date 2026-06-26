package r01f.cloud.aws.s3.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.util.types.Strings;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;

/**
 * A successfully deleted object.
 */
@Accessors(prefix="_")
public class AWSS3ObjectDeleteResult
     extends AWSS3RequestResultBase<AWSS3ObjectDeleteResult> {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter @Setter private AWSS3ObjectVersionID _versionId;
	@Getter @Setter private boolean _deleteMarker;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR / BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public AWSS3ObjectDeleteResult(final AWSS3Bucket bucket,final AWSS3ObjectKey key) {
		super(bucket,key,
			  AWSS3RequestedOperation.DELETE);
	}
	public static AWSS3DeleteResultBuilderStep fromDeleteObjectResponseOn(final AWSS3Bucket bucket,final AWSS3ObjectKey key) {
		AWSS3ObjectDeleteResult res = new AWSS3ObjectDeleteResult(bucket,key);
		return res.new AWSS3DeleteResultBuilderStep();
	}
	@NoArgsConstructor(access=AccessLevel.PRIVATE)
	public class AWSS3DeleteResultBuilderStep {
		
		public AWSS3ObjectDeleteResult with(final DeleteObjectResponse delRes) {
			// This functionality (versionId and deleteMarker) is not supported for directory buckets and minIO.
			if (Strings.isNOTNullOrEmpty(delRes.versionId())) _versionId = AWSS3ObjectVersionID.forId(delRes.versionId());
			if (delRes.deleteMarker() != null) _deleteMarker = delRes.deleteMarker();
			
			return AWSS3ObjectDeleteResult.this;
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
