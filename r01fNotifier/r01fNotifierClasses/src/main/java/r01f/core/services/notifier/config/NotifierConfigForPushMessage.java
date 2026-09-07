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
import r01f.core.services.notifier.properties.NotifierPropertiesForPush;
import r01f.guids.CommonOIDs.AppCode;
import r01f.guids.CommonOIDs.IsAppCode;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

@Accessors(prefix="_")
public abstract class NotifierConfigForPushMessage
     		  extends NotifierConfigForMediumBase {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final IsAppCode _from;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public NotifierConfigForPushMessage(final AppCode appCode,
										final boolean enabled,
										final NotifierImpl impl,final ContainsConfigData serviceImplDepConfig,
										final ContainsConfigData appDepConfig) {
		super(NotifierType.PUSH,
			  appCode,
			  enabled,
			  impl,serviceImplDepConfig,
			  appDepConfig);
		_from = appCode;
	}
	/**
	 * Use the implementation-specific loader (ie: PushNotifierServiceConfigLoader)
	 * @param props
	 * @param implDependentPropsProvider
	 */
	public NotifierConfigForPushMessage(final XMLPropertiesForAppComponent props,
								        final NotifierServiceImplDependentConfigProviderFromProperties serviceImplDepConfigProvider,
								        final NotifierAppDependentConfigProviderFromProperties appDepConfigProvider) {
		super(NotifierType.PUSH,
			  props,
			  serviceImplDepConfigProvider,
			  appDepConfigProvider);

		_from = props.getAppCode();
	}
	
	/**
	 * Use the implementation-specific loader (ie: PushNotifierServiceConfigLoader)
	 * @param props
	 * @param serviceImplDepConfigProvider
	 */
	public NotifierConfigForPushMessage(final NotifierPropertiesContainer propertiesConstainer,
			                            final NotifierServiceImplDependentConfigProviderFromRecordProperties serviceImplDepConfigProvider,
                                        final NotifierAppDependentConfigProviderFromRecordProperties appDepConfigProvider) {
		super(NotifierType.PUSH,
			  propertiesConstainer,
			   serviceImplDepConfigProvider,
			   appDepConfigProvider);
		NotifierPropertiesForPush propsForPush = propertiesConstainer.notifiers().push();
		
		AppCode appCode =  propsForPush.from() != null ? propsForPush.from() 
												       : null;
				
		_from = appCode;
		
	  }
	
}
