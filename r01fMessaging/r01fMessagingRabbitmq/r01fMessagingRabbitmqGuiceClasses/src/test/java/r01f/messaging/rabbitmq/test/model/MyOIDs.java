package r01f.messaging.rabbitmq.test.model;

import lombok.Getter;
import r01f.guids.OIDTyped;
import r01f.guids.PersistableObjectOID;
import r01f.objectstreamer.annotations.MarshallType;

public abstract class MyOIDs {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallType(as="myTestOid")
	public record MyTestOID(@Getter String id)
	   implements OIDTyped<String>,
	   			  PersistableObjectOID {
		public static MyTestOID from(final String oid) {
			return new MyTestOID(oid);
		}
		public static MyTestOID forId(final String oid) {
			return new MyTestOID(oid);
		}
		public static MyTestOID valueOf(final String oid) {
			return new MyTestOID(oid);
		}
		public static MyTestOID fromString(final String oid) {
			return new MyTestOID(oid);
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
	@MarshallType(as="myOtherTestOid")
	public record MyOtherTestOID(@Getter String id)
	   implements OIDTyped<String> {
		public static MyOtherTestOID from(final String oid) {
			return new MyOtherTestOID(oid);
		}
		public static MyOtherTestOID forId(final String oid) {
			return new MyOtherTestOID(oid);
		}
		public static MyOtherTestOID valueOf(final String oid) {
			return new MyOtherTestOID(oid);
		}
		public static MyOtherTestOID fromString(final String oid) {
			return new MyOtherTestOID(oid);
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
