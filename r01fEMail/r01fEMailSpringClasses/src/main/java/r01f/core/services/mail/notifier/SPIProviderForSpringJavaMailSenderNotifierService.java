package r01f.core.services.mail.notifier;

import org.springframework.mail.javamail.JavaMailSender;

import r01f.core.services.mail.JavaMailSenderProvider;
import r01f.core.services.mail.config.JavaMailSenderConfig;
import r01f.core.services.notifier.NotifierServiceForEMail;
import r01f.core.services.notifier.config.NotifierConfigForEMail;
import r01f.core.services.notifier.config.NotifierConfigProviders.NotifierAppDependentConfigProviderFromProperties;
import r01f.core.services.notifier.config.NotifierConfigProviders.NotifierAppDependentConfigProviderFromRecordProperties;
import r01f.core.services.notifier.properties.NotifierPropertiesContainer;
import r01f.core.services.notifier.spi.NotifierSPIProviderForEMail;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

/**
 * SPI provider for JavaMailSender email notifier
 * (see: https://www.baeldung.com/java-spi)
 * BEWARE!!	There MUST exist a file named as the FQN of the spi provider INTERFACE at META-INF folder
 * 			The content of this file must be the FQN of the spi provider interface IMPLEMENTATION
 */
public class SPIProviderForSpringJavaMailSenderNotifierService
  implements NotifierSPIProviderForEMail {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public NotifierServiceForEMail provideEMailNotifier(final NotifierConfigForEMail config) {
		// [1] - Get the config
		JavaMailSenderConfig springMailSenderCfg = config.getServiceImplConfigAs(JavaMailSenderConfig.class);
		// [2] - Build the spring mail sender
		JavaMailSenderProvider springMailSenderProvider = new JavaMailSenderProvider(springMailSenderCfg);
		JavaMailSender sprigMailSender = springMailSenderProvider.get();

		// [3] - Build the notifier service
		return new JavaMailSenderNotifierService(sprigMailSender);
	}
	@Override
	public NotifierConfigForEMail provideEMailNotifierConfig(final XMLPropertiesForAppComponent props,
															 final NotifierAppDependentConfigProviderFromProperties appDepConfigProvider) {
		return new JavaMailSenderNotifierConfig(props,
											   	appDepConfigProvider);
	}
	@Override
	public NotifierConfigForEMail provideEMailNotifierConfig(final NotifierPropertiesContainer props,
															 final NotifierAppDependentConfigProviderFromRecordProperties appDepConfigProvider) {
		return new JavaMailSenderNotifierConfig(props,
			                                 	appDepConfigProvider);
	}
}
