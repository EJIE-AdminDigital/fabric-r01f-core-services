package r01f.cloud.aws.sns.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import r01f.guids.OIDBaseMutable;

public class AWSSNSSmsSenderID
     extends OIDBaseMutable<String> { 	// usually this should extend OIDBaseInmutable BUT it MUST have a default no-args constructor to be serializable

	private static final long serialVersionUID = -3663803045303085477L;
	public AWSSNSSmsSenderID() {
		/* default no args constructor for serialization purposes */
	}
	public AWSSNSSmsSenderID(final String id) {
		
		super(id.length() > 11 ? id.substring(0,11) : id);		// not more than 11 characters
		System.out.println("AWSSNSSmsSenderID=" + id);
		if (!_isValidId(id)) throw new IllegalArgumentException("SMS sender id=" + id + " NOT valid: "  + 
															 	"SMS sender id can contain up to 11 alphanumeric chars " +
																"including at least one letter and NO spaces " +
																"(see https://docs.aws.amazon.com/sns/latest/dg/sms_publish-to-phone.html)");
	}
	public static AWSSNSSmsSenderID valueOf(final String s) {
		return AWSSNSSmsSenderID.forId(s);
	}
	public static AWSSNSSmsSenderID fromString(final String s) {
		return AWSSNSSmsSenderID.forId(s);
	}
	public static AWSSNSSmsSenderID forId(final String id) {
		return new AWSSNSSmsSenderID(id);
	}
	private static final Pattern VALID_PATTERN = Pattern.compile("\\w*" +			// Any letter or number, *: 0 or more times
																 "[a-zA-Z0-9]" +	// Any letter or number
																 "\\w*");			// Any letter or number, *: 0 or more times

	private static boolean _isValidId(final String id) {
		if (id.length() > 11) return false;
		Matcher m = VALID_PATTERN.matcher(id);
		return m.matches();
	}
}
