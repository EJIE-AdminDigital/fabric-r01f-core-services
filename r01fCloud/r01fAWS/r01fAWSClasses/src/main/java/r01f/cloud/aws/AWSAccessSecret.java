package r01f.cloud.aws;

import lombok.Getter;
import r01f.guids.OIDTyped;
import r01f.securitycontext.SecurityIDS.Password;

public record AWSAccessSecret(@Getter String id)
   implements OIDTyped<String> { 	// normally this should extend OIDBaseInmutable BUT it MUST have a default no-args constructor to be serializable

	public static AWSAccessSecret from(final String s) {
		return AWSAccessSecret.forId(s);
	}
	public static AWSAccessSecret forId(final String id) {
		return new AWSAccessSecret(id);
	}
	public static AWSAccessSecret valueOf(final String s) {
		return AWSAccessSecret.forId(s);
	}
	public static AWSAccessSecret fromString(final String s) {
		return AWSAccessSecret.forId(s);
	}
	public static AWSAccessSecret of(final Password pw) {
		return new AWSAccessSecret(pw.asString());
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
