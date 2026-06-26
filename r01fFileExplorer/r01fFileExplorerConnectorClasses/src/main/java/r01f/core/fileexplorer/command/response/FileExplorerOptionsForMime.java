package r01f.core.fileexplorer.command.response;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.debug.Debuggable;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.util.types.Strings;

/**
 * (see https://github.com/Studio-42/elFinder/wiki/Client-Server-API-2.1#open)
 * 
 * The information about allowed mime options:
 *             "uploadMime": {                                              // (Object) MIME type checker for upload
 *                 "allow": [ "image", "text/plain" ],                      // (Array) Allowed MIME type
 *                 "deny": [ "all" ],                                       // (Array) Denied MIME type
 *                 "firstOrder": "deny"                                     // (String) First order to check ("deny" or "allow")
 *             }
 */
@MarshallType(as="uploadMime")
@Accessors(prefix="_")
public class FileExplorerOptionsForMime 
  implements Debuggable {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	public static String ALL = "all";
	
	public static String ALLOW_FIRST = "allow";
	public static String DENY_FIRST = "deny";
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS	
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="allow")
	@Getter @Setter private String[] _allow;			// Allowed MIME type
	
	@MarshallField(as="deny")
	@Getter @Setter private String[] _deny;				// Denied MIME type
	
	@MarshallField(as="firstOrder")
	@Getter @Setter private String _firstOrder = DENY_FIRST;	// irst order to check ("deny" or "allow")
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////	
	public FileExplorerOptionsForMime() {
		// default no-args constructor
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	DEBUG
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CharSequence debugInfo() {
		return Strings.customized("allow={} deny={} first={}",
								  _allow,_deny,_firstOrder);
	}	
}
