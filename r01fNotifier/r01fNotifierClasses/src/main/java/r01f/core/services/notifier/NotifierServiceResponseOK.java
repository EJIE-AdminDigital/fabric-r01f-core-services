package r01f.core.services.notifier;

import lombok.experimental.Accessors;
import r01f.core.services.notifier.NotifierOIDs.NotifierTaskOID;
import r01f.core.services.notifier.config.NotifierEnums.NotifierType;

@Accessors(prefix="_")
public  class NotifierServiceResponseOK<T>
	  extends NotifierServiceResponseBase<T>
   implements NotifierServiceResponse<T> {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
	public NotifierServiceResponseOK(final NotifierTaskOID taskOid,final T to, 
									 final  NotifierType notifierType) {
		super(taskOid,to, 
			  true, 	// success
			  notifierType);		
	}	
}