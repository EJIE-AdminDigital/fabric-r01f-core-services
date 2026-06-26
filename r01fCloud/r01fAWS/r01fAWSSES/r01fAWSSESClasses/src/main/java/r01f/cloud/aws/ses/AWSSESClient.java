package r01f.cloud.aws.ses;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import r01f.cloud.aws.AWSAccessKey;
import r01f.cloud.aws.AWSAccessSecret;
import r01f.core.services.mail.EMailMimeMessages;
import r01f.core.services.mail.model.EMailDestinations;
import r01f.core.services.mail.model.EMailMessage;
import r01f.core.services.mail.model.EMailMessageAttachment;
import r01f.core.services.mail.model.EMailRFC822Address;
import r01f.util.types.collections.CollectionUtils;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.Body;
import software.amazon.awssdk.services.ses.model.Content;
import software.amazon.awssdk.services.ses.model.Destination;
import software.amazon.awssdk.services.ses.model.Message;
import software.amazon.awssdk.services.ses.model.RawMessage;
import software.amazon.awssdk.services.ses.model.SendEmailRequest;
import software.amazon.awssdk.services.ses.model.SendEmailResponse;
import software.amazon.awssdk.services.ses.model.SendRawEmailRequest;
import software.amazon.awssdk.services.ses.model.SesResponse;

@Slf4j
public class AWSSESClient {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final SesClient _sesClient;
	private final Charset _charset;

/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AWSSESClient(final AWSSESClientConfig config) {
		_sesClient = SesClient.builder()
						 .region(config.getRegion())
						 // Credentials: https://docs.aws.amazon.com/sdk-for-java/v2/developer-guide/credentials.html
						 .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(config.getAccessKey().asString(),
								 																		  config.getAccessSecret().asString())))
						 .build();
		_charset = config.getCharset();
	}
	AWSSESClient(final Region region,	// ie: Region.EU_WEST_1
				 final AWSAccessKey accessKey,final AWSAccessSecret accessSecret,
				 final Charset charset) {
		this(new AWSSESClientConfig(region,
									accessKey,accessSecret,
									charset));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public SesResponse sendEMail(final EMailMessage mailMsg,
								 final EMailMessageAttachment... attachs) throws MessagingException {
		return this.sendEMail(mailMsg,
							  CollectionUtils.hasData(attachs) ? Lists.newArrayList(attachs) : null);
	}
	public SesResponse sendEMail(final EMailMessage mailMsg,
								 final Collection<EMailMessageAttachment> attachs) throws MessagingException {
		if (CollectionUtils.isNullOrEmpty(attachs)) {
			log.info("[AWS SES]: Sending simple email");
			return _sendSimpleEmail(mailMsg);
		}
		log.info("[AWS SES]: Sending email with attachments");
		return _sendRawEMail(mailMsg,
							 attachs);
	}
	public SesResponse sendEMail(final EMailRFC822Address from,final EMailDestinations destinations,
								 final MimeMessage mimeMessage) throws MessagingException {
		log.info("[AWS SES]: Sending raw email");
		return _sendRawEMail(from,destinations,
							 mimeMessage);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	SIMPLE
/////////////////////////////////////////////////////////////////////////////////////////
	private SendEmailResponse _sendSimpleEmail(final EMailMessage mailMsg) {
		SendEmailRequest req = _sendEMailRequestFrom(mailMsg);
		return _sesClient.sendEmail(req);
	}
	private SendEmailRequest _sendEMailRequestFrom(final EMailMessage mailMsg) {
		return SendEmailRequest.builder()
							   .destination(_eMailDestinationFrom(mailMsg.getTo(),
									   							  mailMsg.getCc(),
									   							  mailMsg.getBcc()))
							   .message(Message.builder()
									   		   .subject(_eMailSubjectFrom(mailMsg.getSubject()))
									   		   .body(_eMailBodyFrom(mailMsg.getPlainTextBody(),
									   				   		   	    mailMsg.getHtmlBody()))
									   		   .build())
							   .source(mailMsg.getFrom().asRFC822Address())
							   .replyToAddresses(Lists.newArrayList(mailMsg.getFrom().getEmail().asString()))
							   .build();
	}
	private Content _eMailSubjectFrom(final String subject) {

		return Content.builder()
					  .charset(_charset != null ? _charset.name() : Charset.defaultCharset().name())
					  .data(subject)
					  .build();
	}
	private Body _eMailBodyFrom(final String body,
								final String htmlBody) {
		
		Content contentBody = Content.builder()
									 .charset(_charset != null ? _charset.name() : Charset.defaultCharset().name())
									 .data(body)
									 .build();
		return htmlBody != null ? Body.builder()
									  .html(contentBody)
									  .build()
								: Body.builder()
									  .text(contentBody)
									  .build();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	RAW (MIME)
/////////////////////////////////////////////////////////////////////////////////////////
	private SesResponse _sendRawEMail(final EMailMessage mailMsg,
									  final Collection<EMailMessageAttachment> attachs) throws MessagingException {
		// Create the MimeMessage
		MimeMessage mimeMessage = EMailMimeMessages.createMimeMessageFor(mailMsg)
													   .withCharset(_charset)
													   .usingDefaultSession()
													   .withAttachments(attachs)
													 .build();

        // Send the email.
        return _sendRawEMail(mailMsg.getFrom(),mailMsg.getDestinations(),
        			  		 mimeMessage);
	}
	private SesResponse _sendRawEMail(final EMailRFC822Address from,final EMailDestinations dest,
									  final MimeMessage mimeMessage) throws MessagingException {
        // Send the email.
        try {
	        SdkBytes bytes = SdkBytes.fromInputStream(mimeMessage.getInputStream());
	        RawMessage rawMessage = RawMessage.builder()
	        								  .data(bytes)
	        								  .build();
	        Destination destinations = _eMailDestinationFrom(dest.getTo(),
	        												 dest.getCc(),
	        											     dest.getBcc());
	        SendRawEmailRequest rawEmailRequest = SendRawEmailRequest.builder()
	        														 .destinations(destinations.toAddresses())
																	 .source(from.asRFC822Address())
	        														 .rawMessage(rawMessage)
	        														 .build();

	        return _sesClient.sendRawEmail(rawEmailRequest);
        } catch (IOException ioEx) {
        	throw new MessagingException(ioEx.getMessage(),
        								 ioEx);
        }
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unused")
	private static Destination _eMailDestinationFrom(final Collection<EMailRFC822Address> to,
											  		 final Collection<EMailRFC822Address> cc,
											  		 final Collection<EMailRFC822Address> bcc) {
		return Destination.builder()
				   .toAddresses(to.stream()
						   		  .map(addr -> addr.asRFC822Address())
						   		  .collect(Collectors.toList()))
				   .ccAddresses(CollectionUtils.hasData(cc)
						   			? cc.stream()
						   				.map(addr -> addr.asRFC822Address())
				   						.collect(Collectors.toList())
				   					: Lists.newArrayList())
				   .bccAddresses(CollectionUtils.hasData(cc)
						   			? cc.stream()
						   				.map(addr -> addr.asRFC822Address())
				   						.collect(Collectors.toList())
				   					: Lists.newArrayList())
				   .build();
	}
}
