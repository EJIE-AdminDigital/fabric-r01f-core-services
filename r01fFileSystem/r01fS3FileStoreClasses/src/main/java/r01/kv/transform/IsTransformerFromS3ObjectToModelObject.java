package r01.kv.transform;

import r01.api.filestore.model.S3PersistableKeyValueObject;
import r01.api.filestore.model.oids.S3KEYs.IsS3Key;
import r01f.cloud.aws.s3.model.AWSS3ObjectGetResult;
import r01f.cloud.aws.s3.model.AWSS3ObjectKey;
import r01f.patterns.FactoryFrom;
import r01f.patterns.Transformer;

public interface IsTransformerFromS3ObjectToModelObject<K extends IsS3Key,V extends S3PersistableKeyValueObject<K>>
         extends Transformer<AWSS3ObjectGetResult,V> {		// from something readed from the [file store] (AWSS3ObjectGetResult) creates a [model object]

	public FactoryFrom<AWSS3ObjectKey,K> getKeyFactory();
}
