package r01f.cloud.aws.sns.notifier;

import java.util.Properties;

import r01f.cloud.aws.AWSAccessKey;
import r01f.cloud.aws.AWSAccessSecret;
import r01f.cloud.aws.sns.AWSSNSClientConfig;
import r01f.config.ContainsConfigData;
import r01f.core.services.notifier.config.NotifierConfigForSMS;
import r01f.core.services.notifier.config.NotifierConfigProviders.NotifierAppDependentConfigProviderFromProperties;
import r01f.core.services.notifier.config.NotifierConfigProviders.NotifierAppDependentConfigProviderFromRecordProperties;
import r01f.core.services.notifier.config.NotifierEnums.NotifierImpl;
import r01f.core.services.notifier.config.NotifierEnums.SMSNotifierImpl;
import r01f.core.services.notifier.properties.NotifierImplPropertiesBase.SMSSenderNotifierImplProperties;
import r01f.core.services.notifier.properties.NotifierPropertiesContainer;
import r01f.xmlproperties.XMLPropertiesForAppComponent;
import software.amazon.awssdk.regions.Region;

public class AWSSNSNotifierConfig
	 extends NotifierConfigForSMS {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public AWSSNSNotifierConfig(final XMLPropertiesForAppComponent props,
								final NotifierAppDependentConfigProviderFromProperties appDepConfigProvider) {
		super(props,
			  // provide notifier service config from properties
		      (impl,props1) -> {
					if (impl == null) throw new IllegalStateException("null push message notifier impl! (review notifier properties)");
					
					ContainsConfigData outCfg = null;
					if (SMSNotifierImpl.AWS.is(impl)) {
						outCfg = AWSSNSClientConfig.fromXMLProperties(props1,"notifier/sms");
					} else {
						throw new IllegalStateException(impl + " is NOT a supported push notifier");
					}
					return outCfg;
			  },
			  // the app-dependent config
			  appDepConfigProvider);
	}
	
	
	public AWSSNSNotifierConfig(final NotifierPropertiesContainer props,
			 					 final NotifierAppDependentConfigProviderFromRecordProperties appDepConfigProvider) {
		super(props,
			 (impl,props1) -> {
					if (impl == null) {
						throw new IllegalStateException("null sms sender impl! (review notifier properties)");
					}
					if (SMSNotifierImpl.AWS.is(impl)) {
						SMSSenderNotifierImplProperties smsprops = props1.implForSMS().as(SMSSenderNotifierImplProperties.class);   						
						Properties implProps = smsprops.getImpls().get(impl);   						
						AWSAccessKey accessKey =   AWSAccessKey.forId( (String) implProps.getOrDefault("accessKey", appDepConfigProvider));
					    AWSAccessSecret accessSecret = AWSAccessSecret.forId( (String) implProps.getOrDefault("accessSecret", appDepConfigProvider));
					return new AWSSNSClientConfig(Region.EU_CENTRAL_1,accessKey,accessSecret);
					} else {
						return null;
					}
			},
			appDepConfigProvider);	
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean isSelectedImpl() {
		return _impl.is(NotifierImpl.forId("aws"));
	}
}
