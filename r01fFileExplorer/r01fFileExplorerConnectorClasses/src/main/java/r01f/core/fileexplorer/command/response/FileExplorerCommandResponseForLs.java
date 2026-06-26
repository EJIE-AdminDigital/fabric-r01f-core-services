package r01f.core.fileexplorer.command.response;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallType;

@MarshallType(as="ls")
@Accessors(prefix="_")
public class FileExplorerCommandResponseForLs 
  implements FileExplorerCommandResponseObject {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="list")
	@Getter @Setter private Map<String,String> _list;
}
