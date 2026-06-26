package r01f.cloud.twilio.notifier;

import r01f.cloud.twilio.TwilioConfig;
import r01f.cloud.twilio.TwilioService;
import r01f.core.services.notifier.NotifierServiceForVoicePhoneCall;
import r01f.core.services.notifier.config.NotifierConfigForVoice;
import r01f.core.services.notifier.config.NotifierConfigProviders.NotifierAppDependentConfigProviderFromProperties;
import r01f.core.services.notifier.config.NotifierEnums.NotifierImpl;
import r01f.core.services.notifier.spi.NotifierSPIProviderForVoice;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

/**
 * SPI provider for AWS SNS based SMS notifier
 * (see: https://www.baeldung.com/java-spi)
 * BEWARE!!	There MUST exist a file named as the FQN of the spi provider INTERFACE at META-INF folder
 * 			The content of this file must be the FQN of the spi provider interface IMPLEMENTATION
 */
public class SPIProviderForTwilioNotifierService
  implements NotifierSPIProviderForVoice {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public NotifierServiceForVoicePhoneCall provideVoiceNotifier(final NotifierConfigForVoice config) {
		// [1] - get the twilio config
		TwilioConfig twCfg = config.getServiceImplConfigAs(TwilioConfig.class);
		// [2] - Create the twilio service
		TwilioService twService = new TwilioService(twCfg);

		// [3] - Build the notifier service
		return new TwilioNotifierService(twService);
	}
	@Override
	public NotifierImpl getImpl() {
		return NotifierImpl.forId("twilio");
	}
	@Override
	public NotifierConfigForVoice provideVoiceNotifierConfig(final XMLPropertiesForAppComponent props,
															 final NotifierAppDependentConfigProviderFromProperties appDepConfigProvider) {
		return new TwilioNotifierConfig(props,
										appDepConfigProvider);
	}
}
