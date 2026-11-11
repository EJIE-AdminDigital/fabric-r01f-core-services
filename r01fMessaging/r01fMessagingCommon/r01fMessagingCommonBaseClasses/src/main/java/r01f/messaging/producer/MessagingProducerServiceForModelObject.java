package r01f.messaging.producer;

import r01f.facets.HasOID;
import r01f.messaging.consumer.MessagingConsumerService;
import r01f.messaging.model.MessageSubcriber;
import r01f.model.ModelObject;
import r01f.oids.OID;

/**
 * Interface for Distributed Messaging Consumer
 * @param <O>
 * @param <M>
 */
public interface MessagingProducerServiceForModelObject<O extends OID,M extends ModelObject & HasOID<O>, S extends MessageSubcriber>// T-> TO
	     extends MessagingConsumerService<M,S> {
	// just extend
}
