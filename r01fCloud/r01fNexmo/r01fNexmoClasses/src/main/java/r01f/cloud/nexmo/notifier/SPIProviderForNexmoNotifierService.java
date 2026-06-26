package r01f.cloud.nexmo.notifier;

import com.nexmo.client.NexmoClient;

import r01f.cloud.nexmo.NexmoConfig;
import r01f.cloud.nexmo.api.interfaces.NexmoServicesForVoice;
import r01f.cloud.nexmo.api.interfaces.impl.NexmoServicesForVoiceImpl;
import r01f.core.services.notifier.NotifierServiceForVoicePhoneCall;
import r01f.core.services.notifier.config.NotifierConfigForVoice;
import r01f.core.services.notifier.config.NotifierConfigProviders.NotifierAppDependentConfigProviderFromProperties;
import r01f.core.services.notifier.config.NotifierEnums.NotifierImpl;
import r01f.core.services.notifier.spi.NotifierSPIProviderForVoice;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

/**
 * SPI provider for NEXMO based Voice notifier
 * (see: https://www.baeldung.com/java-spi)
 * BEWARE!!	There MUST exist a file named as the FQN of the spi provider INTERFACE at META-INF folder
 * 			The content of this file must be the FQN of the spi provider interface IMPLEMENTATION
 */
public class SPIProviderForNexmoNotifierService
  implements NotifierSPIProviderForVoice {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public NotifierServiceForVoicePhoneCall provideVoiceNotifier(final NotifierConfigForVoice config) {
		// [1] - get the nexmo config
		NexmoConfig nexmoConfig = config.getServiceImplConfigAs(NexmoConfig.class);
		// [2] - Create the nexmo client 
		final NexmoClient nexmoClient =  NexmoClient.builder()
											                .apiKey(nexmoConfig.getApiData().getApiKey().asString())
											                .apiSecret(nexmoConfig.getApiData().getApiSecret().asString())
									                .build();
		NexmoServicesForVoice serviceForVoice =  new NexmoServicesForVoiceImpl(nexmoConfig.getApiData(),
                                                                               nexmoClient);
		// [3] - Build the notifier service
		return new NexmoNotifierService(serviceForVoice);
	}
	@Override
	public NotifierImpl getImpl() {
		return NotifierImpl.forId("nexmo");
	}
	@Override
	public NotifierConfigForVoice provideVoiceNotifierConfig(final XMLPropertiesForAppComponent props,
															 final NotifierAppDependentConfigProviderFromProperties appDepConfigProvider) {
		return new NexmoNotifierConfig(props,
										appDepConfigProvider);
	}
}
