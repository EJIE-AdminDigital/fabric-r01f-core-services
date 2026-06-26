package r01f.core.fileexplorer.command.response;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallType;

@MarshallType(as="error")
@Accessors(prefix="_")
public class FileExplorerCommandResponseForError 
  implements FileExplorerCommandResponseObject {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="error")
	@Getter @Setter private String _error;
}
