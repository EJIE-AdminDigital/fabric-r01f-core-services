package r01.kv;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01.kv.config.KVModuleConfig;
import r01.kv.config.KVModuleConfigBaseForS3;
import r01.kv.transform.FactoryFromModelObjectToS3Object;
import r01.kv.transform.FactoryFromModelObjectToS3ObjectDefault;
import r01.kv.transform.FactoryFromS3ObjectToModelObject;
import r01.kv.transform.FactoryFromS3ObjectToModelObjectDefault;
import r01.kv.transform.KVEntityIsJSONTransformable;
import r01.kv.transform.KVEntityIsXMLTransformable;
import r01.model.PersistableKeyValueObject;
import r01.model.oids.KEYs.KEY;
import r01f.cloud.aws.s3.client.api.AWSS3ClientAPI;
import r01f.cloud.aws.s3.model.AWSS3Bucket;
import r01f.mime.MimeType;
import r01f.mime.MimeTypes;
import r01f.objectstreamer.Marshaller;

@Accessors(prefix="_")
public class KVBaseForPersistableObject<K extends KEY,V extends PersistableKeyValueObject<K>> {

/////////////////////////////////////////////////////////////////////////////////
// MEMBERS
////////////////////////////////////////////////////////////////////////////////
	@Getter protected final Class<V> _modelObjectType;
	@Getter protected final AWSS3ClientAPI _s3Api;
	@Getter protected final KVModuleConfig _kvCfg;
	@Getter protected final Marshaller _marshaller;
	@Getter protected final	AWSS3Bucket _bucketName;

	/**
	 * Transforms a inputstream from s3 into a model object
	 */
	@Getter protected final FactoryFromS3ObjectToModelObject<K,V>   _factoryFromS3ObjectToModelObject;

	/**
	 * Transforms a model object to S3 InputStream
	 */
	@Getter protected final FactoryFromModelObjectToS3Object<K,V>   _factoryFromModelObjectToS3Object;
/////////////////////////////////////////////////////////////////////////////////
// CONSTRUCTOR :
////////////////////////////////////////////////////////////////////////////////
	public KVBaseForPersistableObject(final Class<V> modelObjectType,
		                              final KVModuleConfig kvCfg,
									  final FactoryFromS3ObjectToModelObject<K,V>   s3InputStreamToModelObjectTransformer,
									  final FactoryFromModelObjectToS3Object<K,V>   modelObjecToS3InputStreamTransformer,
									  final AWSS3ClientAPI s3Api,
									  final Marshaller marshaller) {
		_modelObjectType = modelObjectType;
		_factoryFromS3ObjectToModelObject = s3InputStreamToModelObjectTransformer;
		_factoryFromModelObjectToS3Object = modelObjecToS3InputStreamTransformer;
		_s3Api = s3Api;
		_kvCfg = kvCfg;
		_marshaller =  marshaller;
		_bucketName = kvCfg.as(KVModuleConfigBaseForS3.class)
				             .getModuleConfigForBucket()
				           .getDefaultBucket();
	}

	public KVBaseForPersistableObject(final Class<V> modelObjectType,
			                              final KVModuleConfig kvCfg,
										  final AWSS3ClientAPI s3Api,
										  final Marshaller marshaller) {
		_modelObjectType = modelObjectType;
		_s3Api = s3Api;
		_kvCfg = kvCfg;
	    _marshaller = marshaller;
		_factoryFromS3ObjectToModelObject =  new FactoryFromS3ObjectToModelObjectDefault<K,V> (_modelObjectType,
				                                                                                   _kvCfg,
																								   _marshaller, _mimeTypeFormatForKeyValue());

		_factoryFromModelObjectToS3Object =  new FactoryFromModelObjectToS3ObjectDefault<K,V> (_modelObjectType,
				                                                                                   _kvCfg,
																								    marshaller, _mimeTypeFormatForKeyValue());
		_bucketName = kvCfg.as(KVModuleConfigBaseForS3.class)
								.getModuleConfigForBucket()
				           .getDefaultBucket();

	}
/////////////////////////////////////////////////////////////////////////////////
// 	PRIVATE METHOD
////////////////////////////////////////////////////////////////////////////////
	protected MimeType _mimeTypeFormatForKeyValue() {
		if (this instanceof KVEntityIsJSONTransformable) {
			return MimeTypes.APPLICATION_JSON;
		} else if (this instanceof KVEntityIsXMLTransformable) {
			return  MimeTypes.APPLICATION_XML;
		} else {
			throw new IllegalStateException(" KVBaseForPersistableObject must implement either KVEntityIsJSONTransformable / KVEntityIsJSONTransformable ");
		}
	}
}
