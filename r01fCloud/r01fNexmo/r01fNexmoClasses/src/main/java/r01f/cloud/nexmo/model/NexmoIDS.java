package r01f.cloud.nexmo.model;

import lombok.Getter;
import r01f.annotations.Immutable;
import r01f.guids.OIDTyped;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.securitycontext.SecurityIDS.LoginID;

public abstract class NexmoIDS {

/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static interface IsNexmoID
					extends OIDTyped<String> {
		// just a marker interface
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	ID's for data, login and authorizations of an user
/////////////////////////////////////////////////////////////////////////////////////////
	@Immutable
	@MarshallType(as="peerId")
	public record NexmoPeerID(@Getter String id) 
	   implements IsNexmoID {
		
		public static NexmoPeerID from(final String id) {
			return new NexmoPeerID(id);
		}
		public static NexmoPeerID forId(final String id) {
			return new NexmoPeerID(id);
		}
		public static NexmoPeerID valueOf(final String id) {
			return new NexmoPeerID(id);
		}
		public static NexmoPeerID fromString(final String id) {
			return new NexmoPeerID(id);
		}
		public static final NexmoPeerID ANONYMOUS = NexmoPeerID.forId("anonymous");
		public boolean isAnonymous() {
			return this.is(ANONYMOUS);
		}
		public static final NexmoPeerID MASTER = NexmoPeerID.forId("master");
		public boolean isMaster() {
			return this.is(MASTER);
		}
		public static final NexmoPeerID ADMIN = NexmoPeerID.forId("admin");;
		public boolean isAdmin() {
			return this.is(ADMIN);
		}
		
		public LoginID toLoginId() {
			return new LoginID(this.getId());
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
//	                                                                          
/////////////////////////////////////////////////////////////////////////////////////////	
	@Immutable
	@MarshallType(as="message_uuid") //<== Don't Change must be this
	public record NexmoMessageUUID(@Getter String id)
	   implements IsNexmoID {
		public static NexmoMessageUUID from(final String id) {
			return new NexmoMessageUUID(id);
		}
		public static NexmoMessageUUID forId(final String id) {
			return new NexmoMessageUUID(id);
		}
		public static NexmoMessageUUID valueOf(final String id) {
			return new NexmoMessageUUID(id);
		}
		public static NexmoMessageUUID fromString(final String id) {
			return new NexmoMessageUUID(id);
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
