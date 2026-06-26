package r01f.cloud.firebase.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import r01f.guids.OIDBaseImmutable;
import r01f.objectstreamer.annotations.MarshallType;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class FirebaseIds {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * A token for a registered a firebase device token.
	 */
	@MarshallType(as="firebaseRegisteredDeviceToken")
	public static class FirebaseRegisteredDeviceToken
		extends OIDBaseImmutable<String> {

		private static final long serialVersionUID = 6070024611436072047L;
		private FirebaseRegisteredDeviceToken(final String id) {
			super(id);
		}
		public static FirebaseRegisteredDeviceToken of(final String id) {
			return new FirebaseRegisteredDeviceToken(id);
		}
	}
	/**
	 * A firebase topic represents a group of one or more devices which are subscribed to.
	 */
	@MarshallType(as="firebaseRegisteredDevicesTopic")
	public static class FirebaseRegisteredDevicesTopic
				extends OIDBaseImmutable<String> {

		private static final long serialVersionUID = -5867457273405673410L;
		private FirebaseRegisteredDevicesTopic(final String id) {
			super(id);
		}
		public static FirebaseRegisteredDevicesTopic of(final String id) {
			return new FirebaseRegisteredDevicesTopic(id);
		}
	}
	/**
	 * Data items that could be send with message body.
	 */
	@MarshallType(as="firebasePushMessageDataItemId")
	public static class FirebasePushMessageDataItemID
				extends OIDBaseImmutable<String> {

		private static final long serialVersionUID = -4094018924820109804L;
		private FirebasePushMessageDataItemID(final String id) {
			super(id);
		}
		public static FirebasePushMessageDataItemID of(final String id) {
			return new FirebasePushMessageDataItemID(id);
		}
	}

}
