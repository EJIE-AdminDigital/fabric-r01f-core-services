
package r01f.messaging.rabbitmq.test.noguice;

import java.time.Instant;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.guids.OIDs;
import r01f.internal.R01FAppCodes;
import r01f.messaging.model.MessagingFactory;
import r01f.messaging.rabbitmq.RabbitMQIds.RabbitMQQueue;
import r01f.messaging.rabbitmq.model.RabbitMQMessageSubscriber;
import r01f.messaging.rabbitmq.producer.RabbitMQProducerService;
import r01f.messaging.rabbitmq.producer.RabbitMQProducerServiceForModelObjectImpl;
import r01f.messaging.rabbitmq.serialization.RabbitMQSerializerForModelObjectBase;
import r01f.messaging.rabbitmq.test.model.MyOIDs.MyTestOID;
import r01f.messaging.rabbitmq.test.model.MyTestModelObject;
import r01f.messaging.rabbitmq.test.model.RabbitMQProducerConfigSample;
import r01f.objectstreamer.Marshaller;
import r01f.objectstreamer.MarshallerBuilder;

@Accessors(prefix="_")
public class RabbitMQProducerTest {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	static  @Getter Marshaller _marshaller =
			 				MarshallerBuilder.findTypesToMarshallAt(R01FAppCodes.APP_CODE)
												.build();
	
	public static RabbitMQProducerService<MyTestModelObject> buildProducerService(final Marshaller marshaller){

		return new RabbitMQProducerServiceForModelObjectImpl<>(new RabbitMQProducerConfigSample(),
				                                                						  new RabbitMQSerializerForModelObjectBase<>(MyTestModelObject.class,
				                            		                                                                                 marshaller)
																							);

	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void main(final String[] args) {
		// [ 0 ] - Marshaller
		Marshaller marshaller = RabbitMQProducerTest.getMarshaller();

		// [ 1 ] - Producer Service
		RabbitMQProducerService<MyTestModelObject> impl = buildProducerService(marshaller);

		// [ 2 ] - A mock object
		MyTestModelObject obj = new MyTestModelObject();
		obj.setName(" Paleologina 2022 >> This is test object  " + Instant.now());
		obj.setOid(MyTestOID.forId(OIDs.supplyOid()));

		// [ 3]  Send five mocks.
		for (int i = 0; i< 5 ;i ++) {
			impl.send(// ... the subscriber.
					  RabbitMQMessageSubscriber.create().forQueue(RabbitMQQueue.DEFAULT),
					  // ... a message factory
					  new MessagingFactory() {
												@Override
												public MyTestModelObject getValue() {
													return obj;
												}
					  });
		}
	}
}