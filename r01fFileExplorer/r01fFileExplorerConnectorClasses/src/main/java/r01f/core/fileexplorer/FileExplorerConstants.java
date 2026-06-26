package r01f.core.fileexplorer;

import java.nio.charset.Charset;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import r01f.mime.MimeTypes;
import r01f.types.AppVersion;
import r01f.util.types.StringEscapeUtils;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class FileExplorerConstants {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	// options
	public static final AppVersion API_VERSION = AppVersion.from("2.1065");

	public static final String CONTENT_TYPE_APPLICATION_JSON_CHARSET_UTF_8 = "application/json; charset=UTF-8";
	
	public static final Charset UTF8_ENCODING = Charset.forName("utf-8");
	
	public static final String DATETIME_FORMAT_STR = "d MMM yyyy HH:mm:ss 'GMT'";
	public static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern(FileExplorerConstants.DATETIME_FORMAT_STR);
	
	public static final String DEFAULT_SUFFIX_FOR_NAME_COLLISION = "~";
/////////////////////////////////////////////////////////////////////////////////////////
//	REQUEST
/////////////////////////////////////////////////////////////////////////////////////////
	public static final String REQ_PARAMETER_CMD = "cmd";
	
	public static final String REQ_PARAMETER_TARGET = "target";
	public static final String REQ_PARAMETER_TARGETS = "targets[]";
	public static final String REQ_PARAMETER_INIT = "init";
	public static final String REQ_PARAMETER_TREE = "tree";
	public static final String REQ_PARAMETER_NAME = "name";
	public static final String REQ_PARAMETER_UNTIL = "until";
	public static final String REQ_PARAMETER_DESTINATION = "dst";
	public static final String REQ_PARAMETER_CUT = "cut";
	public static final String REQ_PARAMETER_SUFFIX = "suffix";
	public static final String REQ_PARAMETER_CONTENT = "content";		// PUT
	public static final String REQ_PARAMETER_SEARCH_QUERY = "q";		// SEARCH by text
	public static final String REQ_PARAMETER_SEARCH_MIMES = "mimes[]";	// SEARCH by mime types
	public static final String REQ_PARAMETER_TYPE = "type";				// Archive mime-type
	public static final String REQ_PARAMETER_MAKEDIR = "makedir";		// Archive extract
	
	public static final String REQ_PARAMETER_UPLOAD_TARGET = "target";
	public static final String REQ_PARAMETER_UPLOAD_FILES = "upload[]";	
	public static final String REQ_PARAMETER_UPLOAD_PATHS = "upload_path[]";
	public static final String REQ_PARAMETER_UPLOAD_NAMES = "name[]";
	public static final String REQ_PARAMETER_UPLOAD_TIMESTAMPS = "mtime[]";
	public static final String REQ_PARAMETER_UPLOAD_OVERWRITE = "overwrite";
	public static final String REQ_PARAMETER_UPLOAD_RANGE = "range";
	public static final String REQ_PARAMETER_UPLOAD_CHUNK = "chunk";
	public static final String REQ_PARAMETER_UPLOAD_CHUNK_ID = "cid";
	public static final String REQ_PARAMETER_UPLOAD_REQID = "reqid";
	
	public static final String REQ_PARAMETER_BUSINESS_OBJ_OID = "businessObjOid";
	public static final String REQ_PARAMETER_BUSINESS_OBJ_NAME = "businessObjShortName";
	public static final String REQ_PARAMETER_BUSINESS_OBJ_TARGET_PATH = "targetPath";
	
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	public static final String[] DEFAULT_TEXT_MIMES = Stream.of(new CharSequence[] {
																	StringEscapeUtils.escapeJSON(MimeTypes.HTML.asString()),
																	StringEscapeUtils.escapeJSON(MimeTypes.XHTML.asString()),
																	StringEscapeUtils.escapeJSON(MimeTypes.JAVASCRIPT.asString()),
																	StringEscapeUtils.escapeJSON(MimeTypes.STYLESHEET.asString()),
																	StringEscapeUtils.escapeJSON(MimeTypes.APPLICATION_XML.asString()),
																	StringEscapeUtils.escapeJSON(MimeTypes.TEXT_PLAIN.asString()),
																	StringEscapeUtils.escapeJSON(MimeTypes.APPLICATION_JSON.asString())
																})
															.map(CharSequence::toString)
															.toArray(String[]::new);
}
