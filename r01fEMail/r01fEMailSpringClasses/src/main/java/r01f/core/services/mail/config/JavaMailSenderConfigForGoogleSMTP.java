package r01f.core.services.mail.config;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.exceptions.Throwables;
import r01f.securitycontext.SecurityIDS.LoginAndPassword;
import r01f.securitycontext.SecurityIDS.LoginID;
import r01f.securitycontext.SecurityIDS.Password;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

@Accessors(prefix="_")
public class JavaMailSenderConfigForGoogleSMTP
	 extends JavaMailSenderConfigBase {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final LoginAndPassword _userAndPassword;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public JavaMailSenderConfigForGoogleSMTP(final LoginAndPassword userAndPassword,
											 final boolean disabled) {
		super(JavaMailSenderImpl.GOOGLE_SMTP,
			  disabled);
		_userAndPassword = userAndPassword;
	}
	public static JavaMailSenderConfigForGoogleSMTP createFrom(final XMLPropertiesForAppComponent xmlProps,
												      		   final String propsRootNode) {
		// Get the user & password from the properties file
		LoginAndPassword userAndPassword = JavaMailSenderConfigForGoogleSMTP.googleSMTPServiceUserAndPassword(xmlProps,
																											 propsRootNode);
		return new JavaMailSenderConfigForGoogleSMTP(userAndPassword,
													 false);	// not disabled
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	private static final String GOOGLE_SMTP_PROPS_XPATH = "/google/smtp";
	static LoginAndPassword googleSMTPServiceUserAndPassword(final XMLPropertiesForAppComponent xmlProps,
											 				 final String propsRootNode) {
		LoginID user = xmlProps.propertyAt(propsRootNode + GOOGLE_SMTP_PROPS_XPATH + "/user")
							 		.asLoginId();
		Password password = xmlProps.propertyAt(propsRootNode + GOOGLE_SMTP_PROPS_XPATH + "/password")
								 .asPassword();
		// Check
		if (user == null || password == null) {
			throw new IllegalStateException(Throwables.message("Cannot configure Google SMTP: the properties file does NOT contains a the user or password at {} in {} properties file",
															   propsRootNode + GOOGLE_SMTP_PROPS_XPATH,xmlProps.getAppCode()));
		}
		return new LoginAndPassword(user,password);
	}

}
