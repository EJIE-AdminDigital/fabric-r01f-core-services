package r01f.messaging.rabbitmq.test.noguice;

import java.nio.charset.StandardCharsets;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import lombok.extern.slf4j.Slf4j;
import r01f.messaging.rabbitmq.RabbitMQIds.RabbitMQQueue;
import r01f.messaging.rabbitmq.consumer.RabbitMQConsumerService;
import r01f.messaging.rabbitmq.consumer.RabbitMQConsumerServiceForModelObjectImpl;
import r01f.messaging.rabbitmq.serialization.RabbitMQDeserializerForModelObjectBase;
import r01f.messaging.rabbitmq.test.model.MyTestModelObject;
import r01f.messaging.rabbitmq.test.model.RabbitMQConsumerConfigSample;
import r01f.objectstreamer.Marshaller;

@Slf4j
public class RabbitMQConsumerTest {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("resource")
	public static RabbitMQConsumerService<MyTestModelObject> getService(final Marshaller marshaller){
		return new RabbitMQConsumerServiceForModelObjectImpl<>(// A sample config.
															new RabbitMQConsumerConfigSample(),
															// deserializer
															new RabbitMQDeserializerForModelObjectBase<>(MyTestModelObject.class,
																	 marshaller)
															);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	public static void main(final String[]  argv) {
		// [ 0 ] - Marshaller
		Marshaller marshaller = RabbitMQProducerTest.getMarshaller();

		// [ 1 ] - Connection Factory Service
		RabbitMQConsumerService<MyTestModelObject> impl = getService(marshaller);
		ConnectionFactory factory = impl.getConnectionFactory();
		try {
			Connection connection = factory.newConnection();
			Channel channel = connection.createChannel();

			channel.queueDeclare(RabbitMQQueue.DEFAULT.asString(), true, false, false, null);
			log.warn(" [*] Waiting for messages. To exit press CTRL+C");

			DeliverCallback deliverCallback = (consumerTag, 
											   delivery) -> {
														String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
														System.out.println(" [x] Received '" + message + "'");
			};
			channel.basicConsume(RabbitMQQueue.DEFAULT.asString(),
								  true,
								  deliverCallback,
								  consumerTag -> {
									  //
								  });
	   } catch (Throwable e) {
			  //
	   }
	}
}
