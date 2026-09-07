package r01.kv.transform;

import r01.api.filestore.model.S3PersistableKeyValueObject;
import r01.api.filestore.model.oids.S3KEYs.IsS3Key;
import r01f.cloud.aws.s3.model.AWSS3ObjectKey;
import r01f.cloud.aws.s3.model.AWSS3ObjectPutRequest;
import r01f.model.ModelObject;
import r01f.patterns.FactoryFrom;
import r01f.patterns.Transformer;
import software.amazon.awssdk.services.s3.model.S3Object;

/**
 * Interface for types that transforms a a {@link ModelObject} to Amazon S3 {@link S3Object}
 * (used when putting a {@link ModelObject} to a  S3 {@link S3Object})
 * @param < V extends MT01PersistableKeyValueObject<K>>
 * @param <M>
 */
public interface IsTransformerFromModelObjectToS3Object<K extends IsS3Key,V extends S3PersistableKeyValueObject<K>>
         extends Transformer<V,AWSS3ObjectPutRequest> {		// from the [model object] creates a AWSS3ObjectPutRequest (something to store in the FileStore)

	public FactoryFrom<K,AWSS3ObjectKey> getKeyFactory();

}
