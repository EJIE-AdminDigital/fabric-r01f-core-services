package r01f.messaging.kafka.test;

import lombok.extern.slf4j.Slf4j;
import r01f.cloud.aws.s3.events.consumer.AWSS3EventNotification;
import r01f.cloud.aws.s3.events.kafka.consumer.AWSS3KafkaConsumerServiceConfig;
import r01f.cloud.aws.s3.events.kafka.consumer.AWSS3KafkaConsumerServiceImplBase;
import r01f.cloud.aws.s3.events.kafka.model.AWSS3KafkaMessageSubscriber;
import r01f.objectstreamer.Marshaller;

@Slf4j
public class Z99S3EventNotificationConsumer 
		extends   AWSS3KafkaConsumerServiceImplBase {

	public Z99S3EventNotificationConsumer(final AWSS3KafkaConsumerServiceConfig config, 
			                              final Marshaller marshaller) {
		super(config, marshaller);
		
	}

	@Override
	public void registerSubscriber(final AWSS3KafkaMessageSubscriber subscriber) {
		log.warn(":::registerSubscriber: ");
		
	}

	@Override
	public void unregisterSubscriber(final AWSS3KafkaMessageSubscriber subscriber) {
		log.warn(":::unregisterSubscriber: ");
		
	}

	@Override
	public void consumeEvent(final AWSS3EventNotification eventNotification) {
		log.warn(":::consumeEvent: ");
		
	}

}
