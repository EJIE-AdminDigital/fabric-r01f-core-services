package r01f.core.services.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import r01f.cloud.aws.ses.AWSSESClient;
import r01f.cloud.aws.ses.AWSSESClientConfig;
import r01f.core.services.mail.config.JavaMailSenderConfigForAWSSES;
import r01f.core.services.mail.model.EMailDestinations;
import r01f.core.services.mail.model.EMailMessage;
import r01f.core.services.mail.model.EMailRFC822Address;
import software.amazon.awssdk.services.ses.model.SendEmailResponse;
import software.amazon.awssdk.services.ses.model.SendRawEmailResponse;
import software.amazon.awssdk.services.ses.model.SesResponse;

@Slf4j
@RequiredArgsConstructor
public class JavaMailSenderAWSSESImpl
     extends JavaMailSenderBase {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final AWSSESClientConfig _config;
/////////////////////////////////////////////////////////////////////////////////////////
//	BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Creates a {@link JavaMailSender} to send an email using a SMTP server
	 * @param host
	 * @param port
	 * @return
	 */
	public static JavaMailSender create(final JavaMailSenderConfigForAWSSES config) {
		if (config == null) throw new IllegalArgumentException("Invalid AWS SES config ");

		JavaMailSender outMailSender = new JavaMailSenderAWSSESImpl(config.getAwsSESClientConfig());
		return outMailSender;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void send(final SimpleMailMessage... simpleMessages) throws MailException {
		try {
			AWSSESClient sesCli = new AWSSESClient(_config);

			for (SimpleMailMessage simpleMessage : simpleMessages) {
				// Conver to EMailMessage & send
				EMailMessage emailMsg = JavaMailSenders.eMailMessageFrom(simpleMessage);
				SesResponse res = sesCli.sendEMail(emailMsg);

				// log
				SendEmailResponse emailRes = (SendEmailResponse)res;
				log.info("{} > email sent with id={}",
						 JavaMailSenderAWSSESImpl.class.getSimpleName(),
						 emailRes.messageId());
			}
		} catch (final MessagingException msgEx) {
			throw new MailSendException(msgEx.getMessage(),
										msgEx);
		}
	}
	@Override
	protected void doSend(final MimeMessage[] mimeMessages,
						  final Object[] originalMessages) throws MailException {
		try {
			AWSSESClient sesCli = new AWSSESClient(_config);

			for (MimeMessage mimeMessage : mimeMessages) {
				// this is a bit weird: have to 'dis-assemble' the email mime message
				// just to get some data about it like [from: to: cc: bcc:]
				EMailRFC822Address from = EMailMimeMessages.emailMessageFromOf(mimeMessage);
				EMailDestinations dest = EMailMimeMessages.emailMessageDestinationsOf(mimeMessage);
				SesResponse res = sesCli.sendEMail(from,dest,
												   mimeMessage);

				// log
				SendRawEmailResponse emailRes = (SendRawEmailResponse)res;
				log.info("{} > email sent with id={}",
						 JavaMailSenderAWSSESImpl.class.getSimpleName(),
						 emailRes.messageId());
			}
		} catch (MessagingException msgEx) {
			throw new MailSendException(msgEx.getMessage(),
										msgEx);
		}
	}
}
