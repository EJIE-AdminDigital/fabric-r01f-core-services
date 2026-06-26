package r01f.core.fileexplorer.command.response;

import java.util.ArrayList;
import java.util.Collection;

import r01f.core.fileexplorer.FileExplorerItemPathHash;
import r01f.util.types.collections.CollectionUtils;

public interface FileExplorerCommandResponseForHasRemovedItems<SELF_TYPE extends FileExplorerCommandResponseForHasRemovedItems<SELF_TYPE>> 
		 extends FileExplorerCommandResponseObject {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	public Collection<String> getRemoved();
	public void setRemoved(final Collection<String> removed);
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public default void setRemovedItemsPathHashes(final Collection<FileExplorerItemPathHash> itemsPathHashes) {
		if (CollectionUtils.isNullOrEmpty(itemsPathHashes)) return;
		
		itemsPathHashes.forEach(itemPathHash -> this.addRemovedItem(itemPathHash));
	}
	@SuppressWarnings("unchecked")
	public default SELF_TYPE addRemovedItem(final FileExplorerItemPathHash itemPathHash) {
		if (itemPathHash == null) return (SELF_TYPE)this;
		if (this.getRemoved() == null) this.setRemoved(new ArrayList<>());
		this.getRemoved()
			.add(itemPathHash.asString());
		
		return (SELF_TYPE)this;
	}
}
