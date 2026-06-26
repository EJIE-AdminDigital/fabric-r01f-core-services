package r01f.messaging.kafka.producer;

import r01f.facets.HasOID;
import r01f.guids.OID;
import r01f.messaging.kafka.model.KafkaMessageSubscriber;
import r01f.messaging.producer.MessagingProducerServiceForModelObject;
import r01f.model.ModelObject;

public interface KafkaProducerServiceForModelObject<O extends OID,M extends ModelObject & HasOID<O>>
		 extends MessagingProducerServiceForModelObject<O,M,KafkaMessageSubscriber> {
	// a marker interface
}
