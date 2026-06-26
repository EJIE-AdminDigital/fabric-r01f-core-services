package r01f.cloud.nexmo.model;

import lombok.NoArgsConstructor;
import r01f.annotations.Immutable;
import r01f.guids.OIDBaseMutable;
import r01f.guids.OIDTyped;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.securitycontext.SecurityIDS.LoginID;

public abstract class NexmoIDS {

/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static interface NexmoID
					extends OIDTyped<String> {
		// just a marker interface
	}
	/**
	 * Base for every oid objects
	 */
	@Immutable	
	@NoArgsConstructor
	public static abstract class NexmoIDBase
						 extends OIDBaseMutable<String>
					  implements NexmoID {

		private static final long serialVersionUID = 4162366466990455545L;

		public NexmoIDBase(final String id) {
			super(id);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	ID's for data, login and authorizations of an user
/////////////////////////////////////////////////////////////////////////////////////////
	@Immutable
	@MarshallType(as="peerId")
	@NoArgsConstructor
	public static final class PeerID
					  extends NexmoIDBase {
		private static final long serialVersionUID = -2959560256371887489L;
		public PeerID(final String oid) {
			super(oid);
		}
		public LoginID toLoginId() {
			return new LoginID(this.getId());
		}
		public static PeerID forId(final String id) {
			return new PeerID(id);
		}
		public static PeerID valueOf(final String id) {
			return new PeerID(id);
		}
		public static PeerID fromString(final String id) {
			return new PeerID(id);
		}
		public static final PeerID ANONYMOUS = PeerID.forId("anonymous");
		public boolean isAnonymous() {
			return this.is(ANONYMOUS);
		}
		public static final PeerID MASTER = PeerID.forId("master");
		public boolean isMaster() {
			return this.is(MASTER);
		}
		public static final PeerID ADMIN = PeerID.forId("admin");;
		public boolean isAdmin() {
			return this.is(ADMIN);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	                                                                          
/////////////////////////////////////////////////////////////////////////////////////////	
	@Immutable
	@MarshallType(as="message_uuid") //<== Don't Change must be this
	@NoArgsConstructor
	public static final class MessageUUID
					  extends NexmoIDBase {
		private static final long serialVersionUID = 962520569656270232L;
		public MessageUUID(final String oid) {
			super(oid);
		}
		public static MessageUUID forId(final String id) {
			return new MessageUUID(id);
		}
	}
}
