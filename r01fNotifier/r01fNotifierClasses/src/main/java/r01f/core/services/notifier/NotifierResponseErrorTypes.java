package r01f.core.services.notifier;

import lombok.experimental.Accessors;
import r01f.core.services.notifier.NotifierResponseErrorType.NotiferServiceErrorOrigin;
import r01f.exceptions.ExceptionSeverity;

/**
 * Notifier Operations error codes
 */
@Accessors(prefix="_")
public abstract class NotifierResponseErrorTypes   {
	
	// the client cannot reach the server
	public static NotifierResponseErrorType CLIENT_CANNOT_CONNECT_SERVER = NotifierResponseErrorType.originatedAt(NotiferServiceErrorOrigin.CLIENT)
																			  .withName("CLIENT_CANNOT_CONNECT_SERVER")
																			  .coded(100,1)
																			  .severity(ExceptionSeverity.FATAL)
																			  .build();
	// the subscribertwas not found ( for example a token representated subscriber at push)
	public static NotifierResponseErrorType SUBSCRIBER_NOT_FOUND = NotifierResponseErrorType.originatedAt(NotiferServiceErrorOrigin.CLIENT)
																			  .withName("SUBSCRIBER_NOT_FOUND")
																			  .coded(100,2)
																			  .severity(ExceptionSeverity.RECOVERABLE)
																			  .build();

	// some server error
	public static NotifierResponseErrorType SERVER_ERROR = NotifierResponseErrorType.originatedAt(NotiferServiceErrorOrigin.SERVER)
																			  .withName("SERVER_ERROR")
																			  .coded(100,4)
																			  .severity(ExceptionSeverity.FATAL)
																			  .build();
	
	// some server error
	public static NotifierResponseErrorType UNKNOWN = NotifierResponseErrorType.originatedAt(NotiferServiceErrorOrigin.SERVER)
																				  .withName("UNKNOWNN")
																				  .coded(100,4)
																				  .severity(ExceptionSeverity.FATAL)
																				  .build();
}