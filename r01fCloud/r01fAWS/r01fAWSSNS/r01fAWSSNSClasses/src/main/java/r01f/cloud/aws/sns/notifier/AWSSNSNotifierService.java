package r01f.cloud.aws.sns.notifier;

import java.util.Collection;



import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import r01f.cloud.aws.sns.AWSSNSClient;
import r01f.cloud.aws.sns.model.AWSSNSSmsMessageAttributesBuilder;
import r01f.cloud.aws.sns.model.AWSSNSSmsSenderID;
import r01f.cloud.aws.sns.model.AWSSNSSmsType;
import r01f.core.services.notifier.NotifierOIDs.NotifierTaskOID;
import r01f.core.services.notifier.NotifierServiceResponse;
import r01f.core.services.notifier.NotifierServiceResponseError;
import r01f.core.services.notifier.NotifierResponseErrorTypes;
import r01f.core.services.notifier.NotifierServiceResponseOK;
import r01f.core.services.notifier.NotifierServiceForSMS;
import r01f.core.services.notifier.config.NotifierEnums.NotifierType;
import r01f.patterns.Factory;
import r01f.types.contact.OwnedContactMean;
import r01f.types.contact.Phone;
import software.amazon.awssdk.services.sns.model.PublishResponse;

/**
 * SMS notifier using AMAZON AWS SNS (simple notification services)
 * Usage:
 * <pre class='brush:java'>
 * 		// create an AWS SNS client
 *		AWSSNSClient snsCli = AWSSNSClientBuilder.region(Region.EU_WEST_1)
 *									.using(AWSAccessKey.forId("__theKey__"),
 *										   AWSAccessSecret.forId("__theSecret__"))
 *									.build();
 *		// [2] - Create the notifier
 *		AWSSNSNotifierServices snsnotifier = new AWSSNSNotifierServices(snsCli);
 * </pre>
 */
@Slf4j
public class AWSSNSNotifierService
  implements NotifierServiceForSMS {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final AWSSNSClient _awsSNSClient;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	//@Inject
	public AWSSNSNotifierService(final AWSSNSClient awsSNSClient) {
		_awsSNSClient = awsSNSClient;
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

		AWSSNSSmsSenderID senderId = AWSSNSSmsSenderID.forId(from.getOwner() != null ? from.getOwner()
																					 : "R01");

		// [1] - Create the message
		String smsMsg = messageToBeDeliveredFactory.create();

		// [2] - Send an email to every destination
		log.info("[{}] > Sending SMS notifications to {} recipients",
				 AWSSNSNotifierService.class.getSimpleName(),
				 to.size());
		for (Phone phone : to) {
			try {
				log.info("\t- sms notification to {}",phone);
				PublishResponse res = _awsSNSClient.sendSMS(phone,
													  		smsMsg,
													  		AWSSNSSmsMessageAttributesBuilder.forSender(senderId)
															 								 .noMaxPrice()
															 								 .usingSmsOfType(AWSSNSSmsType.TRANSACTIONAL_CRITICAL)
															 								 .build());

				responses.add(new NotifierServiceResponseOK<Phone>(NotifierTaskOID.forId(res.messageId()),
													   		phone,
													   	    NotifierType.SMS));	// success
			} catch (final Throwable th) {
				log.error("Error while notifying using [aws sns] to send an sms to {}: {}",
						  phone,
						  th.getMessage(),th);
				responses.add(new NotifierServiceResponseError<Phone>(null,		// no task id
													   		   phone,
													   		   NotifierType.SMS,
													   		   NotifierResponseErrorTypes.UNKNOWN,
													   		   th.getLocalizedMessage()));	// error...if want some more specific error try to catch more detailed exception.
			}
		}
	    // [3] - Build the response
		return responses;
	}
}
