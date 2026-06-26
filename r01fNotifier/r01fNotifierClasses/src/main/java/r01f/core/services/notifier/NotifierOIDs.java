package r01f.core.services.notifier;

import java.util.UUID;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import r01f.guids.OID;
import r01f.guids.OIDBaseImmutable;
import r01f.objectstreamer.annotations.MarshallType;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class NotifierOIDs {
/////////////////////////////////////////////////////////////////////////////////////////
// 	OIDS.
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * A {@link NotifierMessageToBeDelivered} {@link OID}
	 */
	@MarshallType(as="notifierTaskOID")
	public static class NotifierTaskOID
		 extends OIDBaseImmutable<String> {

		private static final long serialVersionUID = -8429215763429456025L;

		protected NotifierTaskOID(final String id) {
			super(id);
		}
		public static NotifierTaskOID forId(final String id) {
			return new NotifierTaskOID(id);
		}
		public static NotifierTaskOID valueOf(final String id) {
			return new NotifierTaskOID(id);
		}
		public static NotifierTaskOID supply() {
			UUID uuid = UUID.randomUUID();
	        return NotifierTaskOID.forId(uuid.toString().toUpperCase());
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	IDS.
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * A topic identifies a set of devices to which deliver the push.
	 */
	@MarshallType(as="notifierPushTopic")
	public static class NotifierPushTopic
		 extends OIDBaseImmutable<String> {

		private static final long serialVersionUID = -8429215763429456025L;

		protected NotifierPushTopic(final String id) {
			super(id);
		}
		public static NotifierPushTopic forId(final String id) {
			return new NotifierPushTopic(id);
		}
		public static NotifierPushTopic valueOf(final String id) {
			return new NotifierPushTopic(id);
		}
	}
}
