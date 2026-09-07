package r01f.messaging.http.test.events.bridge.spring.app;




import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.inject.Provider;
import lombok.extern.slf4j.Slf4j;
import r01f.cloud.aws.s3.events.consumer.AWSS3EventBridgeNotification;
import r01f.cloud.aws.s3.events.listener.AWSS3ObjectRetrieverAsync;
import r01f.cloud.aws.s3.events.spring.http.listener.AWSS3EventBridgeNotificationWebhookControllerListenerBase;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;



@Slf4j
@RestController
@RequestMapping("/api/v1/webhooks/s3/bridge") // 
public class Z99AWSS3EventBridgeWebhookControllerListener 
		extends AWSS3EventBridgeNotificationWebhookControllerListenerBase {
	
	Z99AWSS3EventBridgeWebhookControllerListenerDelegate _delegate;
//////////////////////////////////////////////////////////////////////////////////
// CONSTRUCTOR
////////////////////////////////////////////////////////////////////////////////
    public Z99AWSS3EventBridgeWebhookControllerListener(final Provider<SecurityContext> securityContextProvider,                            
	    											    final Marshaller marshaller, 
	    		                                        final AWSS3ObjectRetrieverAsync asyncRetriever) {
       super(marshaller);       
       _delegate = new Z99AWSS3EventBridgeWebhookControllerListenerDelegate (securityContextProvider,
    		   												                 marshaller,
    		                                                                 asyncRetriever);
    }
//////////////////////////////////////////////////////////////////////////////////
// ON MESSAGE 
///////////////////////////////////////////////////////////////////////////////////
    @Override
    public void handleEvent(final AWSS3EventBridgeNotification notification) {
    	 log.warn(" [] === Webhook HTTP S3  Event Bridge Notification Received ===");
         // Just Log
    	 if (notification != null 
         		&& notification != null) {               
	                String cleanJson = _marshaller.forWriting().toJson(notification);
	                log.warn("Received Payload:\n{}", cleanJson);	               
	                
	           log.warn("Received HTTP Event Bridge Webhook: {} , operation = '{}'",
	        		   								notification.getObjectKey().asString() , 
	        		                                 notification.getOperation());
             
         }
    	 log.warn(" [] now process the event.....");
    	 _delegate.process(notification);
    }  
//////////////////////////////////////////////////////////////////////////////////
//
///////////////////////////////////////////////////////////////////////////////////
}