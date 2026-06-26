package r01f.core.services.notifier.properties;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.core.services.notifier.config.NotifierEnums.NotifierImpl;

@Slf4j
@Accessors(prefix="_")
public abstract class NotifierImplPropertiesBase 
		implements NotifierImplDependentProperties {
	
	@Getter @Setter  protected Map<NotifierImpl,Properties>  _impls;	
/////////////////////////////////////////////////////////////////////////////////////////
// DEBUG
/////////////////////////////////////////////////////////////////////////////////////////
	public void debugMap() {
		if (_impls == null || _impls.isEmpty()) {
			log.warn("---- null -----");
			return;
		}
		
		_impls.forEach((key, value) -> {
				log.warn("--- Found Implementatio ! : " + key + " ---");
				log.warn(" key type: " + key.asString());				
		
		if (value instanceof Properties p) {
			p.forEach((k, v) -> log.warn("  Prop: " + k + " = " + v));
		} else {
			log.warn("  content :{} " , value);
		}
		});
	}	
/////////////////////////////////////////////////////////////////////////////////////////
//  MAIL
/////////////////////////////////////////////////////////////////////////////////////////
	@Accessors(prefix="_")
	public static class MailSenderNotifierImplProperties 
	             extends NotifierImplPropertiesBase 
	         implements NotifierImpPropertiesForMail {
	/////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	/////////////////////////////////////////////////////////////////////////////////////////
	    public MailSenderNotifierImplProperties() {
	        super();     
	        _impls = new HashMap<>();
	    }
	}
//////////////////////////////////////////////////////////////////////////////
//  SMS
/////////////////////////////////////////////////////////////////////////////////////////
	@Accessors(prefix="_")
	public static class SMSSenderNotifierImplProperties 
			extends NotifierImplPropertiesBase 
		implements NotifierImpPropertiesForSMS {
	/////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	/////////////////////////////////////////////////////////////////////////////////////////
		public SMSSenderNotifierImplProperties() {
			super();     
			_impls = new HashMap<>();
			}
	}
/////////////////////////////////////////////////////////////////////////////
// PUSH
/////////////////////////////////////////////////////////////////////////////////////////
	@Accessors(prefix="_")
	public static class PushSenderNotifierImplProperties 
			extends NotifierImplPropertiesBase 
		implements NotifierImpPropertiesForPush {
	/////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	/////////////////////////////////////////////////////////////////////////////////////////
	public PushSenderNotifierImplProperties() {
		super();     
		_impls = new HashMap<>();
		}
	}
}
