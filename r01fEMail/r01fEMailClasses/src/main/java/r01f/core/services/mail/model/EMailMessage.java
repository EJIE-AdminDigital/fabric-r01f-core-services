package r01f.core.services.mail.model;

import java.util.Collection;

import com.google.common.collect.Lists;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.util.types.collections.CollectionUtils;

@Accessors(prefix="_")
public class EMailMessage {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final EMailRFC822Address _from;
	@Getter private final EMailDestinations _destinations;

	@Getter private final String _subject;

	@Getter private final String _plainTextBody;
	@Getter private final String _htmlBody;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public EMailMessage(final EMailRFC822Address from,final EMailRFC822Address to,
						final String subject,final String plainTextBody) {
		this(from,Lists.newArrayList(to),
			 null,null,
			 subject,plainTextBody,null);	// plain text body (no html)
	}
	public EMailMessage(final EMailRFC822Address from,final Collection<EMailRFC822Address> to,
					   	final String subject,final String plainTextBody) {
		this(from,to,
			 null,null,
			 subject,plainTextBody,null);	// plain text body (no html)
	}
	public EMailMessage(final EMailRFC822Address from,final Collection<EMailRFC822Address> to,final Collection<EMailRFC822Address> cc,
					   	final String subject,final String plainTextBody) {
		this(from,to,
			 cc,null,
			 subject,plainTextBody,null);	// plain text body (no html)
	}
	public EMailMessage(final EMailRFC822Address from,final Collection<EMailRFC822Address> to,final Collection<EMailRFC822Address> cc,final Collection<EMailRFC822Address> bcc,
					   	final String subject,final String plainTextBody) {
		this(from,to,
			 cc,bcc,
			 subject,plainTextBody,null);	// plain text body (no html)
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR (setting html)
/////////////////////////////////////////////////////////////////////////////////////////
	public EMailMessage(final EMailRFC822Address from,final EMailRFC822Address to,
					   	final String subject,
					   	final String plainTextBody,final String htmlBody) {
		this(from,Lists.newArrayList(to),
			 null,null,
			 subject,plainTextBody,htmlBody);
	}
	public EMailMessage(final EMailRFC822Address from,final Collection<EMailRFC822Address> to,
					   	final String subject,
					   	final String plainTextBody,final String htmlBody) {
		this(from,to,
			 null,null,
			 subject,plainTextBody,htmlBody);
	}
	public EMailMessage(final EMailRFC822Address from,final Collection<EMailRFC822Address> to,final Collection<EMailRFC822Address> cc,
					   	final String subject,
					   	final String plainTextBody,final String htmlBody) {
		this(from,to,
			 cc,null,
			 subject,plainTextBody,htmlBody);
	}
	public EMailMessage(final EMailRFC822Address from,final Collection<EMailRFC822Address> to,final Collection<EMailRFC822Address> cc,final Collection<EMailRFC822Address> bcc,
					   	final String subject,
					   	final String plainTextBody,final String htmlBody) {
		_from = from;
		if (CollectionUtils.hasData(to) || CollectionUtils.hasData(cc) || CollectionUtils.hasData(bcc)) {
			_destinations = new EMailDestinations(to,cc,bcc);
		} else {
			_destinations = null;
		}

		_subject = subject;
		_plainTextBody = plainTextBody;
		_htmlBody = htmlBody;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public Collection<EMailRFC822Address> getTo() {
		return _destinations != null ? _destinations.getTo()
									 : null;
	}
	public Collection<EMailRFC822Address> getCc() {
		return _destinations != null ? _destinations.getCc()
									 : null;
	}
	public Collection<EMailRFC822Address> getBcc() {
		return _destinations != null ? _destinations.getBcc()
									 : null;
	}
	public String[] getToAsStringArray() {
		return EMailMessage.eMailRFC822AddressCollectionAsStringArray(this.getTo());
	}
	public String[] getCcAsStringArray() {
		return EMailMessage.eMailRFC822AddressCollectionAsStringArray(this.getCc());
	}
	public String[] getBccAsStringArray() {
		return EMailMessage.eMailRFC822AddressCollectionAsStringArray(this.getBcc());
	}
	public static String[] eMailRFC822AddressCollectionAsStringArray(final Collection<EMailRFC822Address> col) {
		return col.stream()
				  .map(EMailRFC822Address.TO_STRING_TRANSFORM)
				  .toArray(String[]::new);
	}
}
