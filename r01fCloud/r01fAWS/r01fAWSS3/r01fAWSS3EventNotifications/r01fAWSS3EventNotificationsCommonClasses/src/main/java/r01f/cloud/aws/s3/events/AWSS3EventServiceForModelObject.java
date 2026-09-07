package r01f.cloud.aws.s3.events;

import r01f.facets.HasOID;
import r01f.guids.OID;
import r01f.model.ModelObject;

/**
 * Interface for Distributed Messaging Services.
 * @param <O>
 * @param <M>
 */
public interface AWSS3EventServiceForModelObject<O extends OID,M extends ModelObject & HasOID<O>>
	extends AWSS3EventService {
  //
}
