package r01f.cloud.aws.s3.events.consumer;

import r01f.cloud.aws.s3.events.AWSS3EventService;
import r01f.cloud.aws.s3.events.model.AWSS3EventServiceSubscriber;



/**
 * General event consumer service contract.
 * Clean version: S3EventNotification is hardcoded because the payload structure
 * is a standard across AWS, MinIO, and Scality.
 *
 * @param <S> The specific subscriber type dedicated to handling the unpacked events
 */
public interface AWSS3EventServiceConsumerService<S extends AWSS3EventServiceSubscriber> 
         extends AWSS3EventService {

/////////////////////////////////////////////////////////////////////////////////////////
//	SUBSCRIBER REGISTRATION
/////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Registers a business logic listener.
     */
    public void registerSubscriber(final S subscriber);

    /**
     * Removes an active business logic listener.
     */
    public void unregisterSubscriber(final S subscriber);

/////////////////////////////////////////////////////////////////////////////////////////
//	EVENT DISPATCHING
/////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Main entry point to process the incoming notification.
     * No generics here: it strictly expects the monolithic S3EventNotification object.
     *
     * @param eventNotification the standard S3 notification wrapper
     */
    public void consumeEvent(final AWSS3EventNotification eventNotification);
}