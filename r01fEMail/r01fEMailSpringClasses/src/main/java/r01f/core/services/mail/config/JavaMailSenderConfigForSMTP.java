package r01f.core.services.mail.config;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.core.services.mail.JavaMailSenderProperties.SMTPProperties;
import r01f.exceptions.Throwables;
import r01f.types.url.Host;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

@Accessors(prefix="_")
public class JavaMailSenderConfigForSMTP
	 extends JavaMailSenderConfigBase {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final Host _mailServerHost;
//	@Getter private final HttpClientProxySettings _proxySettings;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public JavaMailSenderConfigForSMTP(final Host host,
									   final boolean disabled) {
		super(JavaMailSenderImpl.SMTP,
			  disabled);
		_mailServerHost = host;
	}
	public static JavaMailSenderConfigForSMTP createFrom(final XMLPropertiesForAppComponent xmlProps,
												      	 final String propsRootNode) {
		Host host = JavaMailSenderConfigForSMTP.smtpHostFromProperties(xmlProps,
																	   propsRootNode);
		return new JavaMailSenderConfigForSMTP(host,
													 false);	// not disabled
	}
	public static JavaMailSenderConfigForSMTP createFrom(final SMTPProperties properties) {
		Host host = properties.host();
		return new JavaMailSenderConfigForSMTP(host,
											   false);	// not disabled
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	private static final String SMTP_PROPS_XPATH = "/smtp";
	static Host smtpHostFromProperties(final XMLPropertiesForAppComponent props,
									   final String propsRootNode) {
		Host host = props.propertyAt(propsRootNode + SMTP_PROPS_XPATH + "/host")
					  	 .asHost();
		if (host == null) throw new IllegalStateException(Throwables.message("Cannot configure SMTP: the properties file does NOT contains a the host at {} in {} properties file",
														  propsRootNode + SMTP_PROPS_XPATH,props.getAppCode()));
		return host;
	}

}
