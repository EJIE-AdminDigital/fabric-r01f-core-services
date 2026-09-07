package r01f.messaging.kafka.test.spring;



import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import jakarta.inject.Provider;
import lombok.extern.slf4j.Slf4j;
import r01f.cloud.aws.s3.events.consumer.AWSS3EventNotification;
import r01f.cloud.aws.s3.events.listener.AWSS3ObjectRetrieverAsync;
import r01f.cloud.aws.s3.events.spring.kafka.listener.AWSS3KafkaListenerBase;
import r01f.cloud.aws.s3.model.AWSS3ObjectKey;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;



@Slf4j
@Component // 
public class Z99AWSS3KafkaEventListener 
		extends AWSS3KafkaListenerBase {

	protected Z99AWSS3KafkaEventListenerDelegate  _delegate;
//////////////////////////////////////////////////////////////////////////////////
// CONSTRUCTOR
////////////////////////////////////////////////////////////////////////////////
    public Z99AWSS3KafkaEventListener(final Provider<SecurityContext> securityContextProvider,  
    								  final Marshaller marshaller, 
    		                          final AWSS3ObjectRetrieverAsync objectRetrieverAsync) {
        super(marshaller,objectRetrieverAsync);
        _delegate = new Z99AWSS3KafkaEventListenerDelegate (securityContextProvider,
															marshaller,
															objectRetrieverAsync);
    }
//////////////////////////////////////////////////////////////////////////////////
// ON MESSAGE 
////////////////////////////////////////////////////////////////////////////////
    @KafkaListener(  topics =  "dena-proxy-metadata-applicant-kafka-topic",
    				 groupId = "z99-s3-notification-consumer-group"
    )
    @Override
    public void onMessage(final ConsumerRecord<AWSS3ObjectKey,AWSS3EventNotification> record) {
       super.onMessage(record);
    }
   
//////////////////////////////////////////////////////////////////////////////////
// HANDLE 
////////////////////////////////////////////////////////////////////////////////
	@Override
	public void handleEvent(final AWSS3EventNotification notification) {
		 _delegate.process(notification);
		
	}
}