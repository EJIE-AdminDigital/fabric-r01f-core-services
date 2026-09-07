package r01f.cloud.aws.s3.events.consumer;

import r01f.cloud.aws.s3.model.AWSS3BucketID;
import r01f.cloud.aws.s3.model.AWSS3ObjectKey;
import r01f.cloud.aws.s3.model.AWSS3Operation;

public interface AWSS3Event {
   
	public AWSS3BucketID   getBucketName();
	public AWSS3ObjectKey  getObjectKey();
    public Long getObjectSize();
    public AWSS3Operation getOperation();
    
    /**
     * From native de S3/MinIO.
     */
    default boolean isNativeNotification() {
        return this instanceof AWSS3EventNotification;
    }

    /**
     * From AWS EventBridge.
     */
    default boolean isEventBridge() {
        return this instanceof AWSS3EventBridgeNotification;
    }
}
