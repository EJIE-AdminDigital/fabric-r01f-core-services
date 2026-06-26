package r01f.core.fileexplorer.command.response;

import java.util.Collection;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallType;

@MarshallType(as="open")
@Accessors(prefix="_")
public class FileExplorerCommandResponseForOpen 
  implements FileExplorerCommandResponseForHasFileStoreItems<FileExplorerCommandResponseForOpen>,
  			 FileExplorerCommandResponseObject {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="api")
	@Getter @Setter private String _api;
	
	@MarshallField(as="cwd")
	@Getter @Setter private FileExplorerFileStoreItem _cwd;
	
	@MarshallField(as="files")
	@Getter @Setter private Collection<FileExplorerFileStoreItem> _files;
	
	@MarshallField(as="options")
	@Getter @Setter private FileExplorerOptions _options;
	
	@MarshallField(as="textMimes")
	@Getter @Setter private String[] _textMimes;
	
	@MarshallField(as="netDrivers")
	@Getter @Setter private String[] _netDrivers;
	
	@MarshallField(as="debug")
	@Getter @Setter private FileExplorerDebug _debug;
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
