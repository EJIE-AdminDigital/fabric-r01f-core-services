package r01f.core.services.mail;

import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.lang.Nullable;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import jakarta.mail.Message.RecipientType;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.Cleanup;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import r01f.httpclient.HttpClientProxySettings;
import r01f.httpclient.HttpRequestBodyPublisherForURLEncodedForm;
import r01f.httpclient.HttpRequestFormParameter;
import r01f.httpclient.HttpRequestFormParameterForText;
import r01f.httpclient.HttpResponseCode;
import r01f.httpclient.HttpResponseResult;
import r01f.httpclient.HttpSSLContextBuilder;
import r01f.internal.Env;
import r01f.io.IOStreams;
import r01f.mime.MimeTypes;
import r01f.types.contact.EMail;
import r01f.types.url.Url;
import r01f.types.url.UrlQueryString;
import r01f.types.url.UrlQueryStringParam;
import r01f.util.types.Strings;

@Slf4j
public class JavaMailSenderRESTServiceImpl
     extends JavaMailSenderBase {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final Url _restServiceEndPointUrl;
	@Getter private final HttpClientProxySettings _proxySettings;
	@Getter private final boolean _supportsMimeMessage;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public JavaMailSenderRESTServiceImpl(final Url restServiceEndpointUrl ,
									     final HttpClientProxySettings proxySettings) {
		_restServiceEndPointUrl = restServiceEndpointUrl;
		_proxySettings = proxySettings;
		_supportsMimeMessage = false;
	}
	public JavaMailSenderRESTServiceImpl(final Url restServiceEndpointUrl ,
										 final HttpClientProxySettings proxySettings,
										 final boolean supportsMimeMessage) {
		_restServiceEndPointUrl = restServiceEndpointUrl;
		_proxySettings = proxySettings;
		_supportsMimeMessage = supportsMimeMessage;
	}
	public static JavaMailSender create(final Url restServiceEndPointUrl,
										final HttpClientProxySettings proxySettings) {
		if (Strings.isNullOrEmpty(restServiceEndPointUrl.asString())) throw new IllegalArgumentException("Invalid URL for Third Party Mail Sender");
		JavaMailSender outJavaMailSender =  new JavaMailSenderRESTServiceImpl(restServiceEndPointUrl,
																			  proxySettings);
		return outJavaMailSender;
	}
	public static JavaMailSender create(final Url restServiceEndPointUrl) {
		return JavaMailSenderRESTServiceImpl.create(restServiceEndPointUrl,
					  								null);	// no proxy
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	SIMPLE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void send(final SimpleMailMessage... simpleMessages) throws MailException {
		for (SimpleMailMessage simpleMessage : simpleMessages) {
			EMail from = EMail.of(simpleMessage.getFrom());
			EMail[] to = Stream.of(simpleMessage.getTo())
							   .map(EMail.FROM_STRING_TRANSFORM)
							   .toArray(EMail[]::new);
			String text = simpleMessage.getText();
			String subject = simpleMessage.getSubject();
			_doSend(from,to,
					subject,text);
		}
	}
	private void _doSend(final EMail from,final EMail[] to,
						 final String subject,final String text) {

		List<HttpRequestFormParameter> parameters = new ArrayList<>();
		if (from != null) parameters.add(HttpRequestFormParameterForText.of(from.asString())
														  				.withName("from"));
		parameters.add(HttpRequestFormParameterForText.of(to[0].asString())
													  .withName("to"));
		parameters.add(HttpRequestFormParameterForText.of(subject)
													  .withName("subject"));
		parameters.add(HttpRequestFormParameterForText.of(text)
													  .withName("messageText"));
		HttpResponseResult responseResult = null;
		try {
			log.warn("[ * JavaMailSender (rest service)]: URL {}",
					  _restServiceEndPointUrl);
			HttpRequest request = HttpRequest.newBuilder()
											  .uri(_restServiceEndPointUrl.asUri())
											  .header("Content-Type", MimeTypes.FORM_URL_ENCODED.getName())
											  .POST(new HttpRequestBodyPublisherForURLEncodedForm()
													  .withPOSTFormParameters(parameters).build())
											  .build();
			HttpResponse<InputStream> response =  _httpClient()
														   .send(request,
																 HttpResponse.BodyHandlers.ofInputStream());
			responseResult =  new HttpResponseResult(response);
			
			if (!responseResult.getCode().isIn(HttpResponseCode.OK)) {
				log.warn("[JavaMailSender (rest service)]: HTTP mail service response Code : {} > {}",
						 responseResult.getCode(),responseResult.loadAsString());
				throw new JavaMailSenderRESTServiceImplException(Strings.customized("[JavaMailSender (rest service)]: Remote Server Error: {}",
																					   responseResult.loadAsString()));
			}
		} catch (final Throwable e) {
			log.error("[JavaMailSender (rest service)]: Error > {}",
					  e.getMessage(),e);
			throw new JavaMailSenderRESTServiceImplException(e.getLocalizedMessage());
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	MIME
/////////////////////////////////////////////////////////////////////////////////////////

	@Override
	protected void doSend(			final MimeMessage[] mimeMessages,
						  @Nullable final Object[] originalMessages) throws MailException {
		for (MimeMessage mimeMessage : mimeMessages) {
			try {
				_doSendMimeMessage(mimeMessage);
			} catch (final Throwable msgEx) {
				log.error("Error while sending mail message: {}",
					      msgEx.getMessage(),msgEx);
				throw new MailException( msgEx.getMessage(),msgEx) {
								private static final long serialVersionUID = 1131114138686813364L;
				};
			}
		}
	}
	@SuppressWarnings("resource")
	private void _doSendMimeMessage(final MimeMessage mimeMessage) throws IOException,
																		  MessagingException {
		EMail[] from = Stream.of(mimeMessage.getFrom())
						     .map(addr -> EMail.of(addr.toString()))
						     .toArray(EMail[]::new);

	    EMail[] to = Stream.of(mimeMessage.getRecipients(RecipientType.TO))
						   .map(addr -> EMail.of(addr.toString()))
						   .toArray(EMail[]::new);
		String subject = mimeMessage.getSubject();

		log.warn("[JavaMailSender (rest service {} )]: MIME MESSAGE SUPPORTED {}",
							_restServiceEndPointUrl,_supportsMimeMessage);
		try {
			if (_supportsMimeMessage) {
					@Cleanup InputStream is = mimeMessage.getInputStream();

					Url url = _restServiceEndPointUrl.joinWith(UrlQueryString.fromParams(UrlQueryStringParam.of("to",to[0]),
																						 UrlQueryStringParam.of("from",from[0]),
																						 UrlQueryStringParam.of("subject",subject)));
					HttpRequest request = HttpRequest.newBuilder()
														  .uri(url.asUri())
														  .POST(HttpRequest.BodyPublishers.ofByteArray(IOStreams.inputStreamBytes(is)))
													 .build();

					HttpResponse<InputStream> responseht = HttpClient.newBuilder()
															   .build()
															   .send(request,
										                             HttpResponse.BodyHandlers.ofInputStream());

					HttpResponseResult responseResult = new HttpResponseResult(responseht);

					if (!responseResult.getCode().isIn(HttpResponseCode.OK)) {
						log.error("[JavaMailSender (rest service)] > ERROR Response Code {}",
								  responseResult.getCode(),responseResult.loadAsString() );
						throw new JavaMailSenderRESTServiceImplException(Strings.customized("Remote Server Error at rest Mail Service {}",
																							   responseResult.loadAsString()));

				}
			} else {
				log.warn("[JavaMailSender (rest service)]: Mime Message Not Supported");
				log.warn("[JavaMailSender (rest service)]:  From {}  to {} " , from[0],to);
				_doSend(from[0], to, subject,
						_getMimeMessageAsPlainText(mimeMessage));
			}
		} catch (final Throwable e) {
			throw new JavaMailSenderRESTServiceImplException(e.getLocalizedMessage());
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	INNER CLASSES
/////////////////////////////////////////////////////////////////////////////////////////	
	public class JavaMailSenderRESTServiceImplException
		 extends MailException {
		private static final long serialVersionUID = -8313498571229772866L;
		public JavaMailSenderRESTServiceImplException(final String msg) {
			super(msg);
		}
	}
	
/////////////////////////////////////////////////////////////////////////////////////////
//HTTP CLIENT
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("static-method")
	private  HttpClient _httpClient() {
		Env env = Env.guessFromSystemProps();
		if (env.isNOT(Env.LOC)) {
				return HttpClient.newBuilder()
				.build();
		}
		try {
		log.warn(" >>> [Warn! ] Build ssl context for untrusted cets. Just for local environment  ");;
		return HttpClient.newBuilder()
						.sslContext(HttpSSLContextBuilder.buildSSlContextForUnstrustedCerts())
						.build();
			} catch (final Throwable e) {
				throw new IllegalStateException(e.getLocalizedMessage());
		}
}
}
