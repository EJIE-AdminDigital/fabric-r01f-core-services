package r01f.core.fileexplorer.command.response;

import java.util.Collection;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallType;

@MarshallType(as="put")
@Accessors(prefix="_")
public class FileExplorerCommandResponseForPut 
  implements FileExplorerCommandResponseForHasChangedItems<FileExplorerCommandResponseForPut> {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="changed")
	@Getter @Setter private Collection<FileExplorerFileStoreItem> _changed;
}
