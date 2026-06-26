package r01f.core.services.notifier.properties;

import r01f.core.services.notifier.config.NotifierEnums.NotifierImpl;

public record NotifierPropertiesForMail( boolean enabled,
									     NotifierImpl impl,
									     String from,
									     String name,
									     String msgTemplate,
										 String usrRegMsgTemplate,
										 String pwdRstRedirectUrl,
										 String usrRegRedirectUrl
)  implements NotifierAppDependentProperties  {}