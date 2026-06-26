package r01f.messaging.rabbitmq.test.guice.consumer.test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import r01f.exceptions.Throwables;
import r01f.messaging.rabbitmq.RabbitMQIds.RabbitMQQueue;
import r01f.messaging.rabbitmq.consumer.RabbitMQConsumerService;
import r01f.messaging.rabbitmq.test.model.MyTestModelObject;

/**
 * [ Sample ] Some sample service using a injected KafkaConsumerService
 */
@Slf4j
public class ServiceWithConsumerSample {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////	
	final RabbitMQConsumerService<MyTestModelObject> _consumerService;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
	@Inject
	public ServiceWithConsumerSample( final RabbitMQConsumerService<MyTestModelObject>  consumerService) {
		_consumerService  = consumerService;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	SOMETHING TO DO BASED ON CONSUMER SERVICE
/////////////////////////////////////////////////////////////////////////////////////////	
	@SuppressWarnings({ })
	public void  doIt() {
		// Don't use this! (use listner) this just to test guice
		// [ X ] - Connection Factory Service
	    ConnectionFactory factory = _consumerService.getConnectionFactory();
		try {
			Connection connection = factory.newConnection();
			Channel channel = connection.createChannel();
	        channel.queueDeclare(RabbitMQQueue.DEFAULT.asString(), false, false, false, null);
	        log.warn(" [*] Waiting for messages. To exit press CTRL+C");

	        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
	            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
	            System.out.println(" [x] Received '" + message + "'");
	        };
	        channel.basicConsume(RabbitMQQueue.DEFAULT.asString(),
	        		              true,
	        		              deliverCallback,
	        		              consumerTag -> {
	        		            	  //
	        		              });

		} catch (IOException | TimeoutException e) {
			Throwables.throwUnchecked(e);
		}
	}
}
