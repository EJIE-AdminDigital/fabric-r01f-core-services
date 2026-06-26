package r01f.messaging.kafka.test.guice.producer.test;

import java.time.Instant;

import jakarta.inject.Inject;
import r01f.guids.OIDs;
import r01f.messaging.kafka.KafkaIDs.KafkaTopic;
import r01f.messaging.kafka.model.KafkaMessageSubscriber;
import r01f.messaging.kafka.producer.KafkaProducerService;
import r01f.messaging.kafka.test.model.MyOIDs.MyTestOID;
import r01f.messaging.kafka.test.model.MyTestModelObject;
import r01f.messaging.model.MessagingFactory;

/**
 * [ Sample ] Some sample service using a injected KafkaProducerService
 */
public class ServiceWithProducerSample {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	private final KafkaProducerService<MyTestOID,MyTestModelObject> _producerService;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
	@Inject
	public ServiceWithProducerSample( final KafkaProducerService<MyTestOID,MyTestModelObject> producerService) {
		_producerService  = producerService;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	SOMETHING TO SEND
/////////////////////////////////////////////////////////////////////////////////////////	
	@SuppressWarnings({ "rawtypes","unchecked" })
	public void doIt() {
		MyTestModelObject obj = new MyTestModelObject();
		obj.setName(" Paleologina 2022 >> This is test object " + Instant.now());
		obj.setOid(MyTestOID.forId(OIDs.supplyOid()));

		// Send five mocks.
		for (int i = 0; i< 5 ; i++)
			_producerService.send(// ... the subscriber.
								  KafkaMessageSubscriber.create().forTopic(KafkaTopic.DEFAULT),
								  // ...  the message factory
								  new MessagingFactory() {
											@Override
											public MyTestModelObject getValue() {
												return obj;
											}
								  });
	}
}
