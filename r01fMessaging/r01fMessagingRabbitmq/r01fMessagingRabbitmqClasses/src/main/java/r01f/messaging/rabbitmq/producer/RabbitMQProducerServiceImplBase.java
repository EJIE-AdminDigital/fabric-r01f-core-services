package r01f.messaging.rabbitmq.producer;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.impl.CredentialsProvider;

import r01f.messaging.model.MessagingFactory;
import r01f.messaging.producer.ProducerServiceResponse;
import r01f.messaging.producer.ProducerServiceResponseOK;
import r01f.messaging.rabbitmq.RabbitMQMessagingServiceConfig;
import r01f.messaging.rabbitmq.model.RabbitMQMessageSubscriber;
import r01f.messaging.rabbitmq.serialization.RabbitMQSerializer;

public abstract class RabbitMQProducerServiceImplBase<V>
		   implements RabbitMQProducerService<V> {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////	
	final RabbitMQMessagingServiceConfig _config;
	final RabbitMQSerializer<V> _serializer;
	final ConnectionFactory _connectionFactory ;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
	public RabbitMQProducerServiceImplBase( final RabbitMQMessagingServiceConfig config,
			                                final RabbitMQSerializer<V> serializer ){
		_config = config;
		_connectionFactory = _buildConnectionFactory(config);
		_serializer = serializer;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	protected ConnectionFactory _buildConnectionFactory(final RabbitMQMessagingServiceConfig config) {
		ConnectionFactory connectionFactory =  new ConnectionFactory();
		connectionFactory.setHost(_config.getHost().asString());
		connectionFactory.setPort(_config.getPort());
		connectionFactory.setCredentialsProvider(new CredentialsProvider() { // guest/guest
														@Override
														public String getUsername() {
															return _config.getLoginId().asString();
														}
														@Override
														public String getPassword() {
															return _config.getPassword().asString();
														}});
		return connectionFactory;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public ProducerServiceResponse<RabbitMQMessageSubscriber> send(final RabbitMQMessageSubscriber subscriber,
			                                                       final MessagingFactory<V> messageToBeDeliveredFactory) {
		Connection connection = null;
		Channel channel = null;
		try {
			connection = _connectionFactory.newConnection();
		    channel = connection.createChannel();
			String QUEUE_NAME = subscriber.getQueue().asString();
			channel.queueDeclare(QUEUE_NAME, true, false, false, null);
			byte[] message  = _serializer.serialize(messageToBeDeliveredFactory.getValue());

			channel.basicPublish("", QUEUE_NAME, null, message);


		} catch (final Throwable th) {
			th.printStackTrace();
		} finally {
			try {
				if (channel != null ) channel.close();
				if (connection != null) connection.close();
			} catch (IOException | TimeoutException e) {
				e.printStackTrace();
			}
		}
		return new ProducerServiceResponseOK<>(subscriber);
	}
}
