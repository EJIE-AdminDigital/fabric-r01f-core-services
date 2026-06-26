package r01f.core.services.notifier.config;

import lombok.experimental.Accessors;
import r01f.core.services.notifier.config.NotifierEnums.NotifierType;
import r01f.guids.CommonOIDs.AppCode;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

@Accessors(prefix="_")
public class NotifierConfigForLog
     extends NotifierConfigForMediumBase {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	// nothing specific
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public NotifierConfigForLog(final AppCode appCode,
								final boolean enabled) {
		super(NotifierType.LOG,
			  appCode,
			  enabled,
			  null,null,	// no service impl dependent config
			  null);		// no app-dependent config
	}
	public NotifierConfigForLog(final XMLPropertiesForAppComponent props) {
		super(NotifierType.LOG,
			  props,
			  null,		// no service impl dependent config
			  null);	// no app-dependent config
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean isSelectedImpl() {
		return true;
	}
}
