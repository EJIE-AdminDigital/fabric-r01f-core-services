package r01f.core.services.notifier;

import r01f.types.contact.OwnedContactMean;
import r01f.types.contact.Phone;


public interface NotifierServiceForSMS
         extends NotifierService<OwnedContactMean<Phone>,Phone,		// from & to
							 	 String>  {
	// just a marker interface
}
