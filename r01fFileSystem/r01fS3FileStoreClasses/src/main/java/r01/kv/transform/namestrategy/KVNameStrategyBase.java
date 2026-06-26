package r01.kv.transform.namestrategy;

import lombok.Getter;
import r01.model.oids.KEYs.KEY;
import r01f.mime.MimeType;


public abstract class KVNameStrategyBase<K extends KEY>
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