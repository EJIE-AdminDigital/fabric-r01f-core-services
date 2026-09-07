package r01.kv.transform;

import lombok.Getter;
import r01.api.filestore.model.S3PersistableKeyValueObject;
import r01.api.filestore.model.oids.S3KEYs.IsS3Key;
import r01.kv.config.KVModuleConfig;
import r01.kv.config.KVModuleConfigBaseForS3;
import r01f.mime.MimeType;
import r01f.mime.MimeTypes;
import r01f.objectstreamer.Marshaller;
import r01f.objectstreamer.annotations.MarshallFormat;
import r01f.util.types.Strings;

public abstract class S3FileStoreTransformerBase <K extends IsS3Key,V extends S3PersistableKeyValueObject<K>> {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////	
	@Getter protected final Class<V> _modelObjectType;
	@Getter protected final Marshaller _marshaller;
	@Getter protected final MimeType _mimeType;
	@Getter protected final KVModuleConfigBaseForS3 _kvCfg;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
	public S3FileStoreTransformerBase(final Class<V> modelObjectType, final KVModuleConfig  kvCfg,
					   				  final Marshaller marshaller,
					   				  final MimeType mimeType) {

		_modelObjectType = modelObjectType;
		_marshaller = marshaller;
		_kvCfg = kvCfg.as(KVModuleConfigBaseForS3.class);
		_mimeType = mimeType;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	protected MarshallFormat _marshallFormatFor(final MimeType mimeType) {
		if (mimeType.equals(MimeTypes.APPLICATION_JSON)) {
			return MarshallFormat.JSON;
		} else if (mimeType.equals(MimeTypes.APPLICATION_XML)) {
			return MarshallFormat.XML;
		} else {
			throw new IllegalArgumentException( Strings.customized(" Not possible to marshall for mime type {}",mimeType ));
		}
	}
}
