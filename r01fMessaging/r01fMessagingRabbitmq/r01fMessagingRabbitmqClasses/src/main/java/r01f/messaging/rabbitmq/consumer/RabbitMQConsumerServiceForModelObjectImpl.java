package r01f.messaging.rabbitmq.consumer;

import jakarta.inject.Inject;
import r01f.facets.HasOID;
import r01f.guids.OID;
import r01f.messaging.consumer.MessagingConsumerServiceForModelObject;
import r01f.messaging.rabbitmq.model.RabbitMQMessageSubscriber;
import r01f.messaging.rabbitmq.serialization.RabbitMQDeserializer;
import r01f.model.ModelObject;

public class RabbitMQConsumerServiceForModelObjectImpl<O extends OID,M extends ModelObject & HasOID<O>>
	 extends RabbitMQConsumerServiceImplBase<M>
  implements MessagingConsumerServiceForModelObject<O,M,RabbitMQMessageSubscriber> {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
	@Inject
	public RabbitMQConsumerServiceForModelObjectImpl(final RabbitMQConsumerServiceConfig config,
			                                         final RabbitMQDeserializer<M> deserializer ){
		super(config,
			  deserializer);
	}
}
