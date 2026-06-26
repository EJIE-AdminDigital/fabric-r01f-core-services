package r01f.core.fileexplorer.command.response;

import java.util.Collection;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallType;

@MarshallType(as="parents")
@Accessors(prefix="_")
public class FileExplorerCommandResponseForParents 
  implements FileExplorerCommandResponseForHasFileStoreItems<FileExplorerCommandResponseForParents>,
  			 FileExplorerCommandResponseObject {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="tree")
	@Getter @Setter private Collection<FileExplorerFileStoreItem> _tree;
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Collection<FileExplorerFileStoreItem> getItems() {
		return _tree;
	}
	@Override
	public void setItems(final Collection<FileExplorerFileStoreItem> items) {
		_tree = items;
	}
}
