package r01f.core.services.mail;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.experimental.Accessors;
import r01f.service.ServiceCanBeDisabled;

/**
 * A Java {@link JavaMailSender} implementation base
 */
@Accessors(prefix="_")
abstract class JavaMailSenderBase
       extends JavaMailSenderImpl
    implements ServiceCanBeDisabled {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	protected boolean _disabled;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public JavaMailSenderBase() {
		// default no-args constructor
	}
	public JavaMailSenderBase(final Properties javaMailProperties) {
		this.setJavaMailProperties(javaMailProperties);
	}
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
//
/////////////////////////////////////////////////////////////////////////////////////////
	protected String _getMimeMessageAsPlainText(final MimeMessage mimeMessage) {

		/*MimeMessageParser parser = new MimeMessageParser(mimeMessage);
		try {
			parser.parse();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		if (parser.hasHtmlContent()) {
			return parser.getHtmlContent();
		} else {
			return parser.getPlainContent();
		}*/
//		Email email = EmailConverter.mimeMessageToEmail(mimeMessage, null, false);
//		if ( email.getHTMLText() != null ) {
//			return email.getHTMLText();
//		}
//		return email.getPlainText();
		//TODO TEMPORARY ALTERNATIVE TO MIME PARSER
		try {
			String plainText = new String(mimeMessage.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
			return plainText;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
