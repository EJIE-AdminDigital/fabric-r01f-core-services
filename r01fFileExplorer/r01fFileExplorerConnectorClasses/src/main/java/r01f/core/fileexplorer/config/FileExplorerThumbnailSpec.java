package r01f.core.fileexplorer.config;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallType;

@MarshallType(as="thumbnail")
@Accessors(prefix="_")
public class FileExplorerThumbnailSpec {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	@MarshallField(as="width")
	@Getter @Setter protected int _width;
	
	@MarshallField(as="height")
	@Getter @Setter protected int _height;
}