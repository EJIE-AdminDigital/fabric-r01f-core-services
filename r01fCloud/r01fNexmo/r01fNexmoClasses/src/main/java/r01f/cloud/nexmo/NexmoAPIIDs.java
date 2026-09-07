package r01f.cloud.nexmo;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import r01f.guids.OIDTyped;
import r01f.objectstreamer.annotations.MarshallType;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class NexmoAPIIDs {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public interface IsNexmoID 
			 extends OIDTyped<String> {
		// just a marker interface
	}
	@MarshallType(as="apiClientID")
	public record NexmoAPIClientID(@Getter String id)
	   implements IsNexmoID {
		public static NexmoAPIClientID from(final String id) {
			return new NexmoAPIClientID(id);
		}
		public static NexmoAPIClientID forId(final String id) {
			return new NexmoAPIClientID(id);
		}
		public static NexmoAPIClientID valueOf(final String id) {
			return new NexmoAPIClientID(id);
		}
		public static NexmoAPIClientID fromString(final String id) {
			return new NexmoAPIClientID(id);
		}
		public static NexmoAPIClientID of(final String id) {
			return new NexmoAPIClientID(id);
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
	@MarshallType(as="applicationID")
	public record NexmoApplicationtID(@Getter String id)
	   implements IsNexmoID {
		public static NexmoApplicationtID from(final String id) {
			return new NexmoApplicationtID(id);
		}
		public static NexmoApplicationtID forId(final String id) {
			return new NexmoApplicationtID(id);
		}
		public static NexmoApplicationtID valueOf(final String id) {
			return new NexmoApplicationtID(id);
		}
		public static NexmoApplicationtID fromString(final String id) {
			return new NexmoApplicationtID(id);
		}
		public static NexmoApplicationtID of(final String id) {
			return new NexmoApplicationtID(id);
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
