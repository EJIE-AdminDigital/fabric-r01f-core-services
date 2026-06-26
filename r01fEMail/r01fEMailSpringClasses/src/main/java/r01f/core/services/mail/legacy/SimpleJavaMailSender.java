package r01f.core.services.mail.legacy;

import java.io.File;
import java.util.Collection;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.activation.DataHandler;
import jakarta.activation.FileDataSource;
import jakarta.mail.BodyPart;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Part;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import r01f.exceptions.Throwables;
import r01f.mime.MimeType;
import r01f.types.Path;
import r01f.types.contact.EMail;
import r01f.types.url.Host;
import r01f.util.types.collections.CollectionUtils;

/**
 * A simple helper type to send emails
 * Usage:
 * <pre class="brush:java">
 *	  SimpleJavaMailSender mailSender = new SimpleJavaMailSender(smtpHost);
 *	  mailSender.sendMessage(from,to,
 *	  					   subject,
 *	  					   SimpleJavaMailSender.MIME_HTML,"Hello World",
 *	  					   null);		// no attachments
 * </pre>
 */
public class SimpleJavaMailSender
	 extends SimpleMailSenderBase {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * SMTP Host
	 */
	private final Host _smtpHost;
	/**
	 * Time to wait for a session
	 */
	private final int _timeout;
	/**
	 * Debug enabled?
	 */
	private final boolean _debug;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public SimpleJavaMailSender(final Host smtpHost) {
		this(smtpHost,
			 false);	// debug
	}
	public SimpleJavaMailSender(final Host smtpHost,
								final boolean debug) {
		_smtpHost = smtpHost;
		_timeout = -1;
		_debug = debug;
	}
	public SimpleJavaMailSender(final Host smtpHost,final int timeout) {
		this(smtpHost,timeout,
			 false);	// debug
	}
	public SimpleJavaMailSender(final Host smtpHost,final int timeout,
								final boolean debug) {
		_smtpHost = smtpHost;
		_timeout = timeout;
		_debug = debug;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	public void sendMessage(final EMail from,final Collection<EMail> to,
							final String subject,
							final MimeType messageContentType,final String messageText) throws AddressException,
																 							   MessagingException {
		this.sendMessage(from,to,
						 subject,
						 messageContentType,messageText,
						 null);		// no attachments
	}
	public void sendMessage(final EMail from,final Collection<EMail> to,
							final String subject,
							final MimeType messageContentType,final String messageText,
							final Path[] attachedFilesPaths) throws AddressException,
																 	MessagingException {

		this.sendMessage(from,to,null,null,
						 subject,
						 messageContentType,messageText,
						 attachedFilesPaths);
	}
	public void sendMessage(final EMail from,final Collection<EMail> to,final Collection<EMail> toCC,final Collection<EMail> toCCO,
							final String subject,
							final MimeType messageContentType,final String messageText) throws AddressException,
																 							   MessagingException {
		this.sendMessage(from,to,toCC,toCCO,
						 subject,
						 messageContentType,messageText,
						 null);		// no attachments
	}
	public void sendMessage(final EMail from,final Collection<EMail> to,final Collection<EMail> toCC,final Collection<EMail> toCCO,
							final String subject,
							final MimeType messageContentType,final String messageText,
							final Path[] attachedFilesPaths) throws AddressException,
																 	MessagingException {
		// Checks
		if (_smtpHost == null) throw new MessagingException("The SMTP host cannot be null");
		if (CollectionUtils.isNullOrEmpty(to)) throw new MessagingException("Cannot send a mail message to an unknown destination email address");

		// ----> Open SMTP session
		Properties props = new Properties();
		/////////////////////////////////////////////////////////////////////////////////////////
		// When properties are added to a Properties object since the PUT methods requires
		// 2 STRING params (if 2 Object params are given to the PUT methods when the getProperty(String) 
		//					method is invoked, it returns NULL)
		//	https://stackoverflow.com/questions/30381563/putting-objects-into-java-util-properties
		/////////////////////////////////////////////////////////////////////////////////////////
		props.put("mail.smtp.host",_smtpHost.toString());
		if (_timeout > -1 ) props.put("mail.smtp.connectiontimeout", "" + _timeout);
		props.put("mail.debug",String.valueOf(_debug));

		Session sesion = Session.getInstance(props,null);
		sesion.setDebug(false);

		// ----> Message header
		Message message = new MimeMessage(sesion);

		// FROM
		InternetAddress from_addr = new InternetAddress(from.asString());
		message.setFrom(from_addr);

		// TO
		InternetAddress[] addrsTo = SimpleJavaMailSender.eMailCollectionToInternetAddress(to);
		if (CollectionUtils.hasData(addrsTo)) message.addRecipients(Message.RecipientType.TO,addrsTo);

		// TOCC
		InternetAddress[] addrsToCC = SimpleJavaMailSender.eMailCollectionToInternetAddress(toCC);
		if (CollectionUtils.hasData(addrsToCC)) message.addRecipients(Message.RecipientType.CC,addrsToCC);

		// TOCCO
		InternetAddress[] addrsToCCO = SimpleJavaMailSender.eMailCollectionToInternetAddress(toCCO);
		if (CollectionUtils.hasData(addrsToCCO)) message.addRecipients(Message.RecipientType.BCC,addrsToCCO);

		// subject
		message.setSubject(subject);

		// ----> Message body
		//String theMessageContentType = CONTENT_TYPE_TEXT;
		//if (messageContentType != null) theMessageContentType = messageContentType;
		MimeMultipart mp = new MimeMultipart("related");
		BodyPart textMP = new MimeBodyPart();
		textMP.setDisposition(Part.INLINE);
		textMP.setContent(messageText,messageContentType.asString());
		mp.addBodyPart(textMP);

		// ----> Attached files (if they exist)
		if (CollectionUtils.hasData(attachedFilesPaths)) {
			Collection<MimeBodyPart> mimeBodyParts = Stream.of(attachedFilesPaths)
														   .map(attachedFilePath -> {
																	try {
																		MimeBodyPart file_part = new MimeBodyPart();
																		File file = new File(attachedFilePath.asAbsoluteString());
																		FileDataSource fds = new FileDataSource(file);
																		DataHandler dh = new DataHandler(fds);
																		file_part.setFileName(file.getName());
																		file_part.setDisposition(Part.ATTACHMENT);
																		file_part.setDataHandler(dh);
																		return file_part;
																	} catch (MessagingException msgEx) {
																		throw Throwables.throwUnchecked(msgEx);
																	}
																})
														   .collect(Collectors.toList());
			for (MimeBodyPart mimeBodyPart : mimeBodyParts) {
				mp.addBodyPart(mimeBodyPart);
			}
		}
		message.setContent(mp);

		// ----> Send the message
		Transport.send(message);

		// ----> Delete attached files temp files
		if (attachedFilesPaths != null && attachedFilesPaths.length > 0) _deleteTempFiles(attachedFilesPaths);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  PRIVATE METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	private static InternetAddress[] eMailCollectionToInternetAddress(final Collection<EMail> emails) {
		return CollectionUtils.hasData(emails)
					? emails.stream()
							.map(email -> {
									try {
										return new InternetAddress(email.toString());
									} catch (AddressException addrEx) {
										throw Throwables.throwUnchecked(addrEx);
									}
								 })
							.toArray(InternetAddress[]::new)
					: null;
	}
}
