package r01f.core.services.notifier;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.exceptions.EnrichedThrowableTypeBase;
import r01f.exceptions.EnrichedThrowableTypeBuilder;
import r01f.exceptions.ExceptionSeverity;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;

/**
 * Notifier Response Error Type
 */
@MarshallType(as="notifierResponseErrorType")
@Accessors(prefix="_")
public final class NotifierResponseErrorType 
     	   extends EnrichedThrowableTypeBase {

	private static final long serialVersionUID = 3404282545821425990L;
	/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="origin",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter private final NotiferServiceErrorOrigin _origin;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR                                                                         
/////////////////////////////////////////////////////////////////////////////////////////
	public NotifierResponseErrorType(final NotiferServiceErrorOrigin origin,
								     final String name,
								     final int group,final int code,								
								     final ExceptionSeverity severity) {
		super(name,
			  group,code,
			  severity);
		_origin = origin;
	}
	
	
	public static EnrichedThrowableTypeBuilder<NotifierResponseErrorType> originatedAt(final NotiferServiceErrorOrigin origin) {		
		return new EnrichedThrowableTypeBuilder<NotifierResponseErrorType>() {
						@Override
						protected NotifierResponseErrorType _build(final String name, 
															       final int group,final int code,
															       final ExceptionSeverity severity) {
							return new NotifierResponseErrorType(origin,
															     name,
														         group,code,
														       severity);
						}
			
			   };
	}

/////////////////////////////////////////////////////////////////////////////////////////
//	                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean isServerError() {
		return _origin != null ? _origin == NotiferServiceErrorOrigin.SERVER : false;
	}
	public boolean isClientError() {
		return _origin != null ? _origin == NotiferServiceErrorOrigin.CLIENT : false;
	}
	
	public enum NotiferServiceErrorOrigin {
		CLIENT,
		SERVER,
		UNKNOWN;
	}

}