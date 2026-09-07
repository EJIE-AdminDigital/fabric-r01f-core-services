package r01f.cloud.twilio;

import com.google.common.base.Preconditions;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Call;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import jakarta.inject.Singleton;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.guids.OIDTyped;
import r01f.httpclient.HttpClientProxySettings;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.securitycontext.SecurityIDS.Password;
import r01f.service.ServiceCanBeDisabled;
import r01f.types.contact.Phone;
import r01f.types.url.Url;

/**
 * Encapsulates twilio message & call sending
 * Sample usage:
 * <pre class='brush:java'>
 *		TwilioService twilioService = new TwilioService(new TwilioAPIData(TwilioAPIClientID.of("xxx"),Password.forId("yyy"),
 *																		  Phone.of("+34510000341"),
 *																		  Phone.of("+34510000341")));	// same twilio number for sms and voice
 *		Call call = twilioService.makeCall(Phone.of("+34688671967"),
 *										   SerializedURL.of("http://demo.twilio.com/docs/voice.xml"));
 *		Message sms = twilioService.sendSMS(Phone.of("+34688671967"),
 *											"Hello world");
 * </pre>
 */
@Singleton
@Slf4j
public class TwilioService
  implements ServiceCanBeDisabled {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final TwilioAPIData _apiData;
	@SuppressWarnings("unused")
	private final HttpClientProxySettings _proxySettings;	// TODO enable twilio with proxy
	private boolean _disabled;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public TwilioService(final TwilioConfig config) {
		this(config.getApiData(),
			 config.getProxySettings());
	}
	public TwilioService(final TwilioAPIData apiData) {
		this(apiData,
			 null);	// proxy settings
	}
	public TwilioService(final TwilioAPIData apiData,
						 final HttpClientProxySettings proxySettings) {
		_apiData = apiData;
		_proxySettings = proxySettings;
	    _initializeTwilio();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  ServiceCanBeDisabled
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean isEnabled() {
		return !_disabled;
	}
	@Override
	public boolean isDisabled() {
		return _disabled;
	}
	@Override
	public void setEnabled() {
		_disabled = false;
	}
	@Override
	public void setDisabled() {
		_disabled = true;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	TWILIO
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Makes a twilio outgoing phone call
	 * @param toPhone the target phone number
	 * @param twmlUrl the twml to execute (ie "http://demo.twilio.com/docs/voice.xml")
	 * @return
	 */
	public Call makeCall(final Phone toPhone,
						 final Url twmlUrl) {
		Preconditions.checkArgument(toPhone != null,"The destination phone must NOT be null!");
		Preconditions.checkArgument(twmlUrl != null,"A twml URL is needed!");
		Preconditions.checkState(_apiData.existsAccountData() && _apiData.canMakeVoicePhoneCalls(),"The API is NOT configured properly to make phone calls");
	    Call call;
		try {
			call = Call.creator(  new PhoneNumber(toPhone.asString()),
						                new PhoneNumber(_apiData.getVoicePhone().asString()),
						                twmlUrl.asUri())
			                  .create();
		} catch (final Throwable e) {
			throw new IllegalStateException(e);

		}
        log.info("Call stablished with id={}",call.getSid());
        return call;
	}
	/**
	 * Sends a twilio SMS
	 * @param toPhone
	 * @param text
	 * @return
	 * @throws TwilioRestException
	 */
	public Message sendSMS(final Phone toPhone,
						   final String text) {
		Preconditions.checkArgument(toPhone != null,"The destination phone must NOT be null!");
		Preconditions.checkArgument(text != null,"A text is needed for the sms message!");
		Preconditions.checkState(_apiData.existsAccountData() && _apiData.canSendMessages(),"The API is NOT configured properly to send messages");

		Message message = Message.creator(new PhoneNumber(toPhone.asString()),
                                          new PhoneNumber(_apiData.getMessagingPhone().asString()), text).create();
		log.info("SMS Message sent with id={}",message.getSid());
		return message;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  API DATA
/////////////////////////////////////////////////////////////////////////////////////////
	@Accessors(prefix="_")
	@RequiredArgsConstructor @AllArgsConstructor
	public static class TwilioAPIData {
		@Getter private final TwilioAPIClientID _accountSID;
		@Getter private final Password _accountToken;
		@Getter private Phone _voicePhone;			// (a twilio number) +34518880365
		@Getter private Phone _messagingPhone;		// (a twilio number) +34518880365

		public boolean existsAccountData() {
			return _accountSID != null && _accountToken != null;
		}
		public boolean canMakeVoicePhoneCalls() {
			return _voicePhone != null;
		}
		public boolean canSendMessages() {
			return _messagingPhone != null;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallType(as="twilioAPIClientID")
	public record TwilioAPIClientID(@Getter String id)
	   implements OIDTyped<String> {
		public static TwilioAPIClientID from(final String id) {
			return new TwilioAPIClientID(id);
		}
		public static TwilioAPIClientID forId(final String id) {
			return new TwilioAPIClientID(id);
		}
		public static TwilioAPIClientID valueOf(final String id) {
			return new TwilioAPIClientID(id);
		}
		public static TwilioAPIClientID fromString(final String id) {
			return new TwilioAPIClientID(id);
		}
		public static TwilioAPIClientID of(final String id) {
			return new TwilioAPIClientID(id);
		}
		
		@Override
		public String asString() {
			return this.id;
		}
		@Override
		public String toString() {
			return this.id;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings({ "resource"})
	private void _initializeTwilio() {
		 Twilio.init(_apiData.getAccountSID().asString(),
				     _apiData.getAccountToken().asString());

	}
}
