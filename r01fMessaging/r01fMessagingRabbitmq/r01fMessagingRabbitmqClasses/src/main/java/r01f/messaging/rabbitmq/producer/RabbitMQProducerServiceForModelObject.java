package r01f.messaging.rabbitmq.producer;

import r01f.facets.HasOID;
import r01f.messaging.producer.MessagingProducerServiceForModelObject;
import r01f.messaging.rabbitmq.model.RabbitMQMessageSubscriber;
import r01f.model.ModelObject;
import r01f.oids.OID;

public interface RabbitMQProducerServiceForModelObject<O extends OID,M extends ModelObject & HasOID<O>>
		 extends MessagingProducerServiceForModelObject<O,M,RabbitMQMessageSubscriber> {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
}
