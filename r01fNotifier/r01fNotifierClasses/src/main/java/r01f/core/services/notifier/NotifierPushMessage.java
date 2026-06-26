package r01f.core.services.notifier;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.types.url.Url;
import r01f.util.types.Strings;


@Accessors(prefix="_")
@RequiredArgsConstructor
public class NotifierPushMessage 
  implements Serializable {

	private static final long serialVersionUID = 1494712267736134866L;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter protected String _title;
	@Getter protected String _body;
	@Getter protected Url _image;
	@Getter protected Map<String,String> _keyValueData; // will be transformed to provider (firebase, etc..), key value format.
	@Getter protected String _notificationSound;
	@Getter protected String _collapseKey;
	@Getter protected String _channelId;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTORS
/////////////////////////////////////////////////////////////////////////////////////////	
	public NotifierPushMessage(final String title,final String body) {
		this(title, body,
			 null);
	}
	public NotifierPushMessage(final String title,final String body,
							   final Map<String,String> keyValueData) {
		_title = title;
		_body = body;
		_keyValueData = keyValueData != null ? keyValueData
											 : new HashMap<String,String>();
	}
	public NotifierPushMessage(final String title,final String body, 
							   final String notificationSound, 
							   final Map<String, String> keyValueData) {
		_title = title;
		_body = body;
		_notificationSound = notificationSound;
		_keyValueData = keyValueData != null ? keyValueData : new HashMap<String,String>();
	}
	public NotifierPushMessage(final String title,final String body, 
							   final String notificationSound,
							   final String collapseKey, 
							   final Map<String, String> keyValueData) {
		_title = title;
		_body = body;
		_notificationSound = notificationSound;
		_collapseKey = collapseKey;
		_keyValueData = keyValueData != null ? keyValueData : new HashMap<String,String>();
	}
	public NotifierPushMessage(final String title,final String body, final Url image,
							   final String notificationSound,
							   final String collapseKey,
							   final String channelId, 
							   final Map<String, String> keyValueData) {
		_title = title;
		_body = body;
		_image = image;
		_notificationSound = notificationSound;
		_collapseKey = collapseKey;
		_channelId = channelId;
		_keyValueData = keyValueData != null ? keyValueData : new HashMap<String,String>();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	public boolean hasCustomNotificationSound() {
		return Strings.isNOTNullOrEmpty(_notificationSound);
	}
	public boolean hasCollapseKey() {
		return Strings.isNOTNullOrEmpty(_collapseKey);
	}
	public boolean hasChannelId() {
		return Strings.isNOTNullOrEmpty(_channelId);
	}
	public boolean hasImage() {
		return _image != null;
	}
}