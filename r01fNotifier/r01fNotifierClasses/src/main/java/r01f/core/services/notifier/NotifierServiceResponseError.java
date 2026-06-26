package r01f.core.services.notifier;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.core.services.notifier.NotifierOIDs.NotifierTaskOID;
import r01f.core.services.notifier.config.NotifierEnums.NotifierType;

@Accessors(prefix="_")
public class NotifierServiceResponseError<T>
	extends NotifierServiceResponseBase<T>
  implements NotifierServiceResponse<T> {	
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////	
	@Getter protected final NotifierResponseErrorType _errorType;
	@Getter protected final String  _errorDetail;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
	public NotifierServiceResponseError(final NotifierTaskOID taskOid,final T to,
                                        final NotifierType notifierType, 
                                        final  NotifierResponseErrorType errorType) {
		this(taskOid, to,notifierType, errorType, null);	

	}
	public NotifierServiceResponseError(final NotifierTaskOID taskOid,final T to,
			                            final NotifierType notifierType,
			                            final NotifierResponseErrorType errorType,final String errorDetail) {
		super(taskOid, to, false,notifierType);	
		_errorType = errorType;
		_errorDetail = errorDetail;
	}
}