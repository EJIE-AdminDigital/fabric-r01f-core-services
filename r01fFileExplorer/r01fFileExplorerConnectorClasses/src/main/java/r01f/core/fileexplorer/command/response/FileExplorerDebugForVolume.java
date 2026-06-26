package r01f.core.fileexplorer.command.response;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.core.fileexplorer.FileExplorerVolume;
import r01f.debug.Debuggable;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.util.types.Strings;

/**
 * (see https://github.com/Studio-42/elFinder/wiki/Client-Server-API-2.1#open)
 * 
 * The information about debug:
 *                 "volumes"  : [                         // (Array)  Debugging by volume
 *                   {
 *                     "id"         : "l1_",              // (String) ID of the Volume
 *                     "driver"     : "localfilesystem",  // (String) Driver type (class name)
 *                     "mimeDetect" : "internal",         // (String) Method for determining mime type
 *                     "imgLib"     : "gd"                // (String) Library for working with images
 *                   }
 *                 ],
 */
@MarshallType(as="volume")
@Accessors(prefix="_")
public class FileExplorerDebugForVolume 
  implements Debuggable {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="id")
	@Getter @Setter private String _id;
	
	@MarshallField(as="driver")
	@Getter @Setter private String _driver;
	
	@MarshallField(as="mimeDetect")
	@Getter @Setter private String _mimeDetect = "internal";
	
	@MarshallField(as="imgLib")
	@Getter @Setter private String _imgLib = "java";

/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////	
	public FileExplorerDebugForVolume() {
		// default no-args constructor
	}
	public FileExplorerDebugForVolume(final FileExplorerVolume vol) {
		_id = vol.getId().asString();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	DEBUG
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CharSequence debugInfo() {
		return Strings.customized("id={} driver={} mimeDetect={} imgLib={}",
								  _id,_driver,_mimeDetect,_imgLib);
	}	
}
