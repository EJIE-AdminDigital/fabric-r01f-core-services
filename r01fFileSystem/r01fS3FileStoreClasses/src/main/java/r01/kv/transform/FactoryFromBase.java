package r01.kv.transform;

import lombok.Getter;
import r01.kv.config.KVModuleConfig;
import r01.kv.config.KVModuleConfigBaseForS3;
import r01.model.PersistableKeyValueObject;
import r01.model.oids.KEYs.KEY;
import r01f.mime.MimeType;
import r01f.mime.MimeTypes;
import r01f.objectstreamer.Marshaller;
import r01f.objectstreamer.annotations.MarshallFormat;
import r01f.util.types.Strings;

public class FactoryFromBase <K extends KEY,
								  V extends PersistableKeyValueObject<K>>{
/////////////////////////////////////////////////////////////////////////////////
// MEMBERS
////////////////////////////////////////////////////////////////////////////////
	@Getter protected final Class<V> _modelObjectType;
	@Getter protected final Marshaller _marshaller;
	@Getter protected final MimeType _mimeType;
	@Getter protected final KVModuleConfigBaseForS3  _kvCfg;
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public FactoryFromBase(final Class<V> modelObjectType, final KVModuleConfig  kvCfg,
							   final Marshaller marshaller,
						       final MimeType mimeType) {

		_modelObjectType = modelObjectType;
		_marshaller = marshaller;
		_kvCfg = kvCfg.as(KVModuleConfigBaseForS3.class);
		_mimeType = mimeType;
	}
///////////////////////////////////////////////////////////////////////////////////////////////////////////
//	Protected and private method
///////////////////////////////////////////////////////////////////////////////////////////////////////////
  protected MarshallFormat _marshallFormatFor(final MimeType mimeType) {
	  if (mimeType.equals(MimeTypes.APPLICATION_JSON)) {
		  return MarshallFormat.JSON;
	  } else   if (mimeType.equals(MimeTypes.APPLICATION_XML)) {
		  return MarshallFormat.XML;
	  } else {
		  throw new IllegalArgumentException( Strings.customized(" Not possible to marshall for mime type {}",mimeType ));
	  }
  }

}
