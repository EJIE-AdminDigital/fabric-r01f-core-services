package r01f.core.services.notifier;

import r01f.oids.CommonOIDs.AppCode;



public interface NotifierServiceForPushMessage
         extends NotifierService<AppCode,NotifierPushMessageSubscriber,// subscriber topic / token (SecurityToken)
                                 NotifierPushMessage> {

	// Notifier Service for Push Message

}