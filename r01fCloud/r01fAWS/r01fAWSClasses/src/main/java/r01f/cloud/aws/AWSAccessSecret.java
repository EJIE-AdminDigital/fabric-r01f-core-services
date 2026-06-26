package r01f.cloud.aws;

import r01f.guids.OIDBaseMutable;
import r01f.securitycontext.SecurityIDS.Password;

public class AWSAccessSecret
     extends OIDBaseMutable<String> { 	// normally this should extend OIDBaseInmutable BUT it MUST have a default no-args constructor to be serializable

	private static final long serialVersionUID = -4358217246961346683L;

	public AWSAccessSecret() {
		/* default no args constructor for serialization purposes */
	}
	public AWSAccessSecret(final String id) {
		super(id);
	}
	public static AWSAccessSecret valueOf(final String s) {
		return AWSAccessSecret.forId(s);
	}
	public static AWSAccessSecret fromString(final String s) {
		return AWSAccessSecret.forId(s);
	}
	public static AWSAccessSecret forId(final String id) {
		return new AWSAccessSecret(id);
	}
	public static AWSAccessSecret of(final Password pw) {
		return new AWSAccessSecret(pw.asString());
	}
}
