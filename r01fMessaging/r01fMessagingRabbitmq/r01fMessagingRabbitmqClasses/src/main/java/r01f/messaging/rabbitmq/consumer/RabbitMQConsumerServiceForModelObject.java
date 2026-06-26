package r01f.messaging.rabbitmq.consumer;

import r01f.facets.HasOID;
import r01f.guids.OID;
import r01f.messaging.consumer.MessagingConsumerServiceForModelObject;
import r01f.messaging.rabbitmq.model.RabbitMQMessageSubscriber;
import r01f.model.ModelObject;

public interface RabbitMQConsumerServiceForModelObject<O extends OID,M extends ModelObject & HasOID<O>>
		extends MessagingConsumerServiceForModelObject<O,M,RabbitMQMessageSubscriber> {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
}
