package r01.model;


import r01.model.oids.KEYs.HasKEY;
import r01.model.oids.KEYs.KEY;
import r01f.model.ModelObject;
import r01f.model.PersistableModelObject;

/**
 * Interface for every MT01 {@link PersistableModelObject} Key Value Object
 * @param <O>
 * @param <ID>
 */
public interface PersistableKeyValueObject<K extends KEY>
		 extends PersistableObject<K>,
		 		 HasKEY<K>,							// HAS KEY
     	 		 ModelObject {
	// just a marker interface
}
