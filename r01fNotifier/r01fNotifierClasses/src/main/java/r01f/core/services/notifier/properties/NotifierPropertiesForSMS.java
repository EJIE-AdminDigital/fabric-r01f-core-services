package r01f.core.services.notifier.properties;

import r01f.core.services.notifier.config.NotifierEnums.NotifierImpl;
import r01f.types.contact.Phone;

public record NotifierPropertiesForSMS(  boolean enabled,
									     NotifierImpl impl,
									     Phone fromPhone,
									     String fromName) 
	implements NotifierAppDependentProperties  {}