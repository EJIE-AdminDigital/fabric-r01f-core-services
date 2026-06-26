package r01f.core.fileexplorer.command.response;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.core.fileexplorer.FileExplorerItemPathHash;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.url.Url;

@MarshallType(as="tmb")
@Accessors(prefix="_")
public class FileExplorerCommandResponseForTmb 
  implements FileExplorerCommandResponseObject {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="images")
	@Getter @Setter private Map<String,String> _list;
	
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public void addImageThumb(final FileExplorerItemPathHash imgPathHash,final Url thumbUrl) {
		if (_list == null) _list = new HashMap<>();
		_list.put(imgPathHash.asString(), 
				  thumbUrl.asString());
	}
}
