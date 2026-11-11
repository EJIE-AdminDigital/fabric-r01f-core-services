package r01.api.filestore.model;

import r01f.model.ModelObject;
import r01f.model.PersistableModelObject;
import r01f.oids.PersistableObjectOID;


/**
 * Interface for every  Persistable Model Object
 * @param <O>
 * @param <ID>
 */
public interface S3PersistableObject<O extends PersistableObjectOID>
		 extends PersistableModelObject<O>,			// is persistable
     	 		 ModelObject {
	// nothing
}
