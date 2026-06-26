package r01.kv.transform;

import r01.model.PersistableKeyValueObject;
import r01.model.oids.KEYs.KEY;
import r01f.cloud.aws.s3.model.AWSS3ObjectKey;
import r01f.cloud.aws.s3.model.AWSS3ObjectPutRequest;
import r01f.model.ModelObject;
import r01f.patterns.FactoryFrom;
import software.amazon.awssdk.services.s3.model.S3Object;

/**
 * Interface for types that transforms a a {@link ModelObject} to Amazon S3 {@link S3Object}
 * (used when putting a {@link ModelObject} to a  S3 {@link S3Object})
 * @param < V extends MT01PersistableKeyValueObject<K>>
 * @param <M>
 */
public interface FactoryFromModelObjectToS3Object<K extends KEY,
												  V extends PersistableKeyValueObject<K>>
        extends FactoryFrom<V,AWSS3ObjectPutRequest> {

	public FactoryFrom<K,AWSS3ObjectKey> getKeyFactory();

}
