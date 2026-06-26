package r01f.messaging.rabbitmq.test.guice.producer.test;

import java.time.Instant;

import jakarta.inject.Inject;
import r01f.guids.OIDs;
import r01f.messaging.rabbitmq.RabbitMQIds.RabbitMQQueue;
import r01f.messaging.rabbitmq.model.RabbitMQMessageSubscriber;
import r01f.messaging.rabbitmq.producer.RabbitMQProducerService;
import r01f.messaging.rabbitmq.test.model.MyOIDs.MyTestOID;
import r01f.messaging.rabbitmq.test.model.MyTestModelObject;

/**
 * [ Sample ] Some sample service using a injected KafkaProducerService
 */
public class ServiceWithProducerSample {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	public final RabbitMQProducerService<MyTestModelObject> _producerService;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
	@Inject
	public ServiceWithProducerSample( final RabbitMQProducerService<MyTestModelObject> producerService) {
		_producerService  = producerService;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	public void doIt() {
		MyTestModelObject obj = new MyTestModelObject();
		obj.setName(" Paleologina 2022 >> This is test object  " + Instant.now());
		obj.setOid(MyTestOID.forId(OIDs.supplyOid()));

		// [ 3]  Send five mocks.
		for ( int i = 0 ; i< 5 ;i ++)
			_producerService.send(// ... the subscriber.
							      RabbitMQMessageSubscriber.create().forQueue(RabbitMQQueue.DEFAULT),
							      // message factory
								  () -> obj);
	}
}
