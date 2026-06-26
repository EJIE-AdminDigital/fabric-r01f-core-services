package r01f.core.fileexplorer.command.response;

import java.util.ArrayList;
import java.util.Collection;

public interface FileExplorerCommandResponseForHasChangedItems<SELF_TYPE extends FileExplorerCommandResponseForHasChangedItems<SELF_TYPE>> 
		 extends FileExplorerCommandResponseObject {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	public Collection<FileExplorerFileStoreItem> getChanged();
	public void setChanged(final Collection<FileExplorerFileStoreItem> changed);
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unchecked")
	public default SELF_TYPE addChangedItem(final FileExplorerFileStoreItem item) {
		if (item == null) return (SELF_TYPE)this;
		if (this.getChanged() == null) this.setChanged(new ArrayList<>());
		this.getChanged()
			.add(item);
		
		return (SELF_TYPE)this;
	}
}
