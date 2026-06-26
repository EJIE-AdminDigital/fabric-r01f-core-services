package r01f.cloud.nexmo;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import com.nexmo.client.NexmoClient;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.cloud.nexmo.api.interfaces.NexmoServicesForMessagingApplication;
import r01f.cloud.nexmo.api.interfaces.NexmoServicesForSMS;
import r01f.cloud.nexmo.api.interfaces.NexmoServicesForVoice;
import r01f.cloud.nexmo.api.interfaces.impl.NexmoServicesForMessagingApplicationImpl;
import r01f.cloud.nexmo.api.interfaces.impl.NexmoServicesForSMSImpl;
import r01f.cloud.nexmo.api.interfaces.impl.NexmoServicesForVoiceImpl;
import r01f.guids.OIDBaseImmutable;
import r01f.model.annotations.ModelObjectsMarshaller;
import r01f.objectstreamer.Marshaller;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.securitycontext.SecurityIDS.Password;
import r01f.types.contact.Phone;
import r01f.types.url.Url;

/**
 * Encapsulates nexmo message & call sending
 * Sample usage:
 * <pre class='brush:java'>

 */
@Singleton
@Accessors(prefix="_")
public class NexmoAPI  {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter final NexmoAPIData _apiData;
	@Getter private NexmoServicesForSMS _forSMS;
	@Getter private NexmoServicesForVoice _forVoice;
	@Getter private NexmoServicesForMessagingApplication _forMessageApplication;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	@Inject
	public NexmoAPI(						final NexmoConfig config,
			        @ModelObjectsMarshaller final Marshaller marshaller) {
		this(config.getApiData(),marshaller);
	}

	public NexmoAPI(final NexmoAPIData apiData,
			        final Marshaller marshaller) {
		// Nexmo API Data.
		_apiData = apiData;
		// Nexmo Client
		 NexmoClient  nexmoClient =  _createNexmoRESTClient(apiData) ;
		//... the subapis.
		_forSMS = new NexmoServicesForSMSImpl(apiData,nexmoClient,marshaller);
		_forVoice = new  NexmoServicesForVoiceImpl(apiData,nexmoClient,marshaller);
		_forMessageApplication = new NexmoServicesForMessagingApplicationImpl(apiData,nexmoClient,marshaller);

	}
/////////////////////////////////////////////////////////////////////////////////////////
//  API DATA
/////////////////////////////////////////////////////////////////////////////////////////
	@Accessors(prefix="_")
	@RequiredArgsConstructor @AllArgsConstructor
	public static class NexmoAPIData {
		// Client API

		@Getter private final NexmoAPIClientID _apiKey;
		@Getter private final Password _apiSecret;
		@Getter private final NexmoApplicationtID _applicationId;
		@Getter private final Password _privateKey;
		// Used for supapis.
		@Getter private Phone _voicePhone;			// (a nexmo number) +34518880365
		@Getter private Phone _smsPhone;	    	// (a nexmo number) +34518880365
		@Getter private Phone _messagingPhone;		// (a nexmo number) +34518880365
		@Getter private MessagingService _messagingService;
		@Getter private Url   _restResouceURIForMessagingApplicationImpl;


		public boolean existsAccountData() {
			return _apiKey != null && _apiSecret != null;
		}
		public boolean canMakeVoicePhoneCalls() {
			return _voicePhone != null;
		}
		public boolean canSendMessages() {
			return _smsPhone != null;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallType(as="apiClientID")
	public static class NexmoAPIClientID
				extends OIDBaseImmutable<String> {
		private static final long serialVersionUID = -5867457273405673410L;
		private NexmoAPIClientID(final String id) {
			super(id);
		}
		public static NexmoAPIClientID of(final String id) {
			return new NexmoAPIClientID(id);
		}
	}
	@MarshallType(as="applicationID")
	public static class NexmoApplicationtID
				extends OIDBaseImmutable<String> {
		private static final long serialVersionUID = -5867457273405673410L;
		private NexmoApplicationtID(final String id) {
			super(id);
		}
		public static NexmoApplicationtID of(final String id) {
			return new NexmoApplicationtID(id);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Accessors(prefix="_")
	@RequiredArgsConstructor
	public static enum MessagingService {
		whatsapp,  // lowcase must be
		messenger;
	/////////////////////////////////////////////////////////////////////////////////////////
    //		FROM OID & ID TYPES
	/////////////////////////////////////////////////////////////////////////////////////////

	}

/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////

	private NexmoClient  _createNexmoRESTClient(final NexmoAPIData apiData) {
		NexmoClient  outClient =
		                         NexmoClient.builder()
		                             .applicationId(apiData.getApplicationId().asString())
		                             .apiKey(apiData.getApiKey().asString())
		                             .apiSecret(apiData.getApiSecret().asString())
		                             .privateKeyContents(apiData.getPrivateKey().asString())
		                             .build();

		return outClient;
	}


}
