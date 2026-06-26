package r01f.messaging.kafka.consumer;

import r01f.facets.HasOID;
import r01f.guids.OID;
import r01f.messaging.consumer.MessagingConsumerServiceForModelObject;
import r01f.messaging.kafka.model.KafkaMessageSubscriber;
import r01f.model.ModelObject;

public interface KafkaConsumerServiceForModelObject<O extends OID,M extends ModelObject & HasOID<O>>
		extends MessagingConsumerServiceForModelObject<O,M,KafkaMessageSubscriber> {
	// just a marker interface
}
