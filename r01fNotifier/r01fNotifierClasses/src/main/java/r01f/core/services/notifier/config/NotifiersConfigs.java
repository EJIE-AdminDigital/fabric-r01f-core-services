package r01f.core.services.notifier.config;

import java.util.Collection;

import com.google.common.collect.Lists;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.config.ContainsConfigData;
import r01f.core.services.notifier.config.NotifierEnums.NotifierType;
import r01f.debug.Debuggable;
import r01f.patterns.Memoized;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

@Accessors(prefix="_")
public class NotifiersConfigs
  implements ContainsConfigData,
  			 Debuggable {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final NotifierConfigForEMail _forEMail;
	@Getter private final NotifierConfigForSMS _forSMS;
	@Getter private final NotifierConfigForVoice _forVoice;
	@Getter private final NotifierConfigForLog _forLog;
	@Getter private final NotifierConfigForPushMessage _forPushMessage;

/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public NotifiersConfigs(final NotifierConfigForEMail forEmail,
							final NotifierConfigForSMS forSMS,
							final NotifierConfigForVoice forVoice,
							final NotifierConfigForLog forLog,
							final NotifierConfigForPushMessage forPushMessage) {
		_forEMail = forEmail;
		_forSMS = forSMS;
		_forVoice = forVoice;
		_forLog = forLog;
		_forPushMessage = forPushMessage;
	}
	public NotifiersConfigs(final NotifierConfigForEMail forEmail) {
		this(forEmail,	// email
			 null,		// SMS
			 null,		// Voice
			 null,		// log
			 null);		// push message
	}
	public NotifiersConfigs(final NotifierConfigForEMail forEmail,
							final NotifierConfigForLog forLog) {
		this(forEmail,	// email
			 null,		// SMS
			 null,		// Voice
			 forLog,	// log
			 null);		// push message
	}
	public NotifiersConfigs(final NotifierConfigForSMS forSMS) {
		this(null,		// email
			 forSMS,	// SMS
			 null,		// Voice
			 null,		// log
			 null);		// push message
	}
	public NotifiersConfigs(final NotifierConfigForSMS forSMS,
							final NotifierConfigForLog forLog) {
		this(null,		// Email
			 forSMS,	// SMS
			 null,		// Voice
			 forLog,	// log
			 null);		// push message
	}
	public NotifiersConfigs(final NotifierConfigForEMail forEmail,
							final NotifierConfigForSMS forSMS) {
		this( forEmail,	// Email
			 forSMS,	// SMS
			 null,		// Voice
			 null,		// log
			 null);		// push message
	}
	public NotifiersConfigs(final NotifierConfigForEMail forEmail,
							final NotifierConfigForSMS forSMS,
							final NotifierConfigForLog forLog) {
		this( forEmail,	// Email
			 forSMS,	// SMS
			 null,		// Voice
			 forLog,	// log
			 null);		// push message
	}
	public NotifiersConfigs(final NotifierConfigForPushMessage forPushMessage) {
		this( null,	// Email
		null,		// SMS
		null,		// Voice
		null,		// log
		forPushMessage);	// push message
	}
	public NotifiersConfigs(final NotifierConfigForPushMessage forPushMessage,
			                final NotifierConfigForLog forLog) {
		this( null,	// Email
		null,		// SMS
		null,		// Voice
		forLog,		// log
		forPushMessage);	// push message
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	private transient final Memoized<Collection<NotifierType>> _configuredNotifierTypes = Memoized.using(() -> {
																											Collection<NotifierType> outCol = Lists.newArrayList();
																											if (this.isEMailNotifierConfigured()) 		outCol.add(NotifierType.EMAIL);
																											if (this.isMessagingNotifierConfigured())	outCol.add(NotifierType.SMS);
																											if (this.isVoiceNotifierConfigured())		outCol.add(NotifierType.VOICE);
																											if (this.isPushNotifierConfigured())		outCol.add(NotifierType.PUSH);
																											if (this.isLogNotifierConfigured())			outCol.add(NotifierType.LOG);
																											return outCol;
																										});
	public boolean doesNOTContainAnyConfiguredNotifier() {
		return CollectionUtils.isNullOrEmpty(this.getConfiguredNotifierTypes());
	}
	public Collection<NotifierType> getConfiguredNotifierTypes() {
		return _configuredNotifierTypes.get();
	}
	public boolean isNotifierConfigured(final NotifierType type) {
		return this.getConfiguredNotifierTypes().contains(type);
	}
	public boolean areAllNotifiersConfigured(final Collection<NotifierType> types) {
		return this.getConfiguredNotifierTypes().containsAll(types);
	}
	public boolean isAnyNotifierConfigured(final Collection<NotifierType> types) {
		boolean anyContained = false;
		for (NotifierType type : this.getConfiguredNotifierTypes()) {
			if (types.contains(type)) {
				anyContained = true;
				break;
			}
		}
		return anyContained;
	}
	public boolean isEMailNotifierConfigured() {
		return _forEMail != null;
	}
	public boolean isMessagingNotifierConfigured() {
		return _forSMS != null;
	}
	public boolean isVoiceNotifierConfigured() {
		return _forVoice != null;
	}
	public boolean isPushNotifierConfigured() {
		return _forPushMessage != null;
	}
	public boolean isLogNotifierConfigured() {
		return _forLog != null;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	DEBUG
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CharSequence debugInfo() {
		StringBuilder sb = new StringBuilder();
		sb.append("Notifiers config: ").append(this.getConfiguredNotifierTypes()).append("\n");
		sb.append(Strings.customized("\t\t-      EMail: {}\n",
									 _forEMail != null ? _forEMail.isEnabled() ? "ENABLED" : "DISABLED"
													   : "NULL"));
		sb.append(Strings.customized("\t\t-        SMS: {}\n",
									 _forSMS != null ? _forSMS.isEnabled() ? "ENABLED" : "DISABLED"
													 : "NULL"));
		sb.append(Strings.customized("\t\t-      Voice: {}\n",
									 _forVoice != null ? _forVoice.isEnabled() ? "ENABLED" : "DISABLED"
													   : "NULL"));
		sb.append(Strings.customized("\t\t-        Log: {}\n",
									 _forLog != null ? _forLog.isEnabled() ? "ENABLED" : "DISABLED"
													 : "NULL"));
		sb.append(Strings.customized("\t\t-PushMessage: {}\n",
									 _forPushMessage != null ? _forPushMessage.isEnabled() ? "ENABLED" : "DISABLED"
															 : "NULL"));
		return sb;
	}
}