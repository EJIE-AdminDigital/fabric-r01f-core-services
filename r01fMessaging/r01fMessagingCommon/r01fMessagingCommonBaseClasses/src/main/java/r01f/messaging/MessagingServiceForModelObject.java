package r01f.messaging;

import r01f.facets.HasOID;
import r01f.guids.OID;
import r01f.model.ModelObject;

/**
 * Interface for Distributed Messaging Services.
 * @param <O>
 * @param <M>
 */
public interface MessagingServiceForModelObject<O extends OID,M extends ModelObject & HasOID<O>>
	extends MessagingService {
  //
}
