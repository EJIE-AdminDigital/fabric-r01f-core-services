package r01f.core.fileexplorer.command.response;

import java.util.Collection;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallType;

@MarshallType(as="search")
@Accessors(prefix="_")
public class FileExplorerCommandResponseForSearch 
  implements FileExplorerCommandResponseForHasFileStoreItems<FileExplorerCommandResponseForSearch>,
  			 FileExplorerCommandResponseObject {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="files")
	@Getter @Setter private Collection<FileExplorerFileStoreItem> _files;
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Collection<FileExplorerFileStoreItem> getItems() {
		return _files;
	}
	@Override
	public void setItems(final Collection<FileExplorerFileStoreItem> items) {
		_files = items;
	}
}
