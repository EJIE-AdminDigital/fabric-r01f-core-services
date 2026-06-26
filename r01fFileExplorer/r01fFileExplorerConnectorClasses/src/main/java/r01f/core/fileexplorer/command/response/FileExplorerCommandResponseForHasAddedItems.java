package r01f.core.fileexplorer.command.response;

import java.util.ArrayList;
import java.util.Collection;

public interface FileExplorerCommandResponseForHasAddedItems<SELF_TYPE extends FileExplorerCommandResponseForHasAddedItems<SELF_TYPE>> 
		 extends FileExplorerCommandResponseForHasFileStoreItems<SELF_TYPE> {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	public Collection<FileExplorerFileStoreItem> getAdded();
	public void setAdded(final Collection<FileExplorerFileStoreItem> added);
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unchecked")
	public default SELF_TYPE addAddedItem(final FileExplorerFileStoreItem item) {
		if (item == null) return (SELF_TYPE)this;
		if (this.getAdded() == null) this.setAdded(new ArrayList<>());
		this.getAdded()
			.add(item);
		
		return (SELF_TYPE)this;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public default Collection<FileExplorerFileStoreItem> getItems() {
		return this.getAdded();
	}
	@Override
	public default void setItems(final Collection<FileExplorerFileStoreItem> items) {
		this.setAdded(items);
	}
}
