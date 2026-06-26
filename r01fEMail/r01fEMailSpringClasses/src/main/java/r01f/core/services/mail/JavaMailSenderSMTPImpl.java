package r01f.core.services.mail;

import java.util.Properties;

import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import r01f.service.ServiceCanBeDisabled;
import r01f.types.url.Host;
import r01f.util.types.Strings;


/**
 * Uses Microsoft Exchange SMTP service to send mail
 */
@Slf4j
public class JavaMailSenderSMTPImpl
	 extends JavaMailSenderImpl
  implements ServiceCanBeDisabled {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	private static final int MAIL_SOCKET_TIMEOUT = 60000;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private boolean _disabled;
/////////////////////////////////////////////////////////////////////////////////////////
//  ServiceCanBeDisabled
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean isEnabled() {
		return !_disabled;
	}
	@Override
	public boolean isDisabled() {
		return _disabled;
	}
	@Override
	public void setEnabled() {
		_disabled = false;
	}
	@Override
	public void setDisabled() {
		_disabled = true;
	}
	
/////////////////////////////////////////////////////////////////////////////////////////
// SEND
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void send(MimeMessage... mimeMessages) throws MailException {
		log.warn("...JavaMailSenderSMTPImpl [smtp:'{}'] sender ",super.getHost());
		super.doSend(mimeMessages,null);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Creates a {@link JavaMailSender} to send an email using a SMTP server
	 * @param host
	 * @param port
	 * @return
	 */
	public static JavaMailSender create(final Host host) {
		if (Strings.isNullOrEmpty(host.asString())) throw new IllegalArgumentException("Invalid SMTP HOST");
		Properties javaMailProps = JavaMailSenderSMTPImpl.createJavaMailProperties();
		javaMailProps.put("mail.smtp.host",host.asString());

		JavaMailSenderImpl outMailSender = new JavaMailSenderSMTPImpl();
		outMailSender.setHost(host.asString());
		outMailSender.setJavaMailProperties(javaMailProps);

		return outMailSender;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	protected static Properties createJavaMailProperties() {
		Properties props = new Properties();

		// Set a fixed timeout of 60s for all operations - the default timeout is "infinite"
		// see http://www.javacodegeeks.com/2014/06/javamail-can-be-evil-and-force-you-to-restart-your-app-server.html
		props.put("mail.smtp.connectiontimeout",MAIL_SOCKET_TIMEOUT);
		props.put("mail.smtp.timeout", MAIL_SOCKET_TIMEOUT);
		props.put("mail.smtp.writetimeout",MAIL_SOCKET_TIMEOUT);

		return props;
	}
}
