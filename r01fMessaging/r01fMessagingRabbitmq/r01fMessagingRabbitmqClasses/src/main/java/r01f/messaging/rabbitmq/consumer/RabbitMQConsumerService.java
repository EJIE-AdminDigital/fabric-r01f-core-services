package r01f.messaging.rabbitmq.consumer;

import com.rabbitmq.client.ConnectionFactory;

import r01f.messaging.consumer.MessagingConsumerService;
import r01f.messaging.rabbitmq.model.RabbitMQMessageSubscriber;
import r01f.messaging.rabbitmq.serialization.RabbitMQDeserializer;
public interface RabbitMQConsumerService<V>
		extends MessagingConsumerService<V,RabbitMQMessageSubscriber> {
/////////////////////////////////////////////////////////////////////////////////////////

/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * This method return the inner Rabbit ConnectionFactory
	 * .... that could be need for some third party frameworks
	 *
	 * @return
	 */
	public ConnectionFactory getConnectionFactory();

	public RabbitMQConsumerServiceConfig getConsumerServiceConfig();

	public RabbitMQDeserializer<V> getDeserializer();
}
