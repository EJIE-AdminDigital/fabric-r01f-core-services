package r01.kv.transform.namestrategy;

import r01.model.oids.KEYs.KEY;
import r01f.cloud.aws.s3.model.AWSS3ObjectKey;
import r01f.patterns.FactoryFrom;

public interface KVNameStrategy {

	public <K extends KEY> FactoryFrom<K,AWSS3ObjectKey> fromKey();

	public <K extends KEY> FactoryFrom<AWSS3ObjectKey,K> fromS3Key();


}
