package r01.kv.transform;

import r01.model.PersistableKeyValueObject;
import r01.model.oids.KEYs.KEY;
import r01f.cloud.aws.s3.model.AWSS3ObjectGetResult;
import r01f.cloud.aws.s3.model.AWSS3ObjectKey;
import r01f.patterns.FactoryFrom;

public interface FactoryFromS3ObjectToModelObject<K extends KEY,
												  V extends PersistableKeyValueObject<K>>
          extends FactoryFrom<AWSS3ObjectGetResult,V>{

	public FactoryFrom<AWSS3ObjectKey,K> getKeyFactory();
}
