package r01.kv.transform.namestrategy;

import lombok.Getter;
import r01.api.filestore.model.oids.S3KEYs.IsS3Key;
import r01f.mime.MimeType;


public abstract class KVNameStrategyBase<K extends IsS3Key>
           implements KVNameStrategy  {
/////////////////////////////////////////////////////////////////////////////////
// MEMBERS
////////////////////////////////////////////////////////////////////////////////
	@Getter protected final Class<K> _modelObjectKeyType;
	@Getter protected final MimeType _mimeType;
/////////////////////////////////////////////////////////////////////////////////
// CONSTRUCTOR
////////////////////////////////////////////////////////////////////////////////
	public KVNameStrategyBase(final Class<K> modelObjectKeyType, final MimeType mimeType) {
		_modelObjectKeyType = modelObjectKeyType;
		_mimeType = mimeType;
	}
}