package r01f.core.fileexplorer.command.response;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.debug.Debuggable;
import r01f.mime.MimeTypes;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallType;

/**
 * (see https://github.com/Studio-42/elFinder/wiki/Client-Server-API-2.1#open)
 * 
 * The information about options:
 *          {
 *           options : {
 *             "path"            : "files/folder42",                        // (String) Current folder path
 *             "url"             : "http://localhost/elfinder/files/",      // (String) Current folder URL
 *             "separator"       : "/",                                     // (String) Path separator for the current volume
 *             
 *             "onetimeUrl"		 : true,									// (Boolean)
 *             
 *             "csscls"			 : ""										// (String)
 *             
 *             "tmbURL"          : "http://localhost/elfinder/files/.tmb/", // (String) Thumbnails folder URL
 *             "tmbCrop"		 : 1,
 *             "tmbReqCustomData": false,									// (Boolean)
 *             
 *             "disabled"        : [],                                      // (Array)  List of commands not allowed (disabled) on this volume
 *             
 *             "copyOverwrite"   : 1,                                       // (Number) Whether or not to overwrite files with the same name on the current volume when copy
 *             "uploadOverwrite" : 1,                                       // (Number) Whether or not to overwrite files with the same name on the current volume when upload
 *             "uploadMaxSize"   : 1073741824,                              // (Number) Upload max file size per file
 *             "uploadMaxConn"   : 3,                                       // (Number) Maximum number of chunked upload connection. `-1` to disable chunked upload
 *             "uploadMime": {                                              // (Object) MIME type checker for upload
 *                 "allow": [ "image", "text/plain" ],                      // (Array) Allowed MIME type
 *                 "deny": [ "all" ],                                       // (Array) Denied MIME type
 *                 "firstOrder": "deny"                                     // (String) First order to check ("deny" or "allow")
 *             },
 *             "dispInlineRegex" : "^(?:image|text/plain$)",                // (String) Regular expression of MIME types that can be displayed inline with the `file` command
 *             "jpgQuality"      : 100,                                     // (Number) JPEG quality to image resize / crop / rotate (1-100)
 *             "substituteImg"	 : true,									// (Boolean)

 *             "syncChkAsTs"     : 1,                                       // (Number) Whether or not to current volume can detect update by the time stamp of the directory
 *             "syncMinMs"       : 30000,                                   // (Number) Minimum inteval Milliseconds for auto sync
 *             
 *             "uiCmdMap"        : { "chmod" : "perm" },                    // (Object) Command conversion map for the current volume (e.g. chmod(ui) to perm(connector))
 *             
 *             "i18nFolderName"  : 1,                                       // (Number) Is enabled i18n folder name that convert name to elFinderInstance.messages['folder_'+name]
 *             
 *             "archivers"       : {                                        // (Object) Archive settings
 *               "create"  : [
 *                  "application\/x-tar",
 *                  "application\/x-gzip",
 *                  "application\/x-bzip2",
 *                  "application\/x-xz",
 *                  "application\/zip",
 *                  "application\/x-7z-compressed"
 *               ],                                                   // (Array)  List of the mime type of archives which can be created
 *               "extract" : [
 *                  "application\/x-tar",
 *                  "application\/x-gzip",
 *                  "application\/x-bzip2",
 *                  "application\/x-xz",
 *                  "application\/zip",
 *                  "application\/x-7z-compressed"
 *               ],                                                   // (Array)  List of the mime types that can be extracted / unpacked
 *               "createext": {
 *                  "application\/x-tar",
 *                  "application\/x-gzip",
 *                  "application\/x-bzip2",
 *                  "application\/x-xz",
 *                  "application\/zip",
 *                  "application\/x-7z-compressed"
 *               }                                                    // (Object)  Map list of { MimeType: FileExtention }
 *             }
 *           }
 *          }		 
 */
@MarshallType(as="options")
@Accessors(prefix="_")
public class FileExplorerOptions 
  implements Debuggable {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="path")
	@Getter @Setter private String _path;				// current folder path
	
	@MarshallField(as="url")
	@Getter @Setter private String _url;				// current folder url
	
	@MarshallField(as="onetimeurl")
	@Getter @Setter private boolean _onetimeUrl = true;
	
	@MarshallField(as="separator")
	@Getter @Setter private String _separator = "/";	// path separator for the current volume

////////// Thumb nails
	@MarshallField(as="tmbURL")
	@Getter @Setter private String _tmbURL;				// thumbnail folder url
	
	@MarshallField(as="tmbCrop")
	@Getter @Setter private int _tmbCrop;	
	
	@MarshallField(as="tmbReqCustomData")
	@Getter @Setter private boolean _tmbReqCustomData;	
	
////////// Security
	@MarshallField(as="disabled")
	@Getter @Setter private String[] _disabled;			// list of commands not allowed (disabled) on this volume

	@MarshallField(as="copyOverwrite")
	@Getter @Setter private int _copyOverwrite = 1;		// Whether or not to overwrite files with the same name on the current volume when copy

////////// Upload
	@MarshallField(as="uploadOverwrite")
	@Getter @Setter private int _uploadOverwrite = 1;	// Whether or not to overwrite files with the same name on the current volume when upload

	@MarshallField(as="uploadMaxSize")
	@Getter @Setter private long _uploadMaxSize;		// Upload max file size per file
	
	@MarshallField(as="uploadMaxConn")
	@Getter @Setter private int _uploadMaxConn;			// Maximum number of chunked upload connection. `-1` to disable chunked upload
	
	@MarshallField(as="uploadMime")
	@Getter @Setter private FileExplorerOptionsForMime _uploadMime;	// MIME type checker for upload
	
////////// UI
	@MarshallField(as="uiCmdMap")
	@Getter @Setter private Map<String,String> _uiCmdMap;// Command conversion map for the current volume (e.g. chmod(ui) to perm(connector))
	
	@MarshallField(as="csscls")
	@Getter @Setter private String _csscls;
	
	@MarshallField(as="dispInlineRegex")
	@Getter @Setter private String _dispInlineRegex = "^(?:image|text/plain$)";		// Regular expression of MIME types that can be displayed 
																					// inline with the `file` command which outputs file contents
																					// into browser (preview)
	
	@MarshallField(as="jpgQuality")
	@Getter @Setter private int _jpgQuality = 100;		// JPEG quality to image resize / crop / rotate (1-100)
	
	@MarshallField(as="substituteImg")
	@Getter @Setter private boolean _substituteImg = true;
	
////////// System
	@MarshallField(as="syncChkAsTs")
	@Getter @Setter private int _syncChkAsTs = 1;		// Whether or not to current volume can detect update by the time stamp of the directory
	
	@MarshallField(as="syncMinMs")
	@Getter @Setter private int _syncMinMs = 30000;		// Minimum interval Milliseconds for auto sync
	
	@MarshallField(as="i18nFolderName")
	@Getter @Setter private int _i18nFolderName = 1;	// Enable i18n folder name that convert name to elFinderInstance.messages['folder_'+name]
	
	@MarshallField(as="archivers")
	@Getter @Setter private FileExplorerOptionsForArchivers _archivers = new FileExplorerOptionsForArchivers(List.of(MimeTypes.APPLICATION_ZIP),
																											 List.of(MimeTypes.APPLICATION_ZIP));
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////	
	public FileExplorerOptions() {
		// default no-args constructor
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	DEBUG
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CharSequence debugInfo() {
		StringBuilder sb = new StringBuilder();
		sb.append("Elfinder options:\n");
		sb.append("-                     path: ").append(_path).append("\n");
		sb.append("-                      url: ").append(_url).append("\n");
		sb.append("-                separator: ").append(_separator).append("\n");
		sb.append("-               thumbs url: ").append(_tmbURL).append("\n");
		sb.append("-            disabled cmds: ").append(_disabled).append("\n");
		sb.append("-          copy overwrites: ").append(_copyOverwrite).append("\n");
		sb.append("-        upload overwrites: ").append(_uploadOverwrite).append("\n");
		sb.append("-          upload max size: ").append(_uploadMaxSize).append("\n");
		sb.append("-  upload max chunked conx: ").append(_uploadMaxConn).append("\n");
		if (_uploadMime != null) sb.append("-upload allowed mime types: ").append(_uploadMime.debugInfo()).append("\n");
		sb.append("-     display inline regex: ").append(_dispInlineRegex).append("\n");
		sb.append("-              jpg quality: ").append(_jpgQuality).append("\n");
		sb.append("-      sync at time-stamps: ").append(_syncChkAsTs).append("\n");
		sb.append("-     sync interval millis: ").append(_syncMinMs).append("\n");
		sb.append("-         i18n folder name: ").append(_i18nFolderName).append("\n");
		if (_archivers != null) sb.append("-                archivers: ").append(_archivers.debugInfo());
		return sb;
	}	
}
