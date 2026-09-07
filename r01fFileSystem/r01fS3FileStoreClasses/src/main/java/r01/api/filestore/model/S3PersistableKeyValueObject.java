package r01.api.filestore.model;


import r01.api.filestore.model.oids.S3KEYs.HasS3Key;
import r01.api.filestore.model.oids.S3KEYs.IsS3Key;
import r01f.model.ModelObject;
import r01f.model.PersistableModelObject;

/**
 * Interface for every MT01 {@link PersistableModelObject} Key Value Object
 * @param <O>
 * @param <ID>
 */
public interface S3PersistableKeyValueObject<K extends IsS3Key>
		 extends S3PersistableObject<K>,
		 		 HasS3Key<K>,							// HAS KEY
     	 		 ModelObject {
	// just a marker interface
}
