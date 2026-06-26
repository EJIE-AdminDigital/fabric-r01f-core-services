package r01f.cloud.aws;

import r01f.guids.OID;
import r01f.guids.OIDBaseMutable;

public class AWSAccessKey
     extends OIDBaseMutable<String> { 	// normally this should extend OIDBaseInmutable BUT it MUST have a default no-args constructor to be serializable

	private static final long serialVersionUID = -4358217246961346683L;
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	public AWSAccessKey() {
		/* default no args constructor for serialization purposes */
	}
	public AWSAccessKey(final String id) {
		super(id);
	}
	public static AWSAccessKey valueOf(final String s) {
		return AWSAccessKey.forId(s);
	}
	public static AWSAccessKey fromString(final String s) {
		return AWSAccessKey.forId(s);
	}
	public static AWSAccessKey forId(final String id) {
		return new AWSAccessKey(id);
	}
	public static <O extends OID> AWSAccessKey of(final O oid) {
		return new AWSAccessKey(oid.asString());
	}
}
