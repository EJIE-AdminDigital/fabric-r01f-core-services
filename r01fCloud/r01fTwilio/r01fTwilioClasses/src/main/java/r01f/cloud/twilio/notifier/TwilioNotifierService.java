package r01f.cloud.twilio.notifier;

import java.util.Collection;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.twilio.rest.api.v2010.account.Call;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import r01f.cloud.twilio.TwilioService;
import r01f.core.services.notifier.NotifierOIDs.NotifierTaskOID;
import r01f.core.services.notifier.NotifierServiceResponse;
import r01f.core.services.notifier.NotifierServiceResponseError;
import r01f.core.services.notifier.NotifierResponseErrorTypes;
import r01f.core.services.notifier.NotifierServiceResponseOK;
import r01f.core.services.notifier.NotifierServiceForVoicePhoneCall;
import r01f.core.services.notifier.config.NotifierEnums.NotifierType;
import r01f.patterns.Factory;
import r01f.types.contact.OwnedContactMean;
import r01f.types.contact.Phone;
import r01f.types.url.Url;

@Slf4j
@Singleton
public class TwilioNotifierService
  implements NotifierServiceForVoicePhoneCall {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final TwilioService _twilioService;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	@Inject
	public TwilioNotifierService(final TwilioService twilioService) {
		_twilioService = twilioService;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public NotifierServiceResponse<Phone> notify(final OwnedContactMean<Phone> from, final Phone to,
										  final Factory<String> messageToBeDeliveredFactory) {
		Collection<NotifierServiceResponse<Phone>> res = this.notifyAll(from,Lists.newArrayList(to),
																 messageToBeDeliveredFactory);
		return Iterables.getFirst(res,
								  null);
	}
	@Override
	public Collection<NotifierServiceResponse<Phone>> notifyAll(final OwnedContactMean<Phone> from,final Collection<Phone> to,
														 final Factory<String> messageToBeDeliveredFactory) {
		Collection<NotifierServiceResponse<Phone>> responses = Lists.newArrayListWithExpectedSize(to.size());

		// [1] - Create the message
		@SuppressWarnings("unused")
		String voiceMsg = messageToBeDeliveredFactory.create();

		// [2] - Send an email to every destination
		log.info("[{}] > Sending voice notifications to {} recipients",
				 TwilioNotifierService.class.getSimpleName(),
				 to.size());
		for (Phone phone : to) {
			try {
				Phone intPhone = Phone.of(phone.asStringEnsuringCountryCode("+34"));	// ensure the phone contains +34
				log.info("\t- making voice call to {}",intPhone);
				Url twmlUrl = Url.from("https://dl.dropboxusercontent.com/u/1264561/testTwilioTWML.xml");
				Call twCall = _twilioService.makeCall(intPhone,
													  twmlUrl);
				log.warn(twCall.getCallerName());
				responses.add(new NotifierServiceResponseOK<Phone>(NotifierTaskOID.supply(),
														    phone,
														    NotifierType.VOICE));	// success
			} catch (final Throwable restEx) {
				log.error("Error while making a voice call to {} using twilio: {}",
									  phone,
									  restEx.getMessage(),restEx);
				responses.add(new NotifierServiceResponseError<Phone>(NotifierTaskOID.supply(),
														  	   phone,
														  	   NotifierType.VOICE,
														       NotifierResponseErrorTypes.UNKNOWN,
														       restEx.getLocalizedMessage()));	// failed, if want some more specific error try to catch another exceps.
			}
		}
	    // [3] - Build the response
		return responses;
	}
}
