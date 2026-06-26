package r01f.messaging.kafka.consumer;

import jakarta.inject.Inject;
import r01f.facets.HasOID;
import r01f.guids.OID;
import r01f.messaging.consumer.MessagingConsumerServiceForModelObject;
import r01f.messaging.kafka.model.KafkaMessageSubscriber;
import r01f.messaging.kafka.serialization.KafkaDeserializer;
import r01f.model.ModelObject;

public class KafkaConsumerServiceForModelObjectImpl<O extends OID ,M extends ModelObject & HasOID<O>>
	 extends KafkaConsumerServiceImplBase<O,M>
  implements MessagingConsumerServiceForModelObject<O,M,KafkaMessageSubscriber> {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
	@Inject
	public KafkaConsumerServiceForModelObjectImpl(final KafkaConsumerServiceConfig config,
			                                      final KafkaDeserializer<O> keydeserializer,final KafkaDeserializer<M> valueSerializer ){
		super(config,
			  keydeserializer,valueSerializer);
	}
}
