package r01f.cloud.aws.sns.model;

import java.util.regex.Pattern;

import lombok.Getter;
import r01f.guids.OIDTyped;

public record AWSSNSSmsSenderID(@Getter String id)
   implements OIDTyped<String> { 	// usually this should extend OIDBaseInmutable BUT it MUST have a default no-args constructor to be serializable
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public AWSSNSSmsSenderID(final String id) {
		this.id = id.length() > 11 ? id.substring(0,11) : id;		// not more than 11 characters
		if (!AWSSNSSmsSenderID.isValid(id)) throw new IllegalArgumentException("SMS sender id=" + id + " NOT valid: "  + 
															 				   "SMS sender id can contain up to 11 alphanumeric chars " +
															 				   "including at least one letter and NO spaces " +
																			   "(see https://docs.aws.amazon.com/sns/latest/dg/sms_publish-to-phone.html)");
	}
	public static AWSSNSSmsSenderID from(final String id) {
		return new AWSSNSSmsSenderID(id);
	}
	public static AWSSNSSmsSenderID forId(final String id) {
		return new AWSSNSSmsSenderID(id);
	}
	public static AWSSNSSmsSenderID valueOf(final String s) {
		return AWSSNSSmsSenderID.forId(s);
	}
	public static AWSSNSSmsSenderID fromString(final String s) {
		return AWSSNSSmsSenderID.forId(s);
	}
	
	private static final Pattern VALID_PATTERN = Pattern.compile("\\w*" +			// Any letter or number, *: 0 or more times
																 "[a-zA-Z0-9]" +	// Any letter or number
																 "\\w*");			// Any letter or number, *: 0 or more times
	public static boolean isValid(final String id) {
		boolean valid = id.length() > 11 ? false : true;
		if (valid) valid = VALID_PATTERN.matcher(id)
										.matches();
		return valid;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public String asString() {
		return this.id;
	}
	@Override
	public String toString() {
		return this.id;
	}
}	
