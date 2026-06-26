package r01f.cloud.aws.sns.notifier;

import r01f.cloud.aws.sns.AWSSNSClient;
import r01f.cloud.aws.sns.AWSSNSClientConfig;
import r01f.core.services.notifier.NotifierServiceForSMS;
import r01f.core.services.notifier.config.NotifierConfigForSMS;
import r01f.core.services.notifier.config.NotifierConfigProviders.NotifierAppDependentConfigProviderFromProperties;
import r01f.core.services.notifier.config.NotifierConfigProviders.NotifierAppDependentConfigProviderFromRecordProperties;
import r01f.core.services.notifier.config.NotifierEnums.NotifierImpl;
import r01f.core.services.notifier.properties.NotifierPropertiesContainer;
import r01f.core.services.notifier.spi.NotifierSPIProviderForSMS;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

/**
 * SPI provider for AWS SNS based SMS notifier
 * (see: https://www.baeldung.com/java-spi)
 * BEWARE!!	There MUST exist a file named as the FQN of the spi provider INTERFACE at META-INF folder
 * 			The content of this file must be the FQN of the spi provider interface IMPLEMENTATION
 */
public class SPIProviderForAWSSNSNotifierService
  implements NotifierSPIProviderForSMS {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public NotifierServiceForSMS provideSMSNotifier(final NotifierConfigForSMS config) {
		// [1] - Get the aws sns service config
		AWSSNSClientConfig awsSNSCfg = config.getServiceImplConfigAs(AWSSNSClientConfig.class);
		// [2] - Create a aws sns client
		AWSSNSClient awsSNSCli = new AWSSNSClient(awsSNSCfg);

		// [3] - Build the notifier service
		return new AWSSNSNotifierService(awsSNSCli);
	}
	@Override
	public NotifierImpl getImpl() {
		return NotifierImpl.forId("aws");
	}
	
	@Override
	public NotifierConfigForSMS provideSMSNotifierConfig(final XMLPropertiesForAppComponent props,
														 final NotifierAppDependentConfigProviderFromProperties appDepConfigProvider) {
		return new AWSSNSNotifierConfig(props,
										appDepConfigProvider);
	}
	
	@Override
	public NotifierConfigForSMS provideSMSNotifierConfig(final NotifierPropertiesContainer props,
														 final NotifierAppDependentConfigProviderFromRecordProperties appDepConfigProvider) {
		return new AWSSNSNotifierConfig(props,
                                         appDepConfigProvider);
	}
}
