package r01f.core.fileexplorer.command.response;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallType;

@MarshallType(as="size")
@Accessors(prefix="_")
public class FileExplorerCommandResponseForSize 
  implements FileExplorerCommandResponseObject {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="size")
	@Getter @Setter private long _size;
	
	@MarshallField(as="sizes")
	@Getter @Setter private Map<String,Long> _sizes;
	
	@MarshallField(as="fileCnt")
	@Getter @Setter private int _fileCnt;
	
	@MarshallField(as="dirCnt")
	@Getter @Setter private int _dirCnt;
}
