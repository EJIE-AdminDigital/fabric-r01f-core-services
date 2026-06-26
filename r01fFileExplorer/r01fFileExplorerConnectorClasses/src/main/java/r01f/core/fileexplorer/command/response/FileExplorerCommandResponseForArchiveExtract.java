package r01f.core.fileexplorer.command.response;

import java.util.Collection;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallType;

@MarshallType(as="extract")
@Accessors(prefix="_")
public class FileExplorerCommandResponseForArchiveExtract 
  implements FileExplorerCommandResponseForHasAddedItems<FileExplorerCommandResponseForArchiveExtract> {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="added")
	@Getter @Setter private Collection<FileExplorerFileStoreItem> _added;
}
