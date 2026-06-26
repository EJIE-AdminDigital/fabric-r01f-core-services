package r01f.core.services.notifier;

import java.io.InputStream;
import java.io.Serializable;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Accessors(prefix="_")
@RequiredArgsConstructor
public class NotifierAttachment 
  implements Serializable {
	
	private static final long serialVersionUID = 1255070947234152715L;
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	@Getter private final String _name;
	@Getter private final String _description;
	@Getter private final InputStream _content;
}
