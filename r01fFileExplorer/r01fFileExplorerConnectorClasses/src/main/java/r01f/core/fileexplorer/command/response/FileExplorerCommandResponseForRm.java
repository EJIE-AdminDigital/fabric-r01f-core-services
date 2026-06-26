package r01f.core.fileexplorer.command.response;

import java.util.Collection;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallType;

@MarshallType(as="rm")
@Accessors(prefix="_")
public class FileExplorerCommandResponseForRm 
  implements FileExplorerCommandResponseForHasRemovedItems<FileExplorerCommandResponseForRm> {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="removed")
	@Getter @Setter private Collection<String> _removed;
}
