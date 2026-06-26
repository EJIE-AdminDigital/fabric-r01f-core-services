package r01f.core.fileexplorer.command.response;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.util.types.Strings;

@MarshallType(as="dim")
@Accessors(prefix="_")
public class FileExplorerCommandResponseForDim 
  implements FileExplorerCommandResponseObject {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="dim")
	@Getter @Setter private String _dim;
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public void setDim(final int width,final int height) {
		_dim = Strings.customized("{}x{}",
						 		  width,height);
	}
}
