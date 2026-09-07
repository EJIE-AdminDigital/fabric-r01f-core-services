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
import r01f.guids.CommonOIDs.IsAppCode;
import r01f.internal.R01FAppCodes;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

/**
 * Base type for a medium (sms, email, voice...) notifier config
 *
 * The notifier config is divided into THREE different parts
 *		[1] - A 'common' part: contains common notifier properties like whether
 *		  				   	   the notifier is enabled or the [from] address
 *						   	   All these properties DO NOT depend upon the specific
 *						   	   implementation of the notifier
 *
 *		[2] - A notifier impl specific part that contains properties that depend upon the
 *		  	  concrete service which the notifier uses to perform it's job
 * 		  	  (ie: an SMS notifier can use many SMS-sender services like [amazon SNS] or [latinia]
 * 			       config part contains the concrete SMS-sender service implementation config)
 *
 *		[3] - An application specific part that contains application-arbitrary config
 *			  like paths to templates, or whatever
 */
@Accessors(prefix="_")
abstract class NotifierConfigForMediumBase
	   extends NotifierConfigBase
    implements NotifierConfig {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter protected final boolean _enabled;
	@Getter protected final NotifierImpl _impl;
	@Getter protected final ContainsConfigData _serviceImplConfig;
	@Getter protected final ContainsConfigData _appConfig;

	/**
	 * Returns true if this config matches the selected impl
	 * This method should check if the impl is the correct one
	 */
	public abstract boolean isSelectedImpl();
	/**
	 * Returns the config of the underlying service that the notifier uses to do it's job
	 * (ie: an SMS notifier can use many SMS-sender services like [amazon SNS] or [latinia]
	 * 		this method returns the concrete SMS-sender service implementation config)
	 * @param <C>
	 * @param configType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <C extends ContainsConfigData> C getServiceImplConfigAs(final Class<C> configType) {
		return (C)_serviceImplConfig;
	}
	/**
	 * Returns the app-specific config
	 * @param <C>
	 * @param configType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <C extends ContainsConfigData> C getAppConfigAs(final Class<C> configType) {
		return (C)_appConfig;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	protected NotifierConfigForMediumBase(final NotifierType type,
										  final IsAppCode appCode,
								 		  final boolean enabled,
								 		  final NotifierImpl impl,final ContainsConfigData serviceImplConfig,
								 		  final ContainsConfigData appConfig) {
		super(type,
			  appCode);
		_enabled = enabled;
		_impl = impl;
		_serviceImplConfig = serviceImplConfig;
		_appConfig = appConfig;
	}
	protected NotifierConfigForMediumBase(final NotifierType type,
								 		  final XMLPropertiesForAppComponent props,
								 		  final NotifierServiceImplDependentConfigProviderFromProperties implDepConfigProvider,
								 		  final NotifierAppDependentConfigProviderFromProperties appDepConfigProvider) {
		this(// type
			 type,
			 // appCode
			 props.getAppCode(),
			 // enabled
			 props.propertyAt(_xPathBase(type) + "/@enabled")
			 	  .asBoolean(true),
			 // impl
			 _implFrom(type,props),
			 // service impl dependent config
			 implDepConfigProvider != null ? implDepConfigProvider.provideConfigUsing(_implFrom(type,props),
					 																  props)
					 					   : null,
			 // app dependent config
			 appDepConfigProvider != null ? appDepConfigProvider.provideConfigUsing(_implFrom(type,props),
					 																props)
					 					  : null);
	}
	
	public NotifierConfigForMediumBase(final NotifierType type, 
            						   final NotifierPropertiesContainer propertiesConstainer,
            						   final NotifierServiceImplDependentConfigProviderFromRecordProperties serviceImplDepConfigProvider,
            						   final NotifierAppDependentConfigProviderFromRecordProperties appDepConfigProvider)  {
		this(// type
			type,
			// appCode
			propertiesConstainer.appcode() == null? R01FAppCodes.APP_CODE : propertiesConstainer.appcode(),
			// enabled
			_isEnabledImplFrom(type,propertiesConstainer),			
			// impl
			_implFrom(type,propertiesConstainer),
			// service impl dependent config
			serviceImplDepConfigProvider != null ? serviceImplDepConfigProvider.provideConfigUsing(_implFrom(type,propertiesConstainer),
																							       propertiesConstainer)
										 : null,
			// app dependent config
			appDepConfigProvider != null ? appDepConfigProvider.provideConfigUsing(_implFrom(type,propertiesConstainer),
			                                                                 propertiesConstainer)
									: null);
		}
}
