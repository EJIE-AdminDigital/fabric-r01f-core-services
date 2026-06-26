package r01f.core.fileexplorer.command.response;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.core.fileexplorer.FileExplorerStorage;
import r01f.debug.Debuggable;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

/**
 * (see https://github.com/Studio-42/elFinder/wiki/Client-Server-API-2.1#open)
 * 
 * The information about debug:
 *              {
 *               "debug":{
 *                 "connector":"php",                     // (String) Connector type
 *                 "phpver"   : "5.3.6",                  // (String) php version
 *                 "time"     : 0.0749430656433,          // (Number) Execution time
 *                 "memory"   : "3348Kb / 2507Kb / 128M", // (String) Used / Free / Available Memory
 *                 "volumes"  : [                         // (Array)  Debugging by volume
 *                   {
 *                     "id"         : "l1_",              // (String) ID of the Volume
 *                     "driver"     : "localfilesystem",  // (String) Driver type (class name)
 *                     "mimeDetect" : "internal",         // (String) Method for determining mime type
 *                     "imgLib"     : "gd"                // (String) Library for working with images
 *                   }
 *              
 *                 ],
 *                 "mountErrors" : [                      // (Array) List of errors for not mounted volumes
 *                   0 : "Root folder has not read and write permissions."
 *                 ]
 *               }
 *              }		 
 */
@MarshallType(as="debug")
@Accessors(prefix="_")
public class FileExplorerDebug 
  implements Debuggable {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="connector")
	@Getter @Setter private String _conector = "java";
	
	@MarshallField(as="time")
	@Getter @Setter private long _time = System.currentTimeMillis();
	
	@MarshallField(as="memory")
	@Getter @Setter private String _memory;
	
	@MarshallField(as="volumes")
	@Getter @Setter private Collection<FileExplorerDebugForVolume> _volumes;
	
	@MarshallField(as="mountErrors")
	@Getter @Setter private Map<Integer,String> _mountErrors;

/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////	
	public FileExplorerDebug() {
		// memory data
		Runtime runtime = Runtime.getRuntime();
		long totalMemoryBytes = runtime.totalMemory();
		long freeMemoryBytes = runtime.freeMemory();
		long usedMemoryBytes = totalMemoryBytes - freeMemoryBytes;
		
		_memory = Strings.customized("{}Kb / {}Kb / {}Kb",
									 usedMemoryBytes / 1024L,
									 freeMemoryBytes / 1024L,
									 totalMemoryBytes / 1024L);
	}
	public FileExplorerDebug(final FileExplorerStorage storage) {
		this();
		_volumes = CollectionUtils.hasData(storage.getVolumes())
						? storage.getVolumes()
								 .stream()
								 .map(FileExplorerDebugForVolume::new)
								 .toList()
						: null;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	DEBUG
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CharSequence debugInfo() {
		StringBuilder sb = new StringBuilder();
		sb.append("Debug:\n");
		sb.append("-conector: ").append(_conector).append("\n");
		sb.append("-    time: ").append(_time).append("\n");
		sb.append("-  memory: ").append(_memory).append("\n");
		sb.append(" -volumes:\n");
		if (CollectionUtils.hasData(_volumes)) {
			for (Iterator<FileExplorerDebugForVolume> vIt = _volumes.iterator(); vIt.hasNext(); ) {
				sb.append("\t-").append(vIt.next().debugInfo()).append("\n");
			}
		}
		return sb;
	}	
}
