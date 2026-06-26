package r01f.core.fileexplorer.command.response;

import java.util.Collection;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallType;

@MarshallType(as="info")
@Accessors(prefix="_")
public class FileExplorerCommandResponseForInfo 
  implements FileExplorerCommandResponseForHasFileStoreItems<FileExplorerCommandResponseForInfo> {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="files")
	@Getter @Setter private Collection<FileExplorerFileStoreItem> _items;
}
