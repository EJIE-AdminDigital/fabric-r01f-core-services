package r01f.core.fileexplorer.command.response;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallType;

@MarshallType(as="get")
@Accessors(prefix="_")
public class FileExplorerCommandResponseForGet 
  implements FileExplorerCommandResponseObject {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="content")
	@Getter @Setter private String _content;
}
