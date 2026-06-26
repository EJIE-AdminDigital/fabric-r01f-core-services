package r01f.core.services.notifier.config;

import lombok.experimental.Accessors;
import r01f.core.services.notifier.config.NotifierEnums.NotifierType;
import r01f.guids.CommonOIDs.AppCode;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

/**
 * Base for the app-dependent config part of a notifier config (see {@link NotifierConfigForMediumBase})
 */
@Accessors(prefix="_")
public abstract class NotifierAppDependentConfigBase
		      extends NotifierConfigBase {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	protected NotifierAppDependentConfigBase(final NotifierType type,
										     final AppCode appCode) {
		super(type,
			  appCode);
	}
	protected NotifierAppDependentConfigBase(final NotifierType type,
										     final XMLPropertiesForAppComponent props) {
		super(type,
			  props);
	}
}
