package r01f.bootstrap.rabbitmq;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;

/*@EnableKafka
@ComponentScan(basePackages = { "demo01c.bootstrap.kafka" })
@Configuration*/
public abstract class SpringConfigForRabbitMQConsumerBase<V>
		   implements SpringConfigForRabbitMQConsumer<V> {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////	
	//protected final ServiceBootstrapSpringHandler _servicesBootstrap;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
	public SpringConfigForRabbitMQConsumerBase(){
		//_servicesBootstrap = servicesBootstrap;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSUMMER FACTORY
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public abstract ConnectionFactory connectionFactory();

	@Override
	public abstract <C  extends MessageListenerContainer> RabbitListenerContainerFactory<C> rabbitListenerContainerFactory();

	@Override
    public abstract AmqpAdmin amqpAdmin() ;
}
