package r01f.core.services.notifier.spi;

import r01f.core.services.notifier.NotifierServiceForSMS;
import r01f.core.services.notifier.config.NotifierConfigForSMS;
import r01f.core.services.notifier.config.NotifierConfigProviders.NotifierAppDependentConfigProviderFromProperties;
import r01f.core.services.notifier.config.NotifierConfigProviders.NotifierAppDependentConfigProviderFromRecordProperties;
import r01f.core.services.notifier.config.NotifierEnums.NotifierImpl;
import r01f.core.services.notifier.properties.NotifierPropertiesContainer;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

/**
 * spi provider for notifier services
 * (see: https://www.baeldung.com/java-spi)
 * BEWARE!!	There MUST exist a file named as the FQN of the spi provider INTERFACE at META-INF folder
 * 			of every concrete implementation
 * 			The content of this file must be the FQN of the spi provider interface IMPLEMENTATION
 * see:
 * 		- EMail: [r01fEMailSpringClasses]
 * 		- SMS: [r01fAWSSNSClasses] and [r01fLatiniaClasses]
 * 		- Voice: [r01fTwilioClasses]
 */
public interface NotifierSPIProviderForSMS {
	NotifierImpl getImpl();
	NotifierServiceForSMS provideSMSNotifier(final NotifierConfigForSMS config);
	NotifierConfigForSMS provideSMSNotifierConfig(final XMLPropertiesForAppComponent props,
												  final NotifierAppDependentConfigProviderFromProperties appDepConfigProvider);
	NotifierConfigForSMS provideSMSNotifierConfig(final NotifierPropertiesContainer props,
			                                      final NotifierAppDependentConfigProviderFromRecordProperties appDepConfigProvider);
}