package r01f.core.services.notifier;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.core.services.notifier.NotifierOIDs.NotifierTaskOID;
import r01f.core.services.notifier.config.NotifierEnums.NotifierType;

@RequiredArgsConstructor
@Accessors(prefix="_")
public abstract class NotifierServiceResponseBase<T>
  	       implements NotifierServiceResponse<T> {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////	
	@Getter protected final NotifierTaskOID _taskOid;	
	@Getter protected final T _to;
	@Getter protected final boolean _success;
	@Getter protected final NotifierType _notifierType;
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public NotifierServiceResponseError<T> asResponseError() {
		if (this instanceof NotifierServiceResponseError) return (NotifierServiceResponseError<T>)this;
		throw  new IllegalStateException("Cannot cast a NotifierResponseOK to NotifierResponseError");
	}
	@Override
	public NotifierServiceResponseOK<T> asResponseOK() {
		if (this instanceof NotifierServiceResponseOK) return (NotifierServiceResponseOK<T>)this;
		throw  new IllegalStateException("Cannot cast a NotifierResponseError to NotifierResponseOK");
	}	
	@Override
	public boolean wasSuccessful() {
		return _success;
	}
	@Override
	public boolean hasFailed() {	
		return ! wasSuccessful();
	}	
}