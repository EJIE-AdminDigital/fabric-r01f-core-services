package r01f.messaging.consumer;

import r01f.facets.HasOID;
import r01f.guids.OID;
import r01f.messaging.model.MessageSubcriber;
import r01f.model.ModelObject;

/**
 * Interface for Distributed Messaging Consumer
 * @param <O>
 * @param <M>
 */
public interface MessagingConsumerServiceForModelObject<O extends OID,M extends ModelObject & HasOID<O>, S extends MessageSubcriber>// T-> TO
		 extends MessagingConsumerService<M,S> {
	// just extend
}
