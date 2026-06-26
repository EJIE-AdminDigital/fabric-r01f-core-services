package r01.kv.transform;

import java.io.ByteArrayInputStream;

import r01.kv.config.KVModuleConfig;
import r01.model.PersistableKeyValueObject;
import r01.model.oids.KEYs.KEY;
import r01f.cloud.aws.s3.model.AWSS3ObjectKey;
import r01f.cloud.aws.s3.model.AWSS3ObjectMetaDataItem;
import r01f.cloud.aws.s3.model.AWSS3ObjectMetadataItemId;
import r01f.cloud.aws.s3.model.AWSS3ObjectPutRequest;
import r01f.guids.OIDs;
import r01f.mime.MimeType;
import r01f.objectstreamer.Marshaller;
import r01f.patterns.FactoryFrom;

public class FactoryFromModelObjectToS3ObjectDefault <K extends KEY,
												      V extends PersistableKeyValueObject<K>>
        extends FactoryFromBase<K,V>
     implements FactoryFromModelObjectToS3Object<K,V> {
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public FactoryFromModelObjectToS3ObjectDefault(final Class<V> modelObjectType,final KVModuleConfig  kvCfg,
										      		   final Marshaller marshaller,
										      		   final MimeType mimeType) {

		super(modelObjectType,kvCfg,marshaller,mimeType);
	}
///////////////////////////////////////////////////////////////////////////////////////////////////////////
//	METHODS
///////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public AWSS3ObjectPutRequest from(final V modelobject) {
		return new AWSS3ObjectPutRequest(  // the bucket
				                          _kvCfg.getModuleConfigForBucket().getDefaultBucket(),
				                           // the key
										   getKeyFactory().from(modelobject.getKey()),
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
