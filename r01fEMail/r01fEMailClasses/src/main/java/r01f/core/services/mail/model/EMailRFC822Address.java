package r01f.core.services.mail.model;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import jakarta.mail.Address;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.types.contact.ContactMean;
import r01f.types.contact.EMail;
import r01f.util.types.collections.CollectionUtils;


/**
 * Represents a RFC822 address
 */
@Slf4j
@Accessors(prefix="_")
@RequiredArgsConstructor
public class EMailRFC822Address
  implements ContactMean {

	private static final long serialVersionUID = -2963399480304370901L;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final EMail _email;
    @Getter private final String _name;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR / BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
    public static EMailRFC822Address of(final EMail email) {
    	return new EMailRFC822Address(email,null);
    }
    public static EMailRFC822Address of(final EMail email,final String name) {
    	return new EMailRFC822Address(email,name);
    }
    public static EMailRFC822Address of(final String email) {
    	return new EMailRFC822Address(EMail.of(email),null);
    }
    public static EMailRFC822Address of(final String email,final String name) {
    	return new EMailRFC822Address(EMail.of(email),name);
    }
    public static Collection<EMailRFC822Address> collectionFrom(final Collection<EMail> emails) {
    	return CollectionUtils.hasData(emails) ? emails.stream()
    												   .map(EMailRFC822Address::of)
    												   .collect(Collectors.toList())
    										   : Collections.emptyList();
    }
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public String toString() {
    	return this.asString();
    }
	@Override
	public String asString() {
		return this.asRFC822Address();
	}
    public String asRFC822Address() {
    	return EMailRFC822Address.asRFC822Address(this);
    }
    public InternetAddress asRFC822InternetAddress() {
    	return this.asRFC822InternetAddressUsing(Charset.defaultCharset());
    }
    public InternetAddress asRFC822InternetAddressUsing(final Charset charset) {
    	return EMailRFC822Address.asRFC822InternetAddress(this,
    													  charset);
    }
/////////////////////////////////////////////////////////////////////////////////////////
//	TRANSFORM
/////////////////////////////////////////////////////////////////////////////////////////
    public static InternetAddress asRFC822InternetAddress(final EMailRFC822Address addr,
    													  final Charset charset) {
    	try {
			return new InternetAddress(addr.getEmail().asString(),
									   addr.getName(),
									   charset.name());
		} catch (final UnsupportedEncodingException e) {
			log.error("Error converting {} email={} name={} to {}: {}",
					  EMailRFC822Address.class.getSimpleName(),
					  addr.getEmail(),addr.getName(),
					  InternetAddress.class.getSimpleName(),
					  e.getMessage(),e);
		}
    	return null;
    }
    public static InternetAddress[] multipleAsRFC822InternetAddress(final Collection<EMailRFC822Address> addrs) {
    	return EMailRFC822Address.multipleAsRFC822InternetAddress(addrs,
    															  Charset.defaultCharset());
    }
    public static InternetAddress[] multipleAsRFC822InternetAddress(final Collection<EMailRFC822Address> addrs,
    													  		    final Charset charset) {
    	if (CollectionUtils.isNullOrEmpty(addrs)) return null;
    	return addrs.stream()
					.map(addr -> EMailRFC822Address.asRFC822InternetAddress(addr,
																	  		charset))
					.toArray(InternetAddress[]::new);
    }
    public static String asRFC822Address(final EMailRFC822Address addr) {
        // rfc822 format
        return String.format("\"%s\" <%s>",
        					 addr.getName() != null ? addr.getName() : addr.getEmail(),addr.getEmail());
    }
    public static String multipleAsRFC822Address(final Collection<EMailRFC822Address> addrs) {
    	return CollectionUtils.toStringCommaSeparated(addrs);
    }
    public static EMailRFC822Address fromRFC822AddressString(final String addr) {
    	Collection<InternetAddress> iAddrs = _parseInternetAddress(addr);
    	if (iAddrs == null) throw new IllegalArgumentException(addr + " is NOT a valid rfc822 address as xx@domain.com <name>");
    	if (iAddrs.size() > 1) throw new IllegalArgumentException("This method only accepts a single rfc822 address formatted as xx@domain.com <name>");
    	return EMailRFC822Address.fromRFC822Address(Iterables.get(iAddrs,0));
    }
    public static EMailRFC822Address[] multipleFromRFC822AddressString(final String addr) {
    	Collection<InternetAddress> iAddrs = _parseInternetAddress(addr);
    	if (iAddrs == null) throw new IllegalArgumentException(addr + " is NOT a valid rfc822 address of comma separated addresses as xx@domain.com <name>");
    	return iAddrs.stream()
					 .map(EMailRFC822Address::fromRFC822Address)
					 .toArray(EMailRFC822Address[]::new);
    }
    private static Collection<InternetAddress> _parseInternetAddress(final String addr) {
    	InternetAddress[] iAddr = null;
    	try {
    		iAddr = InternetAddress.parse(addr,true);
    	} catch (AddressException addrEx) {
    		log.error("Could not parse RFC822 email address {}: {}",
    				  addr,
    				  addrEx.getMessage(),addrEx);
    	}
    	return iAddr != null ? Lists.newArrayList(iAddr)
    						 : null;
    }
    public static EMailRFC822Address fromRFC822Address(final Address addr) {
    	if (!(addr instanceof InternetAddress iAddr)) throw new IllegalArgumentException();
		return new EMailRFC822Address(EMail.of(iAddr.getAddress()),
									  iAddr.getPersonal());
    }
    public static Collection<EMailRFC822Address> multipleFromRFC822Address(final Collection<Address> addrs) {
    	if (CollectionUtils.isNullOrEmpty(addrs)) return null;
    	return addrs.stream()
					.map(EMailRFC822Address::fromRFC822Address)
					.collect(Collectors.toList());
    }
/////////////////////////////////////////////////////////////////////////////////////////
//	TRANSFORM
/////////////////////////////////////////////////////////////////////////////////////////
	public static final Function<String,EMailRFC822Address> FROM_STRING_TRANSFORM = EMailRFC822Address::fromRFC822AddressString;
	public static final Function<EMailRFC822Address,String> TO_STRING_TRANSFORM = email -> email.asRFC822Address();
}
