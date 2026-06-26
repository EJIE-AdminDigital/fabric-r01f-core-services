package r01f.core.services.mail.model;

import java.io.Serializable;
import java.util.Collection;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Accessors(prefix="_")
@RequiredArgsConstructor
public class EMailDestinations
  implements Serializable {

	private static final long serialVersionUID = -4981273001945206162L;
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final Collection<EMailRFC822Address> _to;
	@Getter private final Collection<EMailRFC822Address> _cc;
	@Getter private final Collection<EMailRFC822Address> _bcc;


}
