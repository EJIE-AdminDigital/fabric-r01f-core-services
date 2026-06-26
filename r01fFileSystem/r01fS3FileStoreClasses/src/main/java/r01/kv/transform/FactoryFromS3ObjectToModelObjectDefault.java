package r01.kv.transform;

import java.io.ByteArrayInputStream;

import com.google.common.io.ByteStreams;

import lombok.Cleanup;
import lombok.SneakyThrows;
import r01.kv.config.KVModuleConfig;
import r01.model.PersistableKeyValueObject;
import r01.model.oids.KEYs.KEY;
import r01f.cloud.aws.s3.model.AWSS3ObjectGetResult;
import r01f.cloud.aws.s3.model.AWSS3ObjectKey;
import r01f.guids.OIDs;
import r01f.mime.MimeType;
import r01f.objectstreamer.Marshaller;
import r01f.patterns.FactoryFrom;

public class FactoryFromS3ObjectToModelObjectDefault <K extends KEY,
												      V extends PersistableKeyValueObject<K>>
     extends FactoryFromBase<K,V>
  implements FactoryFromS3ObjectToModelObject<K,V> {
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public FactoryFromS3ObjectToModelObjectDefault(final Class<V> modelObjectType,final KVModuleConfig  kvCfg,
										      		   final Marshaller marshaller,
										      		   final MimeType mimeType) {
		super(modelObjectType,kvCfg,marshaller,mimeType);
	}
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// METHODS TO IMPLEMENT
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override @SneakyThrows
	public V from(final AWSS3ObjectGetResult s3Object) {
		//String data = StringPersistenceUtils.load(s3Object.getInputStream());	// possible concurrency weird error caused by async http-client reusing connections
		//@Cleanup InputStream data = s3Object.getInputStream();
		@Cleanup ByteArrayInputStream data = new  ByteArrayInputStream(ByteStreams.toByteArray(s3Object.getInputStream()));
		return _marshaller.forReading()
							.from(data,
									_marshallFormatFor(_mimeType),
									_modelObjectType);

	}
	@Override
	public FactoryFrom<AWSS3ObjectKey,K> getKeyFactory() {
		return	 _kvCfg.getModuleConfigForBucket()
						     .getNameStrategy().getStrategy()
						     .forKeyType(OIDs.oidTypeFor(_modelObjectType))
						     .withMimeType(_mimeType)
					     .build()
				     .fromS3Key();

	};
}
