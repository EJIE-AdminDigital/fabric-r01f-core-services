

package r01f.cloud.aws.s3.events.spring.http.listener;


import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import r01f.cloud.aws.s3.events.consumer.AWSS3EventBridgeNotification;
import r01f.cloud.aws.s3.events.listener.AWSS3EventBridgeNotificationListenerBase;
import r01f.cloud.aws.s3.events.listener.AWSS3ObjectRetriever;
import r01f.cloud.aws.s3.events.listener.AWSS3ObjectRetrieverAsync;
import r01f.objectstreamer.Marshaller;

@Slf4j
@RestController
@RequestMapping("/api/v1/webhooks/s3/bridge") // 
public abstract class AWSS3EventBridgeNotificationWebhookControllerListenerBase 
		extends AWSS3EventBridgeNotificationListenerBase {
///////////////////////////////////////////////////////////////////////////
///  CONSTRUCTORS
///////////////////////////////////////////////////////////////////////////
    public AWSS3EventBridgeNotificationWebhookControllerListenerBase(final Marshaller marshaller) {
       
        super(marshaller,null,null);
    }
    protected AWSS3EventBridgeNotificationWebhookControllerListenerBase(final Marshaller marshaller,
						    							  			    final AWSS3ObjectRetriever objectRetriever,
						    							  			    final AWSS3ObjectRetrieverAsync objectRetrieverAsync) {
		super(marshaller, objectRetriever, objectRetrieverAsync);
	}
    protected AWSS3EventBridgeNotificationWebhookControllerListenerBase(final Marshaller marshaller,												
													  					final AWSS3ObjectRetrieverAsync objectRetrieverAsync) {
    	super(marshaller, null, objectRetrieverAsync);
    }
///////////////////////////////////////////////////////////////////////////
///  
///////////////////////////////////////////////////////////////////////////
    @Override
	public abstract void  handleEvent(final AWSS3EventBridgeNotification notification);
//////////////////////////////////////////////////////////////////////////////////
// (ECHO / PING)
//////////////////////////////////////////////////////////////////////////////////
    @GetMapping("/hi") // http://localhost:8080/api/v1/webhooks/s3/hi
	public String hi() {
		log.info("=== Say Hi Endpoint Called ===");
		return "Hi! REST Listener is up and running.";
	}
///////////////////////////////////////////////////////////////////////////
///  S3 EVENT RECEIVER
///////////////////////////////////////////////////////////////////////////
    @PostMapping( consumes = MediaType.APPLICATION_JSON_VALUE,
                  produces = MediaType.APPLICATION_JSON_VALUE
    )    
    public ResponseEntity<Void> onMessage(@RequestBody final AWSS3EventBridgeNotification notification) {
        try {           
            handleEvent(notification);
            return ResponseEntity.noContent().build();
        } catch (final Throwable e) {
            log.error(" Webhook HTTP de S3", e);            
            return ResponseEntity.internalServerError().build();
        }
    }
}