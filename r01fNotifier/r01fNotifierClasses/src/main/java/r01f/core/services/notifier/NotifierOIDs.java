package r01f.core.services.notifier;

import java.util.UUID;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import r01f.guids.OID;
import r01f.guids.OIDTyped;
import r01f.objectstreamer.annotations.MarshallType;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class NotifierOIDs {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public interface IsNotifierObjectOID 
		 	 extends OIDTyped<String> {
		// just a marker interface
	}
	public interface IsNotifierObjectID 
		 	 extends OIDTyped<String> {
		// just a marker interface
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	OIDS.
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * A {@link NotifierMessageToBeDelivered} {@link OID}
	 */
	@MarshallType(as="notifierTaskOID")
	public record NotifierTaskOID(@Getter String id)
	   implements IsNotifierObjectOID {

		public static NotifierTaskOID from(final String id) {
			return new NotifierTaskOID(id);
		}
		public static NotifierTaskOID forId(final String id) {
			return new NotifierTaskOID(id);
		}
		public static NotifierTaskOID valueOf(final String id) {
			return new NotifierTaskOID(id);
		}
		public static NotifierTaskOID fromString(final String id) {
			return new NotifierTaskOID(id);
		}
		public static NotifierTaskOID supply() {
			UUID uuid = UUID.randomUUID();
	        return NotifierTaskOID.forId(uuid.toString().toUpperCase());
		}
		
		@Override
		public String asString() {
			return this.id;
		}
		@Override
		public String toString() {
			return this.id;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	IDS.
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * A topic identifies a set of devices to which deliver the push.
	 */
	@MarshallType(as="notifierPushTopic")
	public record NotifierPushTopic(@Getter String id)
	   implements IsNotifierObjectID {
		
		
		public static NotifierPushTopic from(final String id) {
			return new NotifierPushTopic(id);
		}
		public static NotifierPushTopic forId(final String id) {
			return new NotifierPushTopic(id);
		}
		public static NotifierPushTopic valueOf(final String id) {
			return new NotifierPushTopic(id);
		}
		public static NotifierPushTopic fromString(final String id) {
			return new NotifierPushTopic(id);
		}
		
		@Override
		public String asString() {
			return this.id;
		}
		@Override
		public String toString() {
			return this.id;
		}
	}
}
