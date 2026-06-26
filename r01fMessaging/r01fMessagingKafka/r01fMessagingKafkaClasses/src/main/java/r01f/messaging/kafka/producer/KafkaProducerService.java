package r01f.messaging.kafka.producer;


import r01f.messaging.kafka.KafkaMessagingService;
import r01f.messaging.kafka.model.KafkaMessageSubscriber;
import r01f.messaging.model.MessagingFactory;
import r01f.messaging.producer.MessagingProducerService;
import r01f.messaging.producer.ProducerServiceResponse;

public interface KafkaProducerService<K,V>
		 extends MessagingProducerService<V,KafkaMessageSubscriber> ,
                 KafkaMessagingService	{
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public abstract ProducerServiceResponse<KafkaMessageSubscriber> send(final KafkaMessageSubscriber to,
                                                                         final MessagingFactory<V> messageToBeDeliveredFactory);
}
