package r01f.cloud.aws.s3.events.listener;

import java.util.concurrent.CompletableFuture;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import r01f.cloud.aws.s3.events.consumer.AWSS3EventBridgeNotification;
import r01f.cloud.aws.s3.events.consumer.AWSS3EventNotification.S3EventRecord;
import r01f.cloud.aws.s3.model.AWSS3BucketID;
import r01f.cloud.aws.s3.model.AWSS3ObjectGetResult;
import r01f.cloud.aws.s3.model.AWSS3ObjectKey;
import r01f.objectstreamer.Marshaller;

@Slf4j
public abstract class AWSS3EventBridgeNotificationListenerBase 
	implements AWSS3EventListener<AWSS3EventBridgeNotification> {	
/////////////////////////////////////////////////////////////////////////////////////////
//FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter  protected final Marshaller _marshaller;	
	@Getter  protected final AWSS3ObjectRetriever 	  _objectRetriever;
	@Getter  protected final AWSS3ObjectRetrieverAsync _objectRetrieverAsync; 
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////	
	 public abstract void  handleEvent(final AWSS3EventBridgeNotification notification);
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	

	protected AWSS3EventBridgeNotificationListenerBase(final Marshaller marshaller,
													   final AWSS3ObjectRetriever objectRetriever,
									                   final AWSS3ObjectRetrieverAsync objectRetrieverAsync) {
        _objectRetriever = objectRetriever;
        _objectRetrieverAsync = objectRetrieverAsync;
        _marshaller = marshaller;
    }
	
/////////////////////////////////////////////////////////////////////////////////////////
//  RETRIEVER
/////////////////////////////////////////////////////////////////////////////////////////	
	protected AWSS3ObjectGetResult tryRetrieveObject(final S3EventRecord record) {
        if (_objectRetriever == null) {
            log.warn("[AWSS3ListenerBase] No retriever attached to this listener instance.");
            return null; 
        }

        AWSS3BucketID bucket = AWSS3BucketID.forId(record.s3().bucket().name());
        AWSS3ObjectKey key = AWSS3ObjectKey.forId(record.s3().object().key());

        log.warn("[AWSS3ListenerBase] Executing domain retriever for bucket '{}' and key '{}'", bucket, key);
        
        return _objectRetriever.retrieveObject(bucket, key);
    }
	
	
/////////////////////////////////////////////////////////////////////////////////////////
//RETRIEVER
/////////////////////////////////////////////////////////////////////////////////////////	
	protected CompletableFuture<AWSS3ObjectGetResult>  tryAsyncRetrieveObject(final S3EventRecord record) {
		if (_objectRetrieverAsync == null) {
			log.warn("[AWSS3ListenerBase] No objectRetrieverAsync attached to this listener instance.");
			return null; 
		}
		
		AWSS3BucketID bucket = AWSS3BucketID.forId(record.s3().bucket().name());
		AWSS3ObjectKey key = AWSS3ObjectKey.forId(record.s3().object().key());
		
		log.warn("[AWSS3ListenerBase] Executing domain retriever for bucket '{}' and key '{}'", bucket, key);
		
		return _objectRetrieverAsync.retrieveObject(bucket, key);
	}
}
