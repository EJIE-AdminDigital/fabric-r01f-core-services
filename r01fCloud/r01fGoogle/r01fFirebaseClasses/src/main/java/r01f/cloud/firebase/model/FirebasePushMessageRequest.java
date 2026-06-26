package r01f.cloud.firebase.model;

import java.util.Collection;

import com.google.common.collect.Lists;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.cloud.firebase.model.FirebaseIds.FirebaseRegisteredDeviceToken;
import r01f.cloud.firebase.model.FirebaseIds.FirebaseRegisteredDevicesTopic;
import r01f.core.services.notifier.NotifierPushMessage;
import r01f.debug.Debuggable;
import r01f.types.url.Url;
import r01f.util.types.collections.CollectionUtils;

@Accessors(prefix="_")
public class FirebasePushMessageRequest
     extends NotifierPushMessage
  implements Debuggable {

	private static final long serialVersionUID = 1546350024120784015L;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final FirebaseRegisteredDevicesTopic _topic;
	@Getter private final FirebaseRegisteredDeviceToken _token;
	@Getter private final String _title;
	@Getter private final String _body;
	@Getter private final Url _image;
	@Getter private final boolean _asyncRequest;
	@Getter private final Collection<FirebasePushMessageDataItem> _dataItems;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTORS
/////////////////////////////////////////////////////////////////////////////////////////
	public FirebasePushMessageRequest(final FirebaseRegisteredDevicesTopic topic,
									  final FirebaseRegisteredDeviceToken token,
									  final String title,
									  final String body,
									  final Url image,
									  final String notificationSound,
									  final String collapseKey,
									  final String channelId,
									  final Collection<FirebasePushMessageDataItem> dataItems) {
		_topic = topic;
		_token = token;
		_image = image;
		_title = title;
		_body = body;
		_notificationSound = notificationSound;
		_collapseKey = collapseKey;
		_channelId = channelId;
		_dataItems = dataItems;
		_asyncRequest = true;
	}
	public FirebasePushMessageRequest(final FirebaseRegisteredDevicesTopic topic,
									  final String title,
									  final String body,
									  final String notificationSound,
									  final String collapseKey,
									  final String channelId,
									  final FirebasePushMessageDataItem... dataItems) {
		this(topic,
			 null,			// token
			 title, body,null,
			 notificationSound, collapseKey, channelId,
			 Lists.newArrayList(dataItems));
		_notificationSound = notificationSound;
		_collapseKey = collapseKey;
		_channelId = channelId;

	}
	public FirebasePushMessageRequest(final FirebaseRegisteredDeviceToken token,
							  		  final String title,
							  		  final String body,
							  		  final String notificationSound,
									  final String collapseKey,
									  final String channelId,
							  		  final FirebasePushMessageDataItem... dataItems) {
		this(null,			// topic
			 token,
			 title, body,null,
			 notificationSound, collapseKey, channelId,
			 Lists.newArrayList(dataItems));
	}
	public FirebasePushMessageRequest(final FirebaseRegisteredDevicesTopic topic,
							  		  final String title,
							  		  final String body,
							  		  final FirebasePushMessageDataItem... dataItems) {
		this(topic,			// topic
		     null,
		     title, body,null,
		     null, null, null,
		     Lists.newArrayList(dataItems));
	}
	public FirebasePushMessageRequest(final FirebaseRegisteredDeviceToken token,
							  		  final String title,
							  		  final String body,final  Url image,
							  		  final FirebasePushMessageDataItem... dataItems) {
		this(null,			// topic
		     token,
		     title, body,image,
		     "default", null, null,
		Lists.newArrayList(dataItems));
	}

	/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean hasToken() {
		return _token != null;
	}
	public boolean hasDataItems() {
		return CollectionUtils.hasData(_dataItems);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	DEBUGGABLE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CharSequence debugInfo() {
		StringBuilder dbg = new StringBuilder();
		if (_title != null) {
			dbg.append("\n Title: ")
			   .append(_title);
		}
		if (_body != null) {
			dbg.append("\n body: ")
			   .append(_body);
		}
		if (_image != null) {
			dbg.append("\n image: ")
			   .append(_image);
		}
		if (_token != null) {
			dbg.append("\n token: ")
			   .append(_token);
		}
		if (_topic != null) {
			dbg.append("\n topic: ")
			   .append(_topic);
		}
		if (_notificationSound != null) {
			dbg.append("\n notification sound: ")
			   .append(_notificationSound);
		}
		if (_collapseKey != null) {
			dbg.append("\n collapseKey: ")
			   .append(_collapseKey);
		}
		if (_channelId != null) {
			dbg.append("\n channelId: ")
			   .append(_channelId);
		}
		dbg.append("\n asyncRequest: ")
			   .append(_asyncRequest);

		if (CollectionUtils.hasData(_dataItems)) {
			dbg.append("\n data items: ");
			for (FirebasePushMessageDataItem it : _dataItems ) {
				dbg.append("\t").append( it.getId() + ": " + it.getValue());
			}
		}
		return dbg;
	}
}
