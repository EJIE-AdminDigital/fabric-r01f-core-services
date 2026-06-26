package r01f.core.services.mail;

import java.util.Properties;

import org.springframework.mail.javamail.JavaMailSender;

import r01f.securitycontext.SecurityIDS.LoginID;
import r01f.securitycontext.SecurityIDS.Password;


/**
 * Uses GMail SMTP service to send mail
 * How to create an app password:
 *		1.- Login to the account settings: https://myaccount.google.com/
 *		2.- Find the [Signing in] section and click on [App passwords]
 *		3.- Select [Other(custom name)] and give it a name (ie appCode)
 *		4.- Copy the generated password and put it here
 */
@Deprecated
public class JavaMailSenderGMailSMTPImpl
	 extends JavaMailSenderSMTPImpl {
/////////////////////////////////////////////////////////////////////////////////////////
//	BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Creates a {@link JavaMailSender} to send an email using gmail
	 * @param userCode
	 * @param password
	 * @return
	 */
	public static JavaMailSender create(final String userCode,
							 			final String password) {
		return JavaMailSenderGMailSMTPImpl.create(LoginID.forId(userCode),
									   	   		  Password.forId(password));
	}
	/**
	 * Creates a {@link JavaMailSender} to send an email using gmail
	 * @param userCode
	 * @param password
	 * @return
	 */
	public static JavaMailSender create(final LoginID userCode,
							 			final Password password) {
		Properties javaMailProps = JavaMailSenderSMTPImpl.createJavaMailProperties();
		javaMailProps.put("mail.smtp.auth",true);
		javaMailProps.put("mail.smtp.starttls.enable",true);

		JavaMailSenderSMTPImpl outMailSender = new JavaMailSenderSMTPImpl();
		outMailSender.setHost("smtp.gmail.com");
		outMailSender.setPort(587);
		outMailSender.setUsername(userCode.asString());
		outMailSender.setPassword(password.asString());	// see https://support.google.com/accounts/answer/185833
		outMailSender.setJavaMailProperties(javaMailProps);

		return outMailSender;
	}

}
