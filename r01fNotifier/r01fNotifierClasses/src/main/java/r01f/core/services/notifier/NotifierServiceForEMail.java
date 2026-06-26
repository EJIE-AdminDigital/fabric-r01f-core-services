package r01f.core.services.notifier;

import jakarta.mail.internet.MimeMessage;

import r01f.core.services.mail.model.EMailRFC822Address;

public interface NotifierServiceForEMail
  	     extends NotifierService<EMailRFC822Address,EMailRFC822Address,		// from & to
							 	 MimeMessage> {
	// just a marker interface
}