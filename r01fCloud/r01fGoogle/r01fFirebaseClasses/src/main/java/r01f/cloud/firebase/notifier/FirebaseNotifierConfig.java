package r01f.cloud.firebase.notifier;

import java.util.Properties;

import r01f.cloud.firebase.service.FirebaseConfig;
import r01f.config.ContainsConfigData;
import r01f.core.services.notifier.config.NotifierConfigForPushMessage;
import r01f.core.services.notifier.config.NotifierConfigProviders.NotifierAppDependentConfigProviderFromProperties;
import r01f.core.services.notifier.config.NotifierConfigProviders.NotifierAppDependentConfigProviderFromRecordProperties;
import r01f.core.services.notifier.config.NotifierEnums.NotifierImpl;
import r01f.core.services.notifier.config.NotifierEnums.PushMessageNotifierImpl;
import r01f.core.services.notifier.properties.NotifierImplPropertiesBase.PushSenderNotifierImplProperties;
import r01f.core.services.notifier.properties.NotifierPropertiesContainer;
import r01f.xmlproperties.XMLPropertiesForAppComponent;



public class FirebaseNotifierConfig
	 extends NotifierConfigForPushMessage {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public FirebaseNotifierConfig(final XMLPropertiesForAppComponent props,
								  final NotifierAppDependentConfigProviderFromProperties appDepConfigProvider) {
		super(props,
  			  // provide notifier service config from properties
			  (impl,props1) -> {
					if (impl == null) throw new IllegalStateException("null push message notifier impl! (review notifier properties)");
					
					ContainsConfigData outCfg = null;
					if (PushMessageNotifierImpl.FIREBASE.is(impl)) {
						outCfg = FirebaseConfig.createFrom(props1,"notifier/push/firebase");
					} else {
						throw new IllegalStateException(impl + " is NOT a supported push notifier");
					}
					return outCfg;
			 },
			 // the app-dependent config
			 appDepConfigProvider);
	}
	
	/**
	 * Loads the PUSH Notifier Service config from record properties
	 * @param props
	 * @return
	 */
	public FirebaseNotifierConfig(final NotifierPropertiesContainer propertiesContainer,
			                      final NotifierAppDependentConfigProviderFromRecordProperties appDepConfigProvider) {
		super(propertiesContainer,
			 (impl,props1) -> {
								if (impl == null) throw new IllegalStateException("null push sender impl! (review notifier properties)");
								
								PushSenderNotifierImplProperties props = propertiesContainer.implForSMS().as(PushSenderNotifierImplProperties.class);
								
								Properties p = props.getImpls().get(impl);
								FirebaseNotifierProperties  senderProperties = FirebaseNotifierProperties.create(p);
								return  FirebaseNotifierProperties.serviceAPIDataFrom(senderProperties);
			 },
			 appDepConfigProvider);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean isSelectedImpl() {
		return _impl.is(NotifierImpl.forId("firebase"));
	}
}
