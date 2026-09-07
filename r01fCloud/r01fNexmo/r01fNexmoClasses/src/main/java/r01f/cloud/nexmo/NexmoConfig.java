package r01f.cloud.nexmo;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.cloud.nexmo.NexmoAPI.NexmoAPIData;
import r01f.cloud.nexmo.NexmoAPIIDs.NexmoAPIClientID;
import r01f.cloud.nexmo.NexmoAPIIDs.NexmoApplicationtID;
import r01f.config.ContainsConfigData;
import r01f.exceptions.Throwables;
import r01f.httpclient.HttpClientProxySettings;
import r01f.httpclient.HttpProxySettingsBuilder;
import r01f.securitycontext.SecurityIDS.Password;
import r01f.types.contact.Phone;
import r01f.types.url.Url;
import r01f.util.types.Strings;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

@Slf4j
@Accessors(prefix="_")
public class NexmoConfig
  implements ContainsConfigData {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final NexmoAPIData _apiData;
	@Getter private final HttpClientProxySettings _proxySettings;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public NexmoConfig(final NexmoAPIData apiData,
					   final HttpClientProxySettings proxySettings) {
		_proxySettings = proxySettings;
		_apiData = apiData;
	}
	public static NexmoConfig createFrom(final XMLPropertiesForAppComponent xmlProps) {
		return NexmoConfig.createFrom(xmlProps,
									   "nexmo");
	}
	public static NexmoConfig createFrom(final XMLPropertiesForAppComponent xmlProps,
										  final String propsRootNode) {
		// ensure the root node
		String thePropsRootNode = Strings.isNullOrEmpty(propsRootNode) ? "nexmo" : propsRootNode;

		// Test proxy connection to see if proxy is needed
		HttpClientProxySettings proxySettings = null;
		try {
			proxySettings = HttpProxySettingsBuilder.guessProxySettings(xmlProps,
																			  thePropsRootNode);
		} catch (Throwable th) {
			log.error("Error while guessing the proxy settings to use Twilio: {}",
					  th.getMessage(),th);
		}

		// Get the twilio api info from the properties file
		NexmoAPIData apiData = NexmoConfig.apiDataFromProperties(xmlProps,
																   thePropsRootNode);

		// return the config
		return new NexmoConfig(apiData,
								proxySettings);
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	// Used for supapis.
/////////////////////////////////////////////////////////////////////////////////////////
	static NexmoAPIData apiDataFromProperties(final XMLPropertiesForAppComponent props,
											  final String propsRootNode) {
		String apiKeyAsString = props.propertyAt(propsRootNode + "/apiKey")
								        .asString();
		String apiSecretasString  = props.propertyAt(propsRootNode + "/apiSecret")
							              	 .asString();
		String priavateKeysString  = props.propertyAt(propsRootNode + "/privateKey")
				                           .asString();
		
		String applicationIdAsString  = props.propertyAt(propsRootNode + "/applicationId")
				                               .asString();
		
		
		String voicePhoneNumberAsString = props.propertyAt(propsRootNode + "/voicePhoneNumber")
											 .asString();
		String smsPhoneNumberAsString = props.propertyAt(propsRootNode + "/smsPhoneNumber")
										    	 .asString();
		
		String messagingServiceAsString = props.propertyAt(propsRootNode + "/messagingService")
                .asString();
		
		String messagePhoneNumberAsString = props.propertyAt(propsRootNode + "/messagingPhoneNumber")
		    	                                      .asString();
		
		String restResouceURIForMessagingApplicationImplAsString = props.propertyAt(propsRootNode + "/restResouceURIForMessagingApplication")
                                                                        .asString();

		// Check
		if (apiKeyAsString == null  || apiSecretasString == null ) {
			throw new IllegalStateException(Throwables.message("Cannot configure NEXMO API: the properties file does NOT contains a the apiKey / apiSecret at {} in {} properties file",
															   propsRootNode + "/nexmo/apiKey|apiSecret",props.getAppCode()));
		}
		if (Strings.isNullOrEmpty(voicePhoneNumberAsString) && Strings.isNullOrEmpty(smsPhoneNumberAsString)) {
			throw new IllegalStateException(Throwables.message("Cannot configure NEXMO API: there's neither a voice-enabled nexmo phone number nor a messaging-enabled twilio phone number configured at {} in {} properties file",
															   propsRootNode + "/nexmo/voicePhoneNumber",props.getAppCode()));
		}
		if (Strings.isNullOrEmpty(voicePhoneNumberAsString)) {
			log.warn("There's NO voice-enabled NEXMO phone number configured at {} in {} properties file: VOICE CALLS ARE NOT ENABLED!",
		
																	propsRootNode + "/nexmo/voicePhoneNumber",props.getAppCode());
		}
		
		if (Strings.isNullOrEmpty(smsPhoneNumberAsString)) {
			log.warn("There's NO messaging-enabled NEXMO phone number configured at {} in {} properties file: MESSAGING IS NOT ENABLED!",		
																		propsRootNode + "/nexmo/messagingPhoneNumber",props.getAppCode());
		}
		
		if (Strings.isNullOrEmpty(applicationIdAsString)) {
			log.warn("There's NO ApplicationID configured at {} in {} properties file: MESSAGING IS NOT ENABLED!",		
																		propsRootNode + "/nexmo/applicationId",props.getAppCode());
		}

		// Create the NEXMO NexmoAPIData :
		NexmoAPIData apiData = new NexmoAPIData(NexmoAPIClientID.of(apiKeyAsString),
				                                Strings.isNOTNullOrEmpty(apiSecretasString) ? Password.forId(apiSecretasString) :null,	
				                                 Strings.isNOTNullOrEmpty(applicationIdAsString) ?  NexmoApplicationtID.of(applicationIdAsString) :null,			
				                                Strings.isNOTNullOrEmpty(priavateKeysString) ? Password.forId(priavateKeysString) :null,				                             
												Strings.isNOTNullOrEmpty(voicePhoneNumberAsString) ? Phone.of(voicePhoneNumberAsString) : null,
											    Strings.isNOTNullOrEmpty(smsPhoneNumberAsString) ? Phone.of(smsPhoneNumberAsString) : null,
											    Strings.isNOTNullOrEmpty(messagePhoneNumberAsString) ? Phone.of(messagePhoneNumberAsString) : null,
											    Strings.isNOTNullOrEmpty(messagingServiceAsString) ? NexmoAPI.MessagingService.valueOf(messagingServiceAsString) : null,
											    Strings.isNOTNullOrEmpty(restResouceURIForMessagingApplicationImplAsString) ? Url.from(restResouceURIForMessagingApplicationImplAsString) : null);
													  
														  
		return apiData;
	}
}
