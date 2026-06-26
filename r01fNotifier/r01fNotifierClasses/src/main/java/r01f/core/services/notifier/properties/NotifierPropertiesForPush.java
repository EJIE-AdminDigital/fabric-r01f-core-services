package r01f.core.services.notifier.properties;

import r01f.core.services.notifier.config.NotifierEnums.NotifierImpl;
import r01f.guids.CommonOIDs.AppCode;

public record NotifierPropertiesForPush(  boolean enabled,
									      NotifierImpl impl,
									  	  AppCode from,
									      String fromName) 
	implements NotifierAppDependentProperties  {}