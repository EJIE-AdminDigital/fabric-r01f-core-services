package r01f.cloud.aws.s3.events.kafka.consumer;


import org.apache.kafka.clients.consumer.KafkaConsumer;

import r01f.cloud.aws.s3.events.consumer.AWSS3EventNotification;
import r01f.cloud.aws.s3.events.consumer.AWSS3EventServiceConsumerService;
import r01f.cloud.aws.s3.events.kafka.AWSS3KafkaEventService;
import r01f.cloud.aws.s3.events.kafka.model.AWSS3KafkaMessageSubscriber;
import r01f.cloud.aws.s3.model.AWSS3ObjectKey;

public interface AWSS3KafkaConsumerService
		 extends AWSS3EventServiceConsumerService<AWSS3KafkaMessageSubscriber> ,
                 AWSS3KafkaEventService {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * This method return the inner Kafka Consumer
	 * .... that could be need for some third party frameworks
	 *   See also //https://dattell.com/data-architecture-blog/understanding-kafka-consumer-offset/
	 * @return
	 */
	public KafkaConsumer<AWSS3ObjectKey,AWSS3EventNotification> getKafkaConsumer();

	public AWSS3KafkaConsumerServiceConfig getConsumerServiceConfig();


}
