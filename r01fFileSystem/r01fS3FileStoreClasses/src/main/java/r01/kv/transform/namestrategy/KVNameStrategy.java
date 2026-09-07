package r01.kv.transform.namestrategy;

import r01.api.filestore.model.oids.S3KEYs.IsS3Key;
import r01f.cloud.aws.s3.model.AWSS3ObjectKey;
import r01f.patterns.FactoryFrom;

public interface KVNameStrategy {

	public <K extends IsS3Key> FactoryFrom<K,AWSS3ObjectKey> fromKey();

	public <K extends IsS3Key> FactoryFrom<AWSS3ObjectKey,K> fromS3Key();


}
