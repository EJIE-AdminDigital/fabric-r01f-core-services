package r01.kv.transform.namestrategy;

import r01.model.oids.KEYs.KEY;
import r01f.cloud.aws.s3.model.AWSS3ObjectKey;
import r01f.guids.OIDs;
import r01f.mime.MimeType;
import r01f.patterns.FactoryFrom;

public class KVNameStrategyDefault<K extends KEY>
		  extends KVNameStrategyBase<K>
       implements KVNameStrategy {
/////////////////////////////////////////////////////////////////////////////////
// CONSTRUCTOR
////////////////////////////////////////////////////////////////////////////////
	public KVNameStrategyDefault(final Class<K> modelObjectKeyType, final MimeType mimeType) {
		super(modelObjectKeyType,mimeType);
	}
/////////////////////////////////////////////////////////////////////////////////
// MEMBERS
////////////////////////////////////////////////////////////////////////////////
	@Override @SuppressWarnings("unchecked")
	public FactoryFrom<K, AWSS3ObjectKey> fromKey() {
		return new  FactoryFrom<K, AWSS3ObjectKey>() {
					@Override
					public AWSS3ObjectKey from(final K other) {
						return AWSS3ObjectKey.forId(other.asString());
					}

		};
	}
	@Override @SuppressWarnings("unchecked")
	public FactoryFrom<AWSS3ObjectKey, K> fromS3Key() {
		return new  FactoryFrom<AWSS3ObjectKey, K>() {
					@Override
					public K from(final AWSS3ObjectKey s3Key) {
						K key = OIDs.createOIDFromString(_modelObjectKeyType, s3Key.asString());
						return key;
					}
			    };
	  }
}
