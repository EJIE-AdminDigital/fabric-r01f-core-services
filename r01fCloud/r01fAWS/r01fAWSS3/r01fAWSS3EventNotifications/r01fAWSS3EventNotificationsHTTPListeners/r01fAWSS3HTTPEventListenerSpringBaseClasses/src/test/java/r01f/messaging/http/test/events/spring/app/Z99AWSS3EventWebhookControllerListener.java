package r01f.messaging.http.test.events.spring.app;




import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.inject.Provider;
import lombok.extern.slf4j.Slf4j;
import r01f.cloud.aws.s3.events.consumer.AWSS3EventNotification;
import r01f.cloud.aws.s3.events.listener.AWSS3ObjectRetrieverAsync;
import r01f.cloud.aws.s3.events.spring.http.listener.AWSS3EventNotificationWebhookControllerListenerBase;

import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;



@Slf4j
@RestController
@RequestMapping("/api/v1/webhooks/s3") // / 
public class Z99AWSS3EventWebhookControllerListener 
		extends AWSS3EventNotificationWebhookControllerListenerBase {
	
	Z99AWSS3EventWebhookControllerListenerDelegate _delegate;
//////////////////////////////////////////////////////////////////////////////////
// CONSTRUCTOR
////////////////////////////////////////////////////////////////////////////////
    public Z99AWSS3EventWebhookControllerListener(final Provider<SecurityContext> securityContextProvider,                            
    											  final Marshaller marshaller, 
    		                                      final AWSS3ObjectRetrieverAsync asyncRetriever) {
       super(marshaller);       
       _delegate = new Z99AWSS3EventWebhookControllerListenerDelegate (securityContextProvider,
    		   												           marshaller,
    		                                                           asyncRetriever);
    }
//////////////////////////////////////////////////////////////////////////////////
// ON MESSAGE 
///////////////////////////////////////////////////////////////////////////////////
    @Override
    public void handleEvent(final AWSS3EventNotification notification) {
    	 log.warn(" [] === Webhook HTTP S3 Native Event Received ===");
         // Just Log
    	 if (notification != null 
         		&& notification.records() != null) {               
	                String cleanJson = _marshaller.forWriting().toJson(notification);
	                log.warn("Received Payload:\n{}", cleanJson);	               
	                notification.firstRecord().ifPresent(record -> {
	                										log.warn("Prossing HTTP Webhook: {}", record.s3().object().key());
             });
         }
    	 log.warn(" [] now process the event.....");
    	 _delegate.process(notification);
    }  
//////////////////////////////////////////////////////////////////////////////////
//
///////////////////////////////////////////////////////////////////////////////////	
   
}