package r01f.messaging.kafka.consumer;


import org.apache.kafka.clients.consumer.KafkaConsumer;

import r01f.messaging.consumer.MessagingConsumerService;
import r01f.messaging.kafka.KafkaMessagingService;
import r01f.messaging.kafka.model.KafkaMessageSubscriber;

public interface KafkaConsumerService<K,V>
		 extends MessagingConsumerService<V,KafkaMessageSubscriber> ,
                 KafkaMessagingService {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * This method return the inner Kafka Consumer
	 * .... that could be need for some third party frameworks
	 *   See also //https://dattell.com/data-architecture-blog/understanding-kafka-consumer-offset/
	 * @return
	 */
	public KafkaConsumer<K,V> getKafkaConsumer();

	public KafkaConsumerServiceConfig getConsumerServiceConfig();


}
