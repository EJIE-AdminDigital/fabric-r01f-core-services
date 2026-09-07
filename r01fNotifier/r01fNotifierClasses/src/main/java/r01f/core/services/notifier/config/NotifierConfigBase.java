package r01f.core.services.notifier.config;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.config.ContainsConfigData;
import r01f.core.services.notifier.config.NotifierEnums.NotifierImpl;
import r01f.core.services.notifier.config.NotifierEnums.NotifierType;
import r01f.core.services.notifier.properties.NotifierPropertiesContainer;
import r01f.core.services.notifier.properties.NotifiersStrategy;
import r01f.guids.CommonOIDs.IsAppCode;
import r01f.patterns.FactoryFrom;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

/**
 * Loads app notifier config:
 * <pre class='brush:xml'>
		<notifier>
			<!-- BEWARE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! -->
			<!-- The notifier services (smtp mail / aws ses / aws sns / latinia / twilio...) -->
			<!-- is at the properties at the config by env module                            -->
		
			<!-- Notifiers config -->
			<notifiers>
				<log enabled="false"/>
				<email enabled="true" impl="aws">	<!-- impls: smtp | aws | google/api | google/smtp -->
					<from mail="me@futuretelematics.net">Zuzenean</from>
					... any app dependent property like:
						- message templates
						- from email address
						- etc
				</email>
				<sms enabled="false" impl="aws">	<!-- impls: latinia | aws -->
					<from phone="012">Zuzenean</from>
					... any app dependent property like:
						- message templates
						- from phone
						- etc
				</sms>
				<voice enabled="false" impl="twilio">	<!-- impls: twilio -->
					<from phone="012">Zuzenean</from>
					... any app dependent property like:
						- message templates
						- from phone
						- twilio message url
				</voice>
				<push enabled="true" impl="firebase"/>
			</notifiers>
			...
		</notifier>
 * </pre>
 */
@Accessors(prefix="_")
abstract class NotifierConfigBase
    implements ContainsConfigData {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter protected final IsAppCode _appCode;
	@Getter protected final NotifierType _type;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	protected NotifierConfigBase(final NotifierType type,
								 final IsAppCode appCode) {
		_type = type;
		_appCode = appCode;
	}
	protected NotifierConfigBase(final NotifierType type,
								 final XMLPropertiesForAppComponent props) {
		_type = type;
		_appCode = props.getAppCode();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	protected static NotifierImpl _implFrom(final NotifierType type,
										    final XMLPropertiesForAppComponent props) {
		String xpath = _xPathBase(type) + "/@impl";
		if (type.isNOT(NotifierType.LOG) && !props.propertyAt(xpath).exist()) throw new IllegalStateException("There does NOT exists the property xpath=" + xpath + " at " + props.getAppCode() + "." + props.getAppComponent() + " properties file!"); 
		return props.propertyAt(xpath)
			 	    .asOID(new FactoryFrom<String,NotifierImpl>() {
								@Override
								public NotifierImpl from(final String id) {
									return NotifierImpl.forId(id);
								}
			 	  		   });
	}
	
	
	protected static boolean _isEnabledImplFrom(final NotifierType type,
            								    final NotifierPropertiesContainer notifierContainer) {
		NotifiersStrategy strategy = notifierContainer.notifiers();
		switch (type) {
			case EMAIL :
				return strategy.email().enabled();
			case SMS :
				return strategy.sms().enabled();
			
			default : throw new IllegalStateException();
		}
	}
	
	
	protected static NotifierImpl _implFrom(final NotifierType type,
            								final NotifierPropertiesContainer notifierContainer) {		
		NotifiersStrategy strategy = notifierContainer.notifiers();
			switch (type) {
				case EMAIL :
				return strategy.email().impl();
				case SMS :
				return strategy.sms().impl();
				
				default : throw new IllegalStateException();
	}

}
	
	
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	protected String _xPathBase() {
		return _xPathBase(_type);
	}
	protected static String _xPathBase(final NotifierType type) {
		return "/notifier/notifiers/" + type.asStringLowerCase();
	}

}
