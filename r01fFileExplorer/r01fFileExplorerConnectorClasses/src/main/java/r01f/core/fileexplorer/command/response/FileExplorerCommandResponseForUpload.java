package r01f.core.fileexplorer.command.response;

import java.util.Collection;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallType;

@MarshallType(as="upload")
@Accessors(prefix="_")
public class FileExplorerCommandResponseForUpload 
  implements FileExplorerCommandResponseForHasAddedItems<FileExplorerCommandResponseForUpload> {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="added")
	@Getter @Setter private Collection<FileExplorerFileStoreItem> _added;
	
	@MarshallField(as="_chunkmerged")
	@Getter @Setter private String _uploadedFileNameAtServer;	// if multi-part (multiple chunks) only at last chunk
	
	@MarshallField(as="_name")
	@Getter @Setter private String _uploadedFileName;	// if multi-part (multiple chunks) only at last chunk
}
