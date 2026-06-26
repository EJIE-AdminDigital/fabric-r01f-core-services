package r01f.core.services.notifier.config;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.config.ContainsConfigData;
import r01f.core.services.notifier.config.NotifierConfigProviders.NotifierAppDependentConfigProviderFromProperties;
import r01f.core.services.notifier.config.NotifierConfigProviders.NotifierServiceImplDependentConfigProviderFromProperties;
import r01f.core.services.notifier.config.NotifierEnums.NotifierImpl;
import r01f.core.services.notifier.config.NotifierEnums.NotifierType;
import r01f.guids.CommonOIDs.AppCode;
import r01f.types.contact.OwnedContactMean;
import r01f.types.contact.Phone;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

@Accessors(prefix="_")
public abstract class NotifierConfigForVoice
     		  extends NotifierConfigForMediumBase {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final OwnedContactMean<Phone> _from;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public NotifierConfigForVoice(final AppCode appCode,
								  final boolean enabled,
								  final NotifierImpl impl,final ContainsConfigData serviceImplDepConfig,
								  final ContainsConfigData appDepConfig,
								  final OwnedContactMean<Phone> from) {
		super(NotifierType.VOICE,
			  appCode,
			  enabled,
			  impl,serviceImplDepConfig,
			  appDepConfig);
		_from = from;
	}
	/**
	 * Use the implementation-specific loader (ie: TwilioNotifierServiceConfigLoader)
	 * @param props
	 * @param implDependentPropsProvider
	 */
	public NotifierConfigForVoice(final XMLPropertiesForAppComponent props,
								  final NotifierServiceImplDependentConfigProviderFromProperties serviceImplDepConfigProvider,
								  final NotifierAppDependentConfigProviderFromProperties appDepConfigProvider) {
		super(NotifierType.VOICE,
			  props,
			  serviceImplDepConfigProvider,
			  appDepConfigProvider);
		Phone fromPhone = props.propertyAt(_xPathBase() + "/from/@phone")
							   .asPhone("no-from-phone-configured");
		String fromOwner = props.propertyAt(_xPathBase() + "/from")
								.asString("no-from-owner-configured");

		_from = new OwnedContactMean<Phone>(fromPhone,fromOwner);
	}
}
