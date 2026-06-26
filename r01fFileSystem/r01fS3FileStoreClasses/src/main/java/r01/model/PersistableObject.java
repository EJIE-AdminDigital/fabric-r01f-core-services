package r01.model;

import r01f.guids.PersistableObjectOID;
import r01f.model.IndexableModelObject;
import r01f.model.ModelObject;
import r01f.model.PersistableModelObject;


/**
 * Interface for every  Persistable Model Object
 * @param <O>
 * @param <ID>
 */
public interface PersistableObject<O extends PersistableObjectOID>
		 extends PersistableModelObject<O>,			// is persistable
     	 		 IndexableModelObject,				// is indexable
     	 		 ModelObject {
	// nothing
}
