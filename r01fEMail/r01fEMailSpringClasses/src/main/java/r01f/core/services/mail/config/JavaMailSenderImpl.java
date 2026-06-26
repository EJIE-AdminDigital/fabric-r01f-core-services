package r01f.core.services.mail.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.core.services.notifier.config.NotifierEnums.NotifierImpl;

@Accessors(prefix="_")
@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
public enum JavaMailSenderImpl {
	SMTP("smtp"),			// ie ms exchange
	AWS_SES("aws"),
	GOOGLE_API("google/api"),
	GOOGLE_SMTP("google/smtp"),
	REST_SERVICE("restService");

	@Getter private final String _id;

	public static JavaMailSenderImpl from(final NotifierImpl notifierImpl) {
		JavaMailSenderImpl outImpl = null;
		for (JavaMailSenderImpl i : JavaMailSenderImpl.values()) {
			if (i.getId().equals(notifierImpl.getId())) {
				outImpl = i;
				break;
			}
		}
		if (outImpl == null) throw new IllegalStateException("NO spring java mail sender impl for id=" + notifierImpl);
		return outImpl;
	}
}
