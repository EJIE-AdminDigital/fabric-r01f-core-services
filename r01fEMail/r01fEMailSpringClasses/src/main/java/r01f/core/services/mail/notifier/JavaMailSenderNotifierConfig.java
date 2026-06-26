package r01f.core.services.mail.notifier;

import r01f.core.services.mail.config.JavaMailSenderConfig;
import r01f.core.services.mail.config.JavaMailSenderConfigBuilder;
import r01f.core.services.mail.config.JavaMailSenderImpl;
import r01f.core.services.notifier.config.NotifierConfigForEMail;
import r01f.core.services.notifier.config.NotifierConfigProviders.NotifierAppDependentConfigProviderFromProperties;
import r01f.core.services.notifier.config.NotifierConfigProviders.NotifierAppDependentConfigProviderFromRecordProperties;
import r01f.core.services.notifier.properties.NotifierImplPropertiesBase.MailSenderNotifierImplProperties;
import r01f.core.services.notifier.properties.NotifierPropertiesContainer;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

public class JavaMailSenderNotifierConfig
     extends NotifierConfigForEMail {
	/**
	 * Loads the EMail Notifier Service config from properties
	 * @param props
	 * @return
	 */
	public JavaMailSenderNotifierConfig(final XMLPropertiesForAppComponent props,
										final NotifierAppDependentConfigProviderFromProperties appDepConfigProvider) {
		super(props,
			  // provide notifier service config from properties
			  (impl,props1) -> {
					if (impl == null) throw new IllegalStateException("null mail sender impl! (review notifier properties)");
					
					JavaMailSenderImpl springMailSenderImpl = JavaMailSenderImpl.from(impl);
					JavaMailSenderConfig springMailSenderCfg = JavaMailSenderConfigBuilder.of(springMailSenderImpl)
															  							  .from(props1,"notifier/email");
					return springMailSenderCfg;
			   },
			   // the app-dependent config
			   appDepConfigProvider);
	}
	
	/**
	 * Loads the EMail Notifier Service config from record properties
	 * @param props
	 * @return
	 */
	public JavaMailSenderNotifierConfig(final NotifierPropertiesContainer propertiesContainer,
			                            final NotifierAppDependentConfigProviderFromRecordProperties appDepConfigProvider) {
		super(propertiesContainer,
			 (impl,props1) -> {
								if (impl == null) throw new IllegalStateException("null mail sender impl! (review notifier properties)");
								System.out.println(impl);
								System.out.println(propertiesContainer.impForMail().getClass());
								//JavaMailSenderNotifierProperties props = propertiesContainer.impForMail().as(JavaMailSenderNotifierProperties.class);
								MailSenderNotifierImplProperties imps = (MailSenderNotifierImplProperties) propertiesContainer.impForMail();
								JavaMailSenderNotifierProperties props = new JavaMailSenderNotifierProperties();
								props.setImpls(imps.getImpls());
								
								JavaMailSenderImpl springMailSenderImpl =  JavaMailSenderImpl.from(impl);
								JavaMailSenderConfig springMailSenderCfg = JavaMailSenderConfigBuilder.of(springMailSenderImpl)
																		  							  .from(props);
								
								
								return springMailSenderCfg;
			 },
			  appDepConfigProvider);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean isSelectedImpl() {
		return true;
	}
}
