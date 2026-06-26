package r01f.core.services.notifier.config;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.config.ContainsConfigData;
import r01f.core.services.mail.model.EMailRFC822Address;
import r01f.core.services.notifier.config.NotifierConfigProviders.NotifierAppDependentConfigProviderFromProperties;
import r01f.core.services.notifier.config.NotifierConfigProviders.NotifierAppDependentConfigProviderFromRecordProperties;
import r01f.core.services.notifier.config.NotifierConfigProviders.NotifierServiceImplDependentConfigProviderFromProperties;
import r01f.core.services.notifier.config.NotifierConfigProviders.NotifierServiceImplDependentConfigProviderFromRecordProperties;
import r01f.core.services.notifier.config.NotifierEnums.NotifierImpl;
import r01f.core.services.notifier.config.NotifierEnums.NotifierType;
import r01f.core.services.notifier.properties.NotifierPropertiesContainer;
import r01f.core.services.notifier.properties.NotifierPropertiesForMail;
import r01f.guids.CommonOIDs.AppCode;
import r01f.types.contact.EMail;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

@Accessors(prefix="_")
public abstract class NotifierConfigForEMail
     		  extends NotifierConfigForMediumBase {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final EMailRFC822Address _from;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public NotifierConfigForEMail(final AppCode appCode,
								  final boolean enabled,
								  final NotifierImpl impl,final ContainsConfigData serviceImplDepConfig,
								  final ContainsConfigData appDepConfig,
								  final EMail fromMail,final String fromName) {
		super(NotifierType.EMAIL,
			  appCode,
			  enabled,
			  impl,serviceImplDepConfig,
			  appDepConfig);
		_from = EMailRFC822Address.of(fromMail,fromName);
	}
	/**
	 * Use the implementation-specific loader (ie: JavaMailSenderNotifierServiceConfigLoader)
	 * @param props
	 * @param serviceImplDepConfigProvider
	 */
	public NotifierConfigForEMail(final XMLPropertiesForAppComponent props,
							      final NotifierServiceImplDependentConfigProviderFromProperties serviceImplDepConfigProvider,
							      final NotifierAppDependentConfigProviderFromProperties appDepConfigProvider) {
		super(NotifierType.EMAIL,
			  props,
			  serviceImplDepConfigProvider,
			  appDepConfigProvider);
		EMail fromMail = props.propertyAt(_xPathBase() + "/from/@mail")
							  .asEMail("no-from-email-addr-configured");
		String fromName = props.propertyAt(_xPathBase() + "/from")
							   .asString("no-from-name-configured");

		_from = EMailRFC822Address.of(fromMail,fromName);
	}
	
	/**
	 * Use the implementation-specific loader (ie: JavaMailSenderNotifierServiceConfigLoader) based un record properties
	 * @param props
	 * @param serviceImplDepConfigProvider
	 */
	public NotifierConfigForEMail(final NotifierPropertiesContainer propertiesConstainer,
			                      final NotifierServiceImplDependentConfigProviderFromRecordProperties serviceImplDepConfigProvider,
                                  final NotifierAppDependentConfigProviderFromRecordProperties appDepConfigProvider) {
		
		super(NotifierType.EMAIL,
			  propertiesConstainer,
			   serviceImplDepConfigProvider,
			   appDepConfigProvider);
		NotifierPropertiesForMail propsForMail = propertiesConstainer.notifiers().email();
		
		EMail fromMail =  propsForMail.from() != null ? EMail.of( propsForMail.from()) 
															  : EMail.of("unknown");
		String fromName  = propsForMail.name() != null ? propsForMail.name() : "unknown";				
		_from = EMailRFC822Address.of(fromMail,fromName);
		
	  }
}
