package r01f.core.services.mail.config;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.core.services.mail.JavaMailSenderProperties.RESTProperties;
import r01f.exceptions.Throwables;
import r01f.httpclient.HttpClientProxySettings;
import r01f.httpclient.HttpProxySettingsBuilder;
import r01f.types.url.Url;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

@Slf4j
@Accessors(prefix="_")
public class JavaMailSenderConfigForRESTService
	 extends JavaMailSenderConfigBase {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final Url _restServiceEndPointUrl;
	@Getter private final HttpClientProxySettings _proxySettings;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public JavaMailSenderConfigForRESTService(final Url restServiceEndpointUrl,
											  final HttpClientProxySettings proxySettings,
											  final boolean disabled) {
		super(JavaMailSenderImpl.REST_SERVICE,
			  disabled);
		_restServiceEndPointUrl = restServiceEndpointUrl;
		_proxySettings = proxySettings;
	}
	public static JavaMailSenderConfigForRESTService createFrom(final XMLPropertiesForAppComponent xmlProps,
												      		  	final String propsRootNode) {
		Url thirdPartyProviderUrl = JavaMailSenderConfigForRESTService.thirdPartyMailServiceFromProperties(xmlProps,
																										   propsRootNode);
		// check if a proxy is needed
		boolean disableMailSender = false;
		HttpClientProxySettings proxySettings = null;
		try {
			proxySettings = HttpProxySettingsBuilder.guessProxySettings(xmlProps,
																			  propsRootNode);
		} catch (final Throwable th) {
			log.error("Error while guessing the internet connection proxy settings to use GMail: {}",th.getMessage(),th);
			disableMailSender = true;	// the mail sender cannot be used
		}

		return new JavaMailSenderConfigForRESTService(thirdPartyProviderUrl,
													  proxySettings,
													  disableMailSender);
	}
	
	public static JavaMailSenderConfigForRESTService createFrom(final RESTProperties properties) {		
		// check if a proxy is needed
		boolean disableMailSender = false;
		HttpClientProxySettings proxySettings = null;
		try {
			//proxySettings = HttpProxySettingsBuilder.guessProxySettings();
		} catch (final Throwable th) {
			log.error("Error while guessing the internet connection proxy settings to use GMail: {}",th.getMessage(),th);
			disableMailSender = true;	// the mail sender cannot be used
		}
		
		Url thirdPartyProviderUrl = properties.endpoint();
		return new JavaMailSenderConfigForRESTService(thirdPartyProviderUrl,
													  proxySettings,
													  disableMailSender);
	}

/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	private static final String THIRD_PARTY_MAIL_HTTPSERVICE_PROPS_XPATH = "/restService";
	static Url  thirdPartyMailServiceFromProperties(final XMLPropertiesForAppComponent props,
													final String propsRootNode) {
		Url url = props.propertyAt(propsRootNode + THIRD_PARTY_MAIL_HTTPSERVICE_PROPS_XPATH + "/url").asUrl();
		if (url == null) throw new IllegalStateException(Throwables.message("Cannot load email rest service config: the properties file does NOT contains a the url at {} in {}.{} properties file",
																			propsRootNode + THIRD_PARTY_MAIL_HTTPSERVICE_PROPS_XPATH,
																			props.getAppCode(),props.getAppComponent()));
		return url;
	}
}
