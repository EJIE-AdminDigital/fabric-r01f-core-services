package r01f.cloud.aws;

import lombok.Getter;
import r01f.guids.OID;
import r01f.guids.OIDTyped;

public record AWSAccessKey(@Getter String id)
   implements OIDTyped<String> { 	// normally this should extend OIDBaseInmutable BUT it MUST have a default no-args constructor to be serializable

	public static AWSAccessKey from(final String s) {
		return AWSAccessKey.forId(s);
	}
	
	public static AWSAccessKey valueOf(final String s) {
		return AWSAccessKey.forId(s);
	}
	public static AWSAccessKey fromString(final String s) {
		return AWSAccessKey.forId(s);
	}
	public static <O extends OID> AWSAccessKey of(final O oid) {
		return new AWSAccessKey(oid.asString());
	}
	public static AWSAccessKey forId(final String s) {
		return new AWSAccessKey(s);
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
