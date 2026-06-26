package r01f.messaging.rabbitmq.producer;

import jakarta.inject.Inject;
import r01f.facets.HasOID;
import r01f.guids.OID;
import r01f.messaging.producer.MessagingProducerServiceForModelObject;
import r01f.messaging.rabbitmq.RabbitMQMessagingServiceConfig;
import r01f.messaging.rabbitmq.model.RabbitMQMessageSubscriber;
import r01f.messaging.rabbitmq.serialization.RabbitMQSerializer;
import r01f.model.ModelObject;

public class RabbitMQProducerServiceForModelObjectImpl<O extends OID,M extends ModelObject & HasOID<O>>
	 extends RabbitMQProducerServiceImplBase<M>
  implements MessagingProducerServiceForModelObject<O,M,RabbitMQMessageSubscriber> {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	@Inject
	public RabbitMQProducerServiceForModelObjectImpl(final RabbitMQMessagingServiceConfig config,
			                                         final RabbitMQSerializer<M> serializer ){
		super(config,
			  serializer);
	}
}
