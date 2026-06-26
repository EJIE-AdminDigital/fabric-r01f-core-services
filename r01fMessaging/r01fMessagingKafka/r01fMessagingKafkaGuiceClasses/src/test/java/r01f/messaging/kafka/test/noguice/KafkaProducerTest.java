
package r01f.messaging.kafka.test.noguice;

import java.time.Instant;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.guids.OIDs;
import r01f.internal.R01FAppCodes;
import r01f.messaging.kafka.KafkaIDs.KafkaTopic;
import r01f.messaging.kafka.model.KafkaMessageSubscriber;
import r01f.messaging.kafka.producer.KafkaProducerService;
import r01f.messaging.kafka.producer.KafkaProducerServiceForModelObjectImpl;
import r01f.messaging.kafka.serialization.KafkaSerializerForModelObjectBase;
import r01f.messaging.kafka.serialization.KafkaSerializerForOIDBase;
import r01f.messaging.kafka.test.model.KafkaProducerConfigSample;
import r01f.messaging.kafka.test.model.MyOIDs.MyTestOID;
import r01f.messaging.kafka.test.model.MyTestModelObject;
import r01f.messaging.model.MessagingFactory;
import r01f.objectstreamer.Marshaller;
import r01f.objectstreamer.MarshallerBuilder;

@Accessors(prefix="_")
public class KafkaProducerTest {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	static  @Getter Marshaller _marshaller = MarshallerBuilder.findTypesToMarshallAt(R01FAppCodes.APP_CODE)
															  .build();
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	@SuppressWarnings("resource")
	public static KafkaProducerService<MyTestOID,MyTestModelObject> buildProducerService(final Marshaller marshaller){
		return new KafkaProducerServiceForModelObjectImpl<>(// A sample config.
				                                            new KafkaProducerConfigSample(),
				                                            // Serializers
				                                            new KafkaSerializerForOIDBase<MyTestOID>(),
				                                            new KafkaSerializerForModelObjectBase<>(MyTestModelObject.class,
				                            		                                                marshaller));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void main(final String[] args) {
		// [ 0 ] - Marshaller
		Marshaller marshaller = KafkaProducerTest.getMarshaller();

		// [ 1 ] - Producer Service
		KafkaProducerService<MyTestOID,MyTestModelObject> impl = buildProducerService(marshaller);

		// [ 2 ] - A mock object
		MyTestModelObject obj = new MyTestModelObject();
		obj.setName(" Paleologina 2022 >> This is test object  " + Instant.now());
		obj.setOid(MyTestOID.forId(OIDs.supplyOid()));

		// [ 3]  Send five mocks.
		for ( int i = 0 ; i< 5 ;i ++)
				impl.send(// ... the subscriber.
						  KafkaMessageSubscriber.create().forTopic(KafkaTopic.DEFAULT),
						  // ... a message factory
						  new MessagingFactory() {
													@Override
													public MyTestModelObject getValue() {
														return obj;
													}});
	}
}