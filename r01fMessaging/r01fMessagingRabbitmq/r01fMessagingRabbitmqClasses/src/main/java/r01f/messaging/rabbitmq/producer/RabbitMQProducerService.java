package r01f.messaging.rabbitmq.producer;


import r01f.messaging.producer.MessagingProducerService;
import r01f.messaging.rabbitmq.RabbitMQMessagingService;
import r01f.messaging.rabbitmq.model.RabbitMQMessageSubscriber;

public interface RabbitMQProducerService<V>
		 extends MessagingProducerService<V,RabbitMQMessageSubscriber>,
                 RabbitMQMessagingService {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
}
