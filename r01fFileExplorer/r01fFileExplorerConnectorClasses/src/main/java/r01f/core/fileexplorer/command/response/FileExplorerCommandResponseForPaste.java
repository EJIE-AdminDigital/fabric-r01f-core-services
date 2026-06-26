package r01f.core.fileexplorer.command.response;

import java.util.Collection;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallType;

@MarshallType(as="paste")
@Accessors(prefix="_")
public class FileExplorerCommandResponseForPaste 
  implements FileExplorerCommandResponseForHasAddedItems<FileExplorerCommandResponseForPaste>,
  			 FileExplorerCommandResponseForHasRemovedItems<FileExplorerCommandResponseForPaste> {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="added")
	@Getter @Setter private Collection<FileExplorerFileStoreItem> _added;
	
	@MarshallField(as="removed")
	@Getter @Setter private Collection<String> _removed;
}
