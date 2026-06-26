package r01f.core.services.notifier.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.enums.EnumExtended;
import r01f.guids.OIDBaseImmutable;
import r01f.types.contact.NotificationMedium;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class NotifierEnums {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static enum NotifierType
 		    implements EnumExtended<NotifierType> {
		EMAIL,
		SMS,
		VOICE,
		LOG,
		PUSH;

		public String asStringLowerCase() {
			return this.name().toLowerCase();
		}
		public static NotifierType from(final NotificationMedium medium) {
			NotifierType outType = null;
			outType = switch (medium) {
			case EMAIL -> EMAIL;
			case LOG -> LOG;
			case SMS -> SMS;
			case VOICE -> VOICE;
			case PUSH -> PUSH;
			default -> throw new IllegalArgumentException(medium + " is NOT a recognized notifier type!");
			};
			return outType;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static class NotifierImpl
				extends OIDBaseImmutable<String> {

		private static final long serialVersionUID = -7135064660162600274L;
		protected NotifierImpl(final String id) {
			super(id);
		}
		public static NotifierImpl valueOf(final String id) {
			return new NotifierImpl(id);
		}
		public static NotifierImpl forId(final String id) {
			return new NotifierImpl(id);
		}
	}
	@Accessors(prefix="_")
	public static enum EMailNotifierImpl {
		SMTP("smtp"),
		AWS("aws"),
		GOOGLE_API("google/api"),
		GOOGLE_SMTP("google/smtp"),
		REST_SERVICE("restService");

		@Getter private final NotifierImpl _id;

		private EMailNotifierImpl(final String id) {
			_id = NotifierImpl.forId(id);
		}
		public boolean is(final NotifierImpl impl) {
			return _id.is(impl);
		}
	}
	@Accessors(prefix="_")
	public static enum SMSNotifierImpl {
		AWS("aws"),
		LATINIA("latinia");

		@Getter private final NotifierImpl _id;

		private SMSNotifierImpl(final String id) {
			_id = NotifierImpl.forId(id);
		}
		public boolean is(final NotifierImpl impl) {
			return _id.is(impl);
		}
	}
	@Accessors(prefix="_")
	public static enum VoiceNotifierImpl {
		TWILIO("twilio");

		@Getter private final NotifierImpl _id;

		private VoiceNotifierImpl(final String id) {
			_id = NotifierImpl.forId(id);
		}
		public boolean is(final NotifierImpl impl) {
			return _id.is(impl);
		}
	}
	@Accessors(prefix="_")
	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	public static enum LogNotifierImpl {
		LOG("log");

		@Getter private final NotifierImpl _id;

		private LogNotifierImpl(final String id) {
			_id = NotifierImpl.forId(id);
		}
		public boolean is(final NotifierImpl impl) {
			return _id.is(impl);
		}
	}
	@Accessors(prefix="_")
	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	public static enum PushMessageNotifierImpl {
		FIREBASE("firebase"),
		AZURE("azure");

		@Getter private final NotifierImpl _id;

		private PushMessageNotifierImpl(final String id) {
			_id = NotifierImpl.forId(id);
		}
		public boolean is(final NotifierImpl impl) {
			return _id.is(impl);
		}
	}
}
