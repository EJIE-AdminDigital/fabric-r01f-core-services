package r01f.core.services.notifier.config;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.config.ContainsConfigData;
import r01f.core.services.notifier.config.NotifierConfigProviders.NotifierAppDependentConfigProviderFromProperties;
import r01f.core.services.notifier.config.NotifierConfigProviders.NotifierAppDependentConfigProviderFromRecordProperties;
import r01f.core.services.notifier.config.NotifierConfigProviders.NotifierServiceImplDependentConfigProviderFromProperties;
import r01f.core.services.notifier.config.NotifierConfigProviders.NotifierServiceImplDependentConfigProviderFromRecordProperties;
import r01f.core.services.notifier.config.NotifierEnums.NotifierImpl;
import r01f.core.services.notifier.config.NotifierEnums.NotifierType;
import r01f.core.services.notifier.properties.NotifierPropertiesContainer;
import r01f.core.services.notifier.properties.NotifierPropertiesForSMS;
import r01f.guids.CommonOIDs.AppCode;
import r01f.types.contact.OwnedContactMean;
import r01f.types.contact.Phone;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

@Accessors(prefix="_")
public abstract class NotifierConfigForSMS
     		  extends NotifierConfigForMediumBase {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final OwnedContactMean<Phone> _from;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public NotifierConfigForSMS(final AppCode appCode,
								final boolean enabled,
								final NotifierImpl impl,final ContainsConfigData serviceImplDepConfig,
								final ContainsConfigData appDepConfig,
								final OwnedContactMean<Phone> from) {
		super(NotifierType.SMS,
			  appCode,
			  enabled,
			  impl,serviceImplDepConfig,
			  appDepConfig);
		_from = from;
	}
	/**
	 * Use the implementation-specific loader (ie: AWSSNSNotifierServiceConfigLoader or LatiniaNotifierServiceConfigLoader)
	 * @param props
	 * @param serviceImplDepConfigProvider
	 */
	public NotifierConfigForSMS(final XMLPropertiesForAppComponent props,
								final NotifierServiceImplDependentConfigProviderFromProperties serviceImplDepConfigProvider,
								final NotifierAppDependentConfigProviderFromProperties appDepConfigProvider) {
		super(NotifierType.SMS,
			  props,
			  serviceImplDepConfigProvider,
			  appDepConfigProvider);
		Phone fromPhone = props.propertyAt(_xPathBase() + "/from/@phone")
							   .asPhone("no-from-phone-configured");
		String fromOwner = props.propertyAt(_xPathBase() + "/from")
								.asString("no-from-owner-configured");

		_from = new OwnedContactMean<Phone>(fromPhone,fromOwner);
	}
	/**
	 * Use the implementation-specific loader (ie: AWSSNSNotifierServiceConfigLoader or LatiniaNotifierServiceConfigLoader)
	 * @param props
	 * @param serviceImplDepConfigProvider
	 */
	public NotifierConfigForSMS(final NotifierPropertiesContainer propertiesConstainer,
			                    final NotifierServiceImplDependentConfigProviderFromRecordProperties serviceImplDepConfigProvider,
                                final NotifierAppDependentConfigProviderFromRecordProperties appDepConfigProvider) {
		super(NotifierType.SMS,
			  propertiesConstainer,
			   serviceImplDepConfigProvider,
			   appDepConfigProvider);
		NotifierPropertiesForSMS propsForSMS = propertiesConstainer.notifiers().sms();
		
		Phone fromPhone =  propsForSMS.fromPhone() != null ? propsForSMS.fromPhone() 
												    : null;
		String fromName  = propsForSMS.fromName() != null ? propsForSMS.fromName() : "unknown";				
		_from = new OwnedContactMean<Phone>(fromPhone,fromName);
		
	  }
	
}
