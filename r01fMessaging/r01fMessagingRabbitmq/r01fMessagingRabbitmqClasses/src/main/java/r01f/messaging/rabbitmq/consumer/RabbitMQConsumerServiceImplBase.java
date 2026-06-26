package r01f.messaging.rabbitmq.consumer;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.impl.CredentialsProvider;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.messaging.rabbitmq.RabbitMQMessagingServiceConfig;
import r01f.messaging.rabbitmq.serialization.RabbitMQDeserializer;

@Accessors(prefix="_")
public abstract class RabbitMQConsumerServiceImplBase<V>
		   implements RabbitMQConsumerService<V> {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter final RabbitMQConsumerServiceConfig _consumerServiceConfig;
	@Getter final RabbitMQDeserializer<V> _deserializer;
	@Getter final ConnectionFactory _connectionFactory;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
	public RabbitMQConsumerServiceImplBase(final RabbitMQConsumerServiceConfig consumerServiceConfig,
			                               final RabbitMQDeserializer<V> deserializer){
		_consumerServiceConfig = consumerServiceConfig;
		_deserializer = deserializer;
		_connectionFactory = _buildConnectionFactory(consumerServiceConfig);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	protected ConnectionFactory _buildConnectionFactory(final RabbitMQMessagingServiceConfig config) {
		ConnectionFactory connectionFactory = new ConnectionFactory();
		connectionFactory.setHost(_consumerServiceConfig.getHost().asString());
		connectionFactory.setPort(_consumerServiceConfig.getPort());
		connectionFactory.setCredentialsProvider(new CredentialsProvider() { // guest/guest
														@Override
														public String getUsername() {
															return _consumerServiceConfig.getLoginId().asString();
														}

														@Override
														public String getPassword() {
															return _consumerServiceConfig.getPassword().asString();
														}
												 });
		return connectionFactory;
	}
}
