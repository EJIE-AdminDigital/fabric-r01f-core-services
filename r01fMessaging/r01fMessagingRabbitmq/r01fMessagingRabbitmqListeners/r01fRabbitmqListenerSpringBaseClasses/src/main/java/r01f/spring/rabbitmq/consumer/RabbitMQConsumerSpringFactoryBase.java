package r01f.spring.rabbitmq.consumer;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionListener;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.messaging.rabbitmq.consumer.RabbitMQConsumerService;

@Accessors(prefix="_")
public  class RabbitMQConsumerSpringFactoryBase<V>
		implements ConnectionFactory {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
    @Getter	protected final RabbitMQConsumerService<V> _consumerService;
    @Getter protected final ConnectionFactory _innerConnectionFactory; //.. wrapped connection factoRy
/////////////////////////////////////////////////////////////////////////////////////////
// CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	 public RabbitMQConsumerSpringFactoryBase( final RabbitMQConsumerService<V> consumerService ) {
		 _consumerService = consumerService;
		 _innerConnectionFactory = createConnectionFactory(consumerService);
	 }
/////////////////////////////////////////////////////////////////////////////////////////
// CONNECTION FATORRY
/////////////////////////////////////////////////////////////////////////////////////////
    ConnectionFactory createConnectionFactory(final RabbitMQConsumerService<V>  consumerService) {
		com.rabbitmq.client.ConnectionFactory rabbitConnectionFactory = consumerService.getConnectionFactory();
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory(rabbitConnectionFactory);
		return connectionFactory;
	}
/////////////////////////////////////////////////////////////////////////////////////////
// METHODS TO IMPLEMENT
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Connection createConnection() throws AmqpException {
		return _innerConnectionFactory.createConnection();
	}
	@Override
	public String getHost() {
		return _innerConnectionFactory.getHost();
	}
	@Override
	public int getPort() {
		return _innerConnectionFactory.getPort();
	}
	@Override
	public String getVirtualHost() {
		return _innerConnectionFactory.getVirtualHost();
	}
	@Override
	public String getUsername() {
		return _innerConnectionFactory.getUsername();
	}
	@Override
	public void addConnectionListener(final ConnectionListener listener) {
		_innerConnectionFactory.addConnectionListener(listener);

	}
	@Override
	public boolean removeConnectionListener(final ConnectionListener listener) {
		return _innerConnectionFactory.removeConnectionListener(listener);
	}
	@Override
	public void clearConnectionListeners() {
		_innerConnectionFactory.clearConnectionListeners();
	}
}
