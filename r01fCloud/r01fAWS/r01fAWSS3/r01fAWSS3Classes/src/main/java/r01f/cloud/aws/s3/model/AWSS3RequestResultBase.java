package r01f.cloud.aws.s3.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.debug.Debuggable;
import software.amazon.awssdk.services.s3.model.S3Response;

/**
 * A successfully deleted object.
 */
@Accessors(prefix="_")
public abstract class AWSS3RequestResultBase<SELF_TYPE extends AWSS3RequestResultBase<SELF_TYPE>>
     extends AWSS3RequestBase<SELF_TYPE>
	implements Debuggable {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter @Setter protected AWSS3Operation _operation;
	@Getter @Setter protected AWSS3RequestID _requestId;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
    public AWSS3RequestResultBase(final AWSS3BucketID bucket,final AWSS3ObjectKey key,
    						      final AWSS3Operation operation) {
    	super(bucket,key);
    	_operation = operation;
    }

/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unchecked")
	public <R extends S3Response> SELF_TYPE copyFieldsFrom(final S3Response res) {
		_requestId = AWSS3RequestID.forId(res.responseMetadata().requestId());
		return (SELF_TYPE)this;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
 	@Override
	public CharSequence debugInfo() {
		StringBuilder outDbgInfo = new StringBuilder();
	    outDbgInfo.append("s3 operation result for ")
	              .append(_operation)
	              .append(" at bucket/key=")
	              .append(_bucket)
	              .append("/").append(_key);
	    return outDbgInfo.toString();
	}
}
