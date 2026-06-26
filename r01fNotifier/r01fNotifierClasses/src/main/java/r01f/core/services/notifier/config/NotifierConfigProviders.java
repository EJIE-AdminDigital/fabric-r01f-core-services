package r01f.core.services.notifier.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import r01f.config.ContainsConfigData;
import r01f.core.services.notifier.config.NotifierEnums.NotifierImpl;
import r01f.core.services.notifier.properties.NotifierPropertiesContainer;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

/**
 *	Notifier config is divided into THREE different parts
 *		[1] - A 'common' part: contains common notifier properties like whether
 *		  				   	   the notifier is enabled or the [from] address
 *						   	   All these properties DO NOT depend upon the specific
 *						   	   implementation of the notifier
 *
 *		[2] - A notifier impl specific part that contains properties that depend upon the
 *		  	  concrete service which the notifier uses to perform it's job
 * 		  	  (ie: an SMS notifier can use many SMS-sender services like [amazon SNS] or [latinia]
 * 			       config part contains the concrete SMS-sender service implementation config)
 *
 *		[3] - An application specific part that contains application-arbitrary config
 *			  like paths to templates, or whatever
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class NotifierConfigProviders {
/////////////////////////////////////////////////////////////////////////////////////////
//	service impl dependent config provider
/////////////////////////////////////////////////////////////////////////////////////////
	@FunctionalInterface
	public interface NotifierServiceImplDependentConfigProviderFromProperties {
		public ContainsConfigData provideConfigUsing(final NotifierImpl impl,
													 final XMLPropertiesForAppComponent props);
	}
	
	
	@FunctionalInterface
	public interface NotifierServiceImplDependentConfigProviderFromRecordProperties {
		 public ContainsConfigData provideConfigUsing(final NotifierImpl impl,
													  final NotifierPropertiesContainer props);
	
	}
	
/////////////////////////////////////////////////////////////////////////////////////////
// 	app dependent config provider
/////////////////////////////////////////////////////////////////////////////////////////
	@FunctionalInterface
	public interface NotifierAppDependentConfigProviderFromProperties {
		public ContainsConfigData provideConfigUsing(final NotifierImpl impl,
													 final XMLPropertiesForAppComponent props);
	}
	

	@FunctionalInterface
	public interface NotifierAppDependentConfigProviderFromRecordProperties {
		public ContainsConfigData provideConfigUsing(final NotifierImpl impl,
													 final NotifierPropertiesContainer props);
	}
}
