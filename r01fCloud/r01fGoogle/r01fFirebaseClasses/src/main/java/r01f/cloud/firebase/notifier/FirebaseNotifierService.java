package r01f.cloud.firebase.notifier;

import java.util.Collection;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MessagingErrorCode;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import r01f.cloud.firebase.model.FirebaseIds.FirebasePushMessageDataItemID;
import r01f.cloud.firebase.model.FirebaseIds.FirebaseRegisteredDeviceToken;
import r01f.cloud.firebase.model.FirebaseIds.FirebaseRegisteredDevicesTopic;
import r01f.cloud.firebase.model.FirebasePushMessageDataItem;
import r01f.cloud.firebase.model.FirebasePushMessageRequest;
import r01f.cloud.firebase.model.FirebasePushMessageResponse;
import r01f.cloud.firebase.service.FirebaseServiceImpl;
import r01f.core.services.notifier.NotifierOIDs.NotifierTaskOID;
import r01f.core.services.notifier.NotifierPushMessage;
import r01f.core.services.notifier.NotifierPushMessageSubscriber;
import r01f.core.services.notifier.NotifierResponseErrorTypes;
import r01f.core.services.notifier.NotifierServiceForPushMessage;
import r01f.core.services.notifier.NotifierServiceResponse;
import r01f.core.services.notifier.NotifierServiceResponseError;
import r01f.core.services.notifier.NotifierServiceResponseOK;
import r01f.core.services.notifier.config.NotifierEnums.NotifierType;
import r01f.guids.CommonOIDs.AppCode;
import r01f.patterns.Factory;
import r01f.util.types.collections.CollectionUtils;

@Slf4j
public class FirebaseNotifierService
  implements NotifierServiceForPushMessage {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final FirebaseServiceImpl _firebaseService;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	//@Inject
	public FirebaseNotifierService(final FirebaseServiceImpl firebaseService) {
		_firebaseService = firebaseService;
	}
/////////////////////////////////////////////////////////////////////////////////////////
// [to do ] OwnedContactMean... cannot be expose a Phone, so
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public NotifierServiceResponse<NotifierPushMessageSubscriber> notify(final AppCode from,
																         final NotifierPushMessageSubscriber to,
														                 final Factory<NotifierPushMessage> messageToBeDeliveredFactory) {

		Collection<NotifierServiceResponse<NotifierPushMessageSubscriber>> res = this.notifyAll(from,
																						 Lists.newArrayList(to),
				 																	     messageToBeDeliveredFactory);
		return Iterables.getFirst(res,null);
	}

	@Override
	public Collection<NotifierServiceResponse<NotifierPushMessageSubscriber>> notifyAll(final AppCode from,
			                                                                            final Collection<NotifierPushMessageSubscriber> to,
			                                                                            final Factory<NotifierPushMessage> messageToBeDeliveredFactory) {
		Collection<NotifierServiceResponse<NotifierPushMessageSubscriber>> responses
						= Lists.newArrayListWithExpectedSize(to.size());

		// [1] - Create the push message
		NotifierPushMessage pushMessage = messageToBeDeliveredFactory.create();

		//[2] key value data items.
		Collection<FirebasePushMessageDataItem> pushMessageDataItems = Lists.newArrayList();
		if (CollectionUtils.hasData(pushMessage.getKeyValueData())) {
			 for (final String key : pushMessage.getKeyValueData().keySet() ) {
				 pushMessageDataItems.add(new FirebasePushMessageDataItem(FirebasePushMessageDataItemID.of(key),pushMessage.getKeyValueData().get(key)));
			 }
		}

		// [3] - push to every destination
		log.warn("[{}] > push to {} subscribers",
												  FirebaseNotifierService.class.getSimpleName(),
												  to.size());
		for (final NotifierPushMessageSubscriber subscriber : to) {
			try {
				FirebaseRegisteredDevicesTopic topic = subscriber.getTopic() != null ?
						                                                                FirebaseRegisteredDevicesTopic.of(subscriber.getTopic().asString()) : null ;
				FirebaseRegisteredDeviceToken token  = subscriber.getToken() != null ?
					                                                                 	FirebaseRegisteredDeviceToken.of(subscriber.getToken().asString()) : null ;
				FirebasePushMessageRequest pushMessageRequest = new FirebasePushMessageRequest(topic,
						                                                                       token,
						                                                                       pushMessage.getTitle(),
						                                                                       pushMessage.getBody(),
						                                                                       pushMessage.getImage(),
						                                                                       pushMessage.getNotificationSound(),
						                                                                       pushMessage.getCollapseKey(),
						                                                                       pushMessage.getChannelId(),
						                                                                       pushMessageDataItems);

				FirebasePushMessageResponse response = _firebaseService.push(pushMessageRequest);

				log.warn(" response : {}",
						                 response.getMessage());

				responses.add(new NotifierServiceResponseOK<>(NotifierTaskOID.supply(),
																					       subscriber,
																					       NotifierType.PUSH));	// success

			} catch (final FirebaseMessagingException firex) {
				MessagingErrorCode errorCode = firex.getMessagingErrorCode();
				  /**
				   * App instance was unregistered from FCM. This usually means that the token used is no longer
				   * valid and a new one must be used.
				   */
				if (errorCode ==  MessagingErrorCode.UNREGISTERED) {
					log.warn(">> Subscriber not found");
					responses.add(new NotifierServiceResponseError<>(NotifierTaskOID.supply(),
	                                                                                              subscriber,
	                                                                                              NotifierType.PUSH,NotifierResponseErrorTypes.SUBSCRIBER_NOT_FOUND,
	                                                                                              firex.getMessage()));	// failed
				} else {
					responses.add(new NotifierServiceResponseError<>(NotifierTaskOID.supply(),
																                                  subscriber,
																                                  NotifierType.PUSH,NotifierResponseErrorTypes.UNKNOWN,
																                                  firex.getMessage()));	// failed
				}



			} catch (final Throwable nitex) {
				responses.add(new NotifierServiceResponseError<>(NotifierTaskOID.supply(),
							                                                                  subscriber,
							                                                                  NotifierType.PUSH,NotifierResponseErrorTypes.UNKNOWN,
							                                                                  nitex.getMessage()));	// failed
			}
		}
	    // [3] - Build the response
		return responses;
	}
}
