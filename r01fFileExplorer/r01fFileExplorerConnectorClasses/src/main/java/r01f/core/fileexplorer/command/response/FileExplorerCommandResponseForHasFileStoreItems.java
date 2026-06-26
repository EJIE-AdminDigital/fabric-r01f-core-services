package r01f.core.fileexplorer.command.response;

import java.util.ArrayList;
import java.util.Collection;

public interface FileExplorerCommandResponseForHasFileStoreItems<SELF_TYPE extends FileExplorerCommandResponseForHasFileStoreItems<SELF_TYPE>> 
		 extends FileExplorerCommandResponseObject {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	public Collection<FileExplorerFileStoreItem> getItems();
	public void setItems(final Collection<FileExplorerFileStoreItem> items);
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unchecked")
	public default SELF_TYPE addItem(final FileExplorerFileStoreItem item) {
		if (item == null) return (SELF_TYPE)this;
		if (this.getItems() == null) this.setItems(new ArrayList<>());
		this.getItems()
			.add(item);
		
		return (SELF_TYPE)this;
	}
}
