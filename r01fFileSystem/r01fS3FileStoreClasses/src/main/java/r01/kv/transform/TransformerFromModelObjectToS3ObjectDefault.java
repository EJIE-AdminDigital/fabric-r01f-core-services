package r01.kv.transform;

import java.io.ByteArrayInputStream;

import r01.api.filestore.model.S3PersistableKeyValueObject;
import r01.api.filestore.model.oids.S3KEYs.IsS3Key;
import r01.kv.config.KVModuleConfig;
import r01f.cloud.aws.s3.model.AWSS3ObjectKey;
import r01f.cloud.aws.s3.model.AWSS3ObjectMetaDataItem;
import r01f.cloud.aws.s3.model.AWSS3ObjectMetadataItemId;
import r01f.cloud.aws.s3.model.AWSS3ObjectPutRequest;
import r01f.guids.OIDs;
import r01f.mime.MimeType;
import r01f.objectstreamer.Marshaller;
import r01f.patterns.FactoryFrom;

public class TransformerFromModelObjectToS3ObjectDefault <K extends IsS3Key,V extends S3PersistableKeyValueObject<K>>
     extends S3FileStoreTransformerBase<K,V>
  implements IsTransformerFromModelObjectToS3Object<K,V> {
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public TransformerFromModelObjectToS3ObjectDefault(final Class<V> modelObjectType,final KVModuleConfig  kvCfg,
										      		   final Marshaller marshaller,
										      		   final MimeType mimeType) {

		super(modelObjectType,kvCfg,marshaller,mimeType);
	}
///////////////////////////////////////////////////////////////////////////////////////////////////////////
//	METHODS
///////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public AWSS3ObjectPutRequest transform(final V modelobject) {
		return new AWSS3ObjectPutRequest(// the bucket
				                         _kvCfg.getModuleConfigForBucket().getDefaultBucket().asS3(),
				                         // the key
										 this.getKeyFactory().from(modelobject.getKey()),
										 // the json or xml stream
									     new ByteArrayInputStream( _marshaller.forWriting().to(_marshallFormatFor(_mimeType),modelobject).getBytes()),
									     // the mime type
									     new AWSS3ObjectMetaDataItem(AWSS3ObjectMetadataItemId.forId("Content-Type"),_mimeType.asString()));



	}
	@Override
	public FactoryFrom<K,AWSS3ObjectKey> getKeyFactory() {
		return	 _kvCfg.getModuleConfigForBucket()
						     .getNameStrategy().getStrategy()
						     .forKeyType(OIDs.oidTypeFor(_modelObjectType))
						     .withMimeType(_mimeType)
					     .build()
				     .fromKey();

	};
}
