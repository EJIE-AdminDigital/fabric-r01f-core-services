package r01f.cloud.firebase.service;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ExecutionException;

import com.google.api.client.http.HttpContent;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.IncomingHttpResponse;
import com.google.firebase.OutgoingHttpRequest;
import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Aps;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Message.Builder;
import com.google.firebase.messaging.Notification;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.cloud.firebase.model.FirebasePushMessageRequest;
import r01f.cloud.firebase.model.FirebasePushMessageResponse;
import r01f.httpclient.HttpClientProxySettings;
import r01f.service.ServiceCanBeDisabled;

@Slf4j
public class FirebaseServiceImpl
  implements FirebaseService,
			 ServiceCanBeDisabled {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final FirebaseAPIData    _apiData;
	private final FirebaseMessaging  _firebaseMessagingInstance;
	private boolean _disabled;
	@SuppressWarnings("unused")
	private final HttpClientProxySettings _proxySettings;	// TODO enable api with proxy
	
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public FirebaseServiceImpl(final FirebaseConfig config) {
		this(config.getApiData(),
			 config.getProxySettings());
	}
	public FirebaseServiceImpl(final FirebaseAPIData apiData) {
		this(apiData,
			 null);		// proxy settings
	}
	

	public FirebaseServiceImpl(final FirebaseAPIData apiData,
							   final HttpClientProxySettings proxySettings) {
		_apiData = apiData;
		_proxySettings = proxySettings;
		FirebaseOptions	options = FirebaseOptions.builder()
												  .setCredentials(_apiData.getGoogleCredentials())
												  .build();
		
		if (FirebaseApp.getApps().isEmpty()) {
			FirebaseApp.initializeApp(options);
		}
		
		_firebaseMessagingInstance = FirebaseMessaging.getInstance();
}
/////////////////////////////////////////////////////////////////////////////////////////
//ServiceCanBeDisabled
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
//	API DATA
/////////////////////////////////////////////////////////////////////////////////////////
	@Accessors(prefix="_")
	@RequiredArgsConstructor
	public static class FirebaseAPIData {
		@Getter private final GoogleCredentials _googleCredentials;  //GoogleCredentials.fromStream(stream)
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public FirebasePushMessageResponse push(final FirebasePushMessageRequest pushMessageRequest) throws FirebaseMessagingException {
		log.warn(".. FirebaseServiceImpl.push {}",
							pushMessageRequest.debugInfo());
		Message message = _buidMessage(pushMessageRequest);
		FirebasePushMessageResponse response  = null;
		if (pushMessageRequest.isAsyncRequest()) {
			response = _pushMessageAsync(pushMessageRequest, message);
		} else {
			response = _pushMessage(pushMessageRequest, message);
		}		
		return response;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	private  FirebasePushMessageResponse _pushMessage(final FirebasePushMessageRequest pushMessageRequest, final Message message) throws FirebaseMessagingException {
		try {
			log.warn(".. push.sync {}", message.toString());
			return new FirebasePushMessageResponse(_firebaseMessagingInstance.send(message));
		} catch (final  FirebaseMessagingException e) {		
			_printFirebaseMessagingException(message.toString(), pushMessageRequest, e);
			throw e;	 // Throw :: This will be catch FirebaseNotifierService and handled as known notifier error type	  
		} catch (final  Throwable e) {
			log.error("Error on sync push {} - Throwable - printStackTrace and throw", message.toString());
			e.printStackTrace();
			throw new RuntimeException(e.getLocalizedMessage());
		} 
	}
	private  FirebasePushMessageResponse _pushMessageAsync(final FirebasePushMessageRequest pushMessageRequest, final Message message) throws FirebaseMessagingException {
		try {
			log.warn(".. push.async {}", message.toString());
			return new FirebasePushMessageResponse(_firebaseMessagingInstance.sendAsync(message).get());
		} catch (final ExecutionException e) {
			log.error("Error on async push {} : {}", message.toString(), e.getLocalizedMessage());
			e.printStackTrace();
			Throwable executionException = e.getCause ();
			if (executionException instanceof FirebaseMessagingException  ) {
				log.error("Error on async push {} is a FirebaseMessagingException", message.toString());
				FirebaseMessagingException fme = (FirebaseMessagingException) executionException;
				_printFirebaseMessagingException(message.toString(), pushMessageRequest, fme);
				throw fme; // Throw :: This will be catch FirebaseNotifierService and handled as known notifier error type	  
			}
			log.error("Error on async push {} - throw", message.toString());
			throw new RuntimeException(e.getLocalizedMessage());
		} catch (final  Throwable e) {
			log.error("Error on async push {} - Throwable - printStackTrace and throw", message.toString());
			e.printStackTrace();
			throw new RuntimeException(e.getLocalizedMessage());
		}
	}
	
	/**
	 * Builds APN Config.
	 * @param topic
	 * @return
	 */
	private static ApnsConfig _buildApnsConfig(final FirebasePushMessageRequest pushMessageRequest) {
		return ApnsConfig.builder()
  						  .setAps(Aps.builder()
  								  	.setSound(pushMessageRequest.getNotificationSound())
  									 // .setCategory(topic.asString())
  									 // .setThreadId(topic.asString()
  								   .build())
  						  .build();
	}
	/**
	 * Builds the Android-specific options that can be included in a Message.Instances of this class are thread-safe and immutable
	 * @param topic
	 * @return
	 */
	private static AndroidConfig _buildAndroidConfig(final FirebasePushMessageRequest pushMessageRequest) {
	  	return AndroidConfig.builder()
						  .setTtl(Duration.ofMinutes(2).toMillis())
						  .setCollapseKey(pushMessageRequest.getCollapseKey())
						  .setPriority(AndroidConfig.Priority.HIGH)
						  .setNotification(AndroidNotification.builder()
															  .setSound(pushMessageRequest.hasCustomNotificationSound() ? pushMessageRequest.getNotificationSound() : "default")
															  .setChannelId(pushMessageRequest.hasChannelId() ? pushMessageRequest.getChannelId() : null)
															  .setDefaultVibrateTimings(true)															  
															  //.setClickAction("MAINACTIVITY")
															  .setImage(pushMessageRequest.getImage() == null ? null :
																  												pushMessageRequest.getImage().asString())
															  .build())
						  
						  .build();
	}
	/**
	 * Builds custom core Notification of the Message.
	 * @param pushMessageRequest
	 * @return
	 */
	private static Notification _buidMessageNotification(final FirebasePushMessageRequest pushMessageRequest) {
		
		Notification.Builder  notificationBuilder = 
				 Notification.builder();
		if (pushMessageRequest.getTitle() != null ) {
			notificationBuilder.setTitle(pushMessageRequest.getTitle() );
		}
		if (pushMessageRequest.getBody() != null ) {
			notificationBuilder.setBody(pushMessageRequest.getBody() );
		}
		if (pushMessageRequest.hasImage()  ) {
			notificationBuilder.setImage(pushMessageRequest.getImage().asString());
		}
		return notificationBuilder.build();
	}
	
  	private static Message _buidMessage(final FirebasePushMessageRequest pushMessageRequest) {
  		Builder baseMessage  = Message.builder()
							  		  .setApnsConfig(_buildApnsConfig(pushMessageRequest))
							  		  .setAndroidConfig(_buildAndroidConfig(pushMessageRequest))
							  		  .setNotification(_buidMessageNotification(pushMessageRequest));
  		
		if (pushMessageRequest.hasToken()) {
			baseMessage.setToken(pushMessageRequest.getToken().asString());
		} else {
			baseMessage.setTopic(pushMessageRequest.getTopic().asString());
		}
		if (pushMessageRequest.hasDataItems()) {
			pushMessageRequest.getDataItems()
						   	  .forEach(i-> {
							   					baseMessage.putData(i.getId().asString(), i.getValue());
						   			        });
  		}		
  		return baseMessage.build();
  	}
  	
	private void _printFirebaseMessagingException(final String messageTag, final FirebasePushMessageRequest pushMessageRequest, final FirebaseMessagingException fme) {
		log.error("Error on push {} > _printFirebaseMessagingException", messageTag);
		log.error("Error on push {} > _printFirebaseMessagingException > Message :\n{}",
				  messageTag, _getLogMessage(pushMessageRequest));
		StringBuffer sb = new StringBuffer("");
		if (fme.getErrorCode() != null) {
			sb.append("> ErrorCode = ").append(fme.getErrorCode().ordinal())
			  .append(" > ").append(fme.getErrorCode().name()).append("\n");
		}
		sb.append("> Message = ").append(fme.getMessage()).append("\n");
		if (fme.getHttpResponse() != null) {
			IncomingHttpResponse response = fme.getHttpResponse();
			sb.append("> HttpResponse >").append("\n")
			  .append("  > Status code = ").append(response.getStatusCode()).append("\n")
			  .append("  > Content = ").append(response.getContent()).append("\n");
			if (response.getRequest() != null) {
				OutgoingHttpRequest request = response.getRequest();
				sb.append("  > Request >").append("\n")
				  .append("    > URL = ").append(request.getUrl()).append("\n")
				  .append("    > Method = ").append(request.getMethod()).append("\n");
				if (request.getContent() != null) {
					HttpContent content = request.getContent();
					sb.append("    > Content >").append("\n")
					  .append("      > Type = ").append(content.getType()).append("\n")
					  .append("      > Length = ");
					try {
						sb.append(content.getLength());
					} catch (IOException e) {
						sb.append(e.getLocalizedMessage());
					}
				}
			}
		}
		log.error("Error on push {} > _printFirebaseMessagingException > Error :\n{}",
				  messageTag, sb.toString());
	}
	
	private static String _getLogMessage(final FirebasePushMessageRequest pushMessageRequest) {
		StringBuffer sb = new StringBuffer();
		if (pushMessageRequest.hasToken()) {
			sb.append("Token = ").append(pushMessageRequest.getToken().asString()).append("\n");
		} else {
			sb.append("Topic = ").append(pushMessageRequest.getTopic().asString()).append("\n");
		}
		if (pushMessageRequest.hasDataItems()) {
			sb.append("Data items >\n");
			pushMessageRequest.getDataItems()
							  .forEach(i-> {
								  sb.append("  > ").append(i.getId().asString())
								  .append(" = ").append(i.getValue()).append("\n");
							  });
		}
		sb.append("Notification >\n");
		if (pushMessageRequest.getTitle() != null ) {
			sb.append("  > Title = ").append(pushMessageRequest.getTitle()).append("\n");
		}
		if (pushMessageRequest.getBody() != null ) {
			sb.append("  > Body = ").append(pushMessageRequest.getBody()).append("\n");
		}
		if (pushMessageRequest.hasImage()  ) {
			sb.append("  > Image = ").append(pushMessageRequest.getImage().asString()).append("\n");
		}
		sb.append("Apns Config >\n")
		  .append("  > Sound = ").append(pushMessageRequest.getNotificationSound()).append("\n");
		sb.append("Android Config >\n")
		  .append("  > Sound = ").append(pushMessageRequest.hasCustomNotificationSound() ? pushMessageRequest.getNotificationSound() : "default").append("\n")
		  .append("  > Channel Id = ").append(pushMessageRequest.hasChannelId() ? pushMessageRequest.getChannelId() : "null").append("\n")
		  .append("  > Image = ").append(pushMessageRequest.hasImage() ? pushMessageRequest.getImage().asString() : "null");
		return sb.toString();
  	}
}
