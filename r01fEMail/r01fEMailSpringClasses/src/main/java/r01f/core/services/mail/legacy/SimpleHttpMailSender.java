package r01f.core.services.mail.legacy;

import java.io.InputStream;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.AddressException;

import r01f.httpclient.HttpClientProxySettings;
import r01f.httpclient.HttpRequestBodyPublisherForMultipartForm;
import r01f.httpclient.HttpRequestBodyPublisherForURLEncodedForm;
import r01f.httpclient.HttpRequestFormParameter;
import r01f.httpclient.HttpRequestFormParameterForMultiPartBinaryData;
import r01f.httpclient.HttpRequestFormParameterForText;
import r01f.httpclient.HttpRequestPayloadForFileParameter;
import r01f.httpclient.HttpResponseResult;
import r01f.mime.MimeType;
import r01f.types.Path;
import r01f.types.contact.EMail;
import r01f.types.url.Host;
import r01f.util.types.collections.CollectionUtils;
import r01f.util.types.collections.Lists;

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
public class SimpleHttpMailSender
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
	@SuppressWarnings("unused")
	private final int _timeout;
	/**
	 * Debug enabled?
	 */
	@SuppressWarnings("unused")
	private final boolean _debug;
	/**
	 * Htpp proxy settings
	 */
	@SuppressWarnings("unused")
	private final HttpClientProxySettings _proxySettings;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public SimpleHttpMailSender(final Host smtpHost,
								final boolean debug) {
		_smtpHost = smtpHost;
		_timeout = -1;
		_debug = debug;
		_proxySettings = null;
	}
	public SimpleHttpMailSender(final Host smtpHost,final int timeout,
								final boolean debug) {
		_smtpHost = smtpHost;
		_timeout = timeout;
		_debug = debug;
		_proxySettings = null;
	}
	public SimpleHttpMailSender(final Host smtpHost,final int timeout,
								final boolean debug,
								final HttpClientProxySettings proxySettings) {
		_smtpHost = smtpHost;
		_timeout = timeout;
		_debug = debug;
		_proxySettings = proxySettings;
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
							final MimeType mimeType,final String messageText,
							final Path[] attachedFilesPaths) throws AddressException,
																 	MessagingException {
		// Checks
		if (_smtpHost == null) throw new MessagingException("The SMTP host cannot be null");
		if (CollectionUtils.isNullOrEmpty(to)) throw new MessagingException("Cannot send a mail message to an unknown destination email address");

		// Compose the post-ed parameters
		HttpRequestFormParameterForText fromParam = HttpRequestFormParameterForText.of(from.asString())
																				   .withName("from");
		HttpRequestFormParameterForText toParam = HttpRequestFormParameterForText.of(CollectionUtils.toStringSeparatedWith(to,';'))
																				 .withName("to");
		HttpRequestFormParameterForText toCCParam = CollectionUtils.hasData(toCC)
															? HttpRequestFormParameterForText.of(CollectionUtils.toStringSeparatedWith(toCC,';'))
																							 .withName("toCC")
															: null;
		HttpRequestFormParameterForText toCCOParam = CollectionUtils.hasData(toCCO)
															? HttpRequestFormParameterForText.of(CollectionUtils.toStringSeparatedWith(toCCO,';'))
																							 .withName("toCCO")
															: null;
		HttpRequestFormParameterForText subjParam = HttpRequestFormParameterForText.of(subject)
																				   .withName("subject");
		HttpRequestFormParameterForText mimeParam = HttpRequestFormParameterForText.of(mimeType.asString())
																				   .withName("messageContentType");
		HttpRequestFormParameterForText textParam = HttpRequestFormParameterForText.of(messageText)
																				   .withName("messageText");

		List<HttpRequestFormParameter> params = Lists.newArrayList();
		params.add(fromParam);
		params.add(toParam);
		if (toCCParam != null) params.add(toCCParam);
		if (toCCOParam != null) params.add(toCCOParam);
		params.add(subjParam);
		params.add(mimeParam);
		params.add(textParam);

		// Add Attachments
		if (CollectionUtils.hasData(attachedFilesPaths)) {
			List<HttpRequestPayloadForFileParameter> fileParams = Stream.of(attachedFilesPaths)
																		.map(attachmentFilePath -> HttpRequestPayloadForFileParameter.wrap(attachmentFilePath.asAbsoluteString())
																												 					 .withFileName(attachmentFilePath.getFileName()))
																		.collect(Collectors.toList());
			HttpRequestFormParameterForMultiPartBinaryData multiPartBinaryData = HttpRequestFormParameterForMultiPartBinaryData.of(fileParams)
																															   .withName("files");
			params.add(multiPartBinaryData);
			params.add(HttpRequestFormParameterForText.of(fileParams.size())
													  .withName("numFiles"));
			// http post multipart
			try {
			    HttpRequest request = HttpRequest.newBuilder()
												  .uri(_smtpHost.asUrl().asUri())
												  .header("Content-Type", HttpRequestBodyPublisherForMultipartForm.payloadContentType().asString())
												  .POST(new HttpRequestBodyPublisherForMultipartForm()
														  		.withPOSTFormParameters(params).build())
												 .build();
				HttpResponse<InputStream> response = HttpClient.newBuilder()
															   .build()
															   .send(request,
										                             HttpResponse.BodyHandlers.ofInputStream());

				String postResponse = new HttpResponseResult(response).loadAsString();
			} catch (final Throwable ioEx) {
				ioEx.printStackTrace();
				throw new MessagingException("http client error: " +
											 ioEx.getLocalizedMessage());
			}
		}
		// no attachments
		else {
			params.add(HttpRequestFormParameterForText.of(0)
													  .withName("numFiles"));
			try {
			    HttpRequest request = HttpRequest.newBuilder()
													  .uri(_smtpHost.asUrl().asUri())
													  .header("Content-Type", mimeType.getName())
													  .POST(new HttpRequestBodyPublisherForURLEncodedForm()
															  		.withPOSTFormParameters(params).build())
												 .build();
				HttpResponse<InputStream> response = HttpClient.newBuilder()
																   .build()
																   .send(request,
											                             HttpResponse.BodyHandlers.ofInputStream());
				String postResponse = new HttpResponseResult(response).loadAsString();

			} catch (final Throwable ioEx) {
				ioEx.printStackTrace();
				throw new MessagingException("http client error: " +
											 ioEx.getLocalizedMessage());
			}
		}
	 }
}
