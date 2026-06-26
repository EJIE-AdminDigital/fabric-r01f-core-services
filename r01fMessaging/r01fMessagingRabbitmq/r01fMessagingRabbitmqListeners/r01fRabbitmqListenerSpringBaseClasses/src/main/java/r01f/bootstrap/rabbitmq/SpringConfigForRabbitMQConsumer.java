package r01f.bootstrap.rabbitmq;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;

public interface SpringConfigForRabbitMQConsumer<V> {

	public ConnectionFactory connectionFactory();

	public <C  extends MessageListenerContainer> RabbitListenerContainerFactory<C> rabbitListenerContainerFactory();

    public abstract AmqpAdmin amqpAdmin() ;

}
