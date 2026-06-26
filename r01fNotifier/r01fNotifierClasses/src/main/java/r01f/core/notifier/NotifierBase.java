package r01f.core.notifier;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.core.services.notifier.NotifierService;
import r01f.core.services.notifier.config.NotifierConfig;
import r01f.service.ServiceCanBeDisabled;

/**
 * Base notifier
 */
@Accessors(prefix="_")
public abstract class NotifierBase<C extends NotifierConfig,
								   M>   // the message
    	   implements Notifier<M> {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter protected final C _config;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTORS
/////////////////////////////////////////////////////////////////////////////////////////
	protected NotifierBase(final C config) {
		_config = config;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean isEnabled() {
		return _config != null ? _config.isEnabled()
							   : false;
	}
	public boolean isEnabledConsidering(final NotifierService<?,?,?> notifierService) {
		boolean isEnabled = this.isEnabled();
		if (notifierService instanceof ServiceCanBeDisabled) {
			ServiceCanBeDisabled serviceCanBeDisabled = (ServiceCanBeDisabled)notifierService;
			if (serviceCanBeDisabled.isDisabled()) isEnabled = false;
		}
		return isEnabled;
	}
}
