package r01f.cloud.firebase.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import r01f.guids.OIDTyped;
import r01f.objectstreamer.annotations.MarshallType;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class FirebaseIds {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public interface IsFireBaseID
			 extends OIDTyped<String> {
		// just a marker interface
	}
	/**
	 * A token for a registered a firebase device token.
	 */
	@MarshallType(as="firebaseRegisteredDeviceToken")
	public record FirebaseRegisteredDeviceToken(@Getter String id)
	   implements IsFireBaseID {
		
		public static FirebaseRegisteredDeviceToken from(final String id) {
			return new FirebaseRegisteredDeviceToken(id);
		}
		public static FirebaseRegisteredDeviceToken forId(final String id) {
			return new FirebaseRegisteredDeviceToken(id);
		}
		public static FirebaseRegisteredDeviceToken valueOf(final String id) {
			return new FirebaseRegisteredDeviceToken(id);
		}
		public static FirebaseRegisteredDeviceToken fromString(final String id) {
			return new FirebaseRegisteredDeviceToken(id);
		}
		public static FirebaseRegisteredDeviceToken of(final String id) {
			return new FirebaseRegisteredDeviceToken(id);
		}
		@Override
		public String asString() {
			return this.getId();
		}
		@Override
		public String toString() {
			return this.getId();
		}
	}
	/**
	 * A firebase topic represents a group of one or more devices which are subscribed to.
	 */
	@MarshallType(as="firebaseRegisteredDevicesTopic")
	public record FirebaseRegisteredDevicesTopic(@Getter String id)
	   implements IsFireBaseID {

		public static FirebaseRegisteredDevicesTopic from(final String id) {
			return new FirebaseRegisteredDevicesTopic(id);
		}
		public static FirebaseRegisteredDevicesTopic forId(final String id) {
			return new FirebaseRegisteredDevicesTopic(id);
		}
		public static FirebaseRegisteredDevicesTopic valueOf(final String id) {
			return new FirebaseRegisteredDevicesTopic(id);
		}
		public static FirebaseRegisteredDevicesTopic fromString(final String id) {
			return new FirebaseRegisteredDevicesTopic(id);
		}
		public static FirebaseRegisteredDevicesTopic of(final String id) {
			return new FirebaseRegisteredDevicesTopic(id);
		}
		@Override
		public String asString() {
			return this.getId();
		}
		@Override
		public String toString() {
			return this.getId();
		}
	}
	/**
	 * Data items that could be send with message body.
	 */
	@MarshallType(as="firebasePushMessageDataItemId")
	public record FirebasePushMessageDataItemID(@Getter String id)
	   implements IsFireBaseID {

		public static FirebasePushMessageDataItemID from(final String id) {
			return new FirebasePushMessageDataItemID(id);
		}
		public static FirebasePushMessageDataItemID forId(final String id) {
			return new FirebasePushMessageDataItemID(id);
		}
		public static FirebasePushMessageDataItemID valueOf(final String id) {
			return new FirebasePushMessageDataItemID(id);
		}
		public static FirebasePushMessageDataItemID fromString(final String id) {
			return new FirebasePushMessageDataItemID(id);
		}
		public static FirebasePushMessageDataItemID of(final String id) {
			return new FirebasePushMessageDataItemID(id);
		}
		@Override
		public String asString() {
			return this.getId();
		}
		@Override
		public String toString() {
			return this.getId();
		}
	}

}
