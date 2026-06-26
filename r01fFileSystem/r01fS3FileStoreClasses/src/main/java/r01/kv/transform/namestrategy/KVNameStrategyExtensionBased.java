package r01.kv.transform.namestrategy;

import org.apache.commons.io.FilenameUtils;

import lombok.extern.slf4j.Slf4j;
import r01.model.oids.KEYs.KEY;
import r01f.cloud.aws.s3.model.AWSS3ObjectKey;
import r01f.guids.OIDs;
import r01f.mime.MimeType;
import r01f.mime.MimeTypes;
import r01f.patterns.FactoryFrom;
import r01f.util.types.Strings;
@Slf4j
public class KVNameStrategyExtensionBased<K extends KEY>
     extends KVNameStrategyBase<K>
  implements KVNameStrategy {
	
/////////////////////////////////////////////////////////////////////////////////
// CONSTRUCTOR
////////////////////////////////////////////////////////////////////////////////
	public KVNameStrategyExtensionBased(final Class<K> modelObjectKeyType, final MimeType mimeType) {
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
						String extension =   MimeTypes.tikaMimeTypeFor(_mimeType.toString()).getExtension();
						String keyAsString  = Strings.customized("{}{}",other.asString(),
								                                       extension);
						return AWSS3ObjectKey.forId(keyAsString);
					}
		};
	}

	@Override @SuppressWarnings("unchecked")
	public FactoryFrom<AWSS3ObjectKey, K> fromS3Key() {
		return new  FactoryFrom<AWSS3ObjectKey, K>() {
					@Override
					public K from(final AWSS3ObjectKey s3Key) {
						log.debug(" ...the S3ObjectKey {}", s3Key);
						String pathWithoutExtension =  FilenameUtils.removeExtension(s3Key.asString());
						log.debug(" ...the model object Key of type {} and value {}", _modelObjectKeyType, pathWithoutExtension);
						K key = OIDs.createOIDFromString(_modelObjectKeyType,pathWithoutExtension );
						return key;
					}
			    };
	  }


}
