package r01f.cloud.firebase.service;

import com.google.firebase.messaging.FirebaseMessagingException;

import r01f.cloud.firebase.model.FirebasePushMessageRequest;
import r01f.cloud.firebase.model.FirebasePushMessageResponse;

public interface FirebaseService {	//interface

	FirebasePushMessageResponse push (final FirebasePushMessageRequest pushMessageRequest) throws FirebaseMessagingException;
}
