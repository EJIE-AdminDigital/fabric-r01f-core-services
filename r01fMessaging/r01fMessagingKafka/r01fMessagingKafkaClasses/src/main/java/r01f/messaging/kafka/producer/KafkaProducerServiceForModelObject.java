package r01f.messaging.kafka.producer;

import r01f.facets.HasOID;
import r01f.messaging.kafka.model.KafkaMessageSubscriber;
import r01f.messaging.producer.MessagingProducerServiceForModelObject;
import r01f.model.ModelObject;
import r01f.oids.OID;

public interface KafkaProducerServiceForModelObject<O extends OID,M extends ModelObject & HasOID<O>>
		 extends MessagingProducerServiceForModelObject<O,M,KafkaMessageSubscriber> {
	// a marker interface
}
