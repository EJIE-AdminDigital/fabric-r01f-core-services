package r01f.core.services.mail.notifier;

import java.util.Properties;

import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.core.services.mail.JavaMailSenderProperties.AWSProperties;
import r01f.core.services.mail.JavaMailSenderProperties.RESTProperties;
import r01f.core.services.mail.JavaMailSenderProperties.SMTPProperties;
import r01f.core.services.notifier.config.NotifierEnums.EMailNotifierImpl;
import r01f.core.services.notifier.properties.NotifierImpPropertiesForMail;
import r01f.core.services.notifier.properties.NotifierImplPropertiesBase.MailSenderNotifierImplProperties;


@Slf4j
@Accessors(prefix="_")
public class JavaMailSenderNotifierProperties 
             extends MailSenderNotifierImplProperties 
         implements NotifierImpPropertiesForMail {
/////////////////////////////////////////////////////////////////////////////////////////
// CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
    public JavaMailSenderNotifierProperties() {
        super();   
    }
/////////////////////////////////////////////////////////////////////////////////////////
// METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	public SMTPProperties smtp() {	
		debugMap();
		Properties smtpData  =  _impls.get(EMailNotifierImpl.SMTP.getId());
		if (smtpData == null) {
			log.warn("..smtp-data-properties are null");
			return null;
		}
		return SMTPProperties.from(smtpData);
	}

	public AWSProperties aws() {	
		debugMap();
		Properties awsData  =  _impls.get(EMailNotifierImpl.AWS.getId());
		if (awsData == null) {
			log.warn("..aws-data-properties are null");
			return null;
		}
		return AWSProperties.from(awsData);
	}

	public RESTProperties rest() {	
		debugMap();
		Properties rest = _impls.get(EMailNotifierImpl.REST_SERVICE.getId());
		if (rest == null) {
			log.warn("..rest-data-properties are null");
			return null;
		}
		return RESTProperties.from(rest);
	}	
}
