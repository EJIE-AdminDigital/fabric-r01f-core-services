package r01f.core.fileexplorer.command.response;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.debug.Debuggable;
import r01f.mime.MimeType;
import r01f.mime.MimeTypes;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

/**
 * (see https://github.com/Studio-42/elFinder/wiki/Client-Server-API-2.1#open)
 * 
 * The information about archivers options:
 *             "archivers"       : {                                  // (Object) Archive settings
 *               "create"  : [
 *                 "application/zip",
 *                 "application/x-tar",
 *                 "application/x-gzip"
 *               ],                                                   // (Array)  List of the mime type of archives which can be created
 *               "extract" : [
 *                 "application/zip",
 *                 "application/x-tar",
 *                 "application/x-gzip"
 *               ],                                                   // (Array)  List of the mime types that can be extracted / unpacked
 *               "createext": {
 *                 "application/zip": "zip",
 *                 "application/x-tar": "tar", 
 *                 "application/x-gzip": "tgz"
 *               }                                                    // (Object)  Map list of { MimeType: FileExtention }
 *             }
 */
@MarshallType(as="archivers")
@Accessors(prefix="_")
public class FileExplorerOptionsForArchivers 
  implements Debuggable {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="create")
	@Getter @Setter private String[] _create;				// current folder path
	
	@MarshallField(as="extract")
	@Getter @Setter private String[] _extract;				// current folder url
	
	@MarshallField(as="createext")
	@Getter @Setter private Map<String,String> _createext;	// Map list of { MimeType: FileExtention }
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////	
	public FileExplorerOptionsForArchivers() {
		// default no-args constructor
	}
	public FileExplorerOptionsForArchivers(final Collection<MimeType> create,
									   	   final Collection<MimeType> extract) {
		_create = _mimeTypeCollectionToStringArray(create);
		_extract = _mimeTypeCollectionToStringArray(extract);
		_createext = _mimeTypeExtensionMap(create,extract);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	private static String[] _mimeTypeCollectionToStringArray(final Collection<MimeType> col) {
		if (CollectionUtils.isNullOrEmpty(col)) return null;
		return col.stream()
				  .map(MimeType::asString)
				  .toArray(String[]::new);
	}
	private static Map<String,String> _mimeTypeExtensionMap(final Collection<MimeType> create,
									   				 		final Collection<MimeType> extract) {
		if (CollectionUtils.isNullOrEmpty(create) && CollectionUtils.isNullOrEmpty(extract)) return null;
		
		Set<MimeType> mimeTypes = Sets.newLinkedHashSet();
		if (CollectionUtils.hasData(create)) mimeTypes.addAll(create);
		if (CollectionUtils.hasData(extract)) mimeTypes.addAll(extract);
		
		return mimeTypes.stream()
						.collect(Collectors.toMap(MimeType::asString,
												  mimeType -> {
														String outExt = null;
														if (mimeType.is(MimeTypes.APPLICATION_ZIP)) {
															outExt = "zip";
														} else if (mimeType.is(MimeTypes.APPLICATION_TAR)) {
															outExt = "tar";
														} else if (mimeType.is(MimeTypes.APPLICATION_GZIP)) {
															outExt = "tgz";
														}
														return outExt;
												  }));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	DEBUG
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CharSequence debugInfo() {
		return Strings.customized("create={} extract={}",
								  CollectionUtils.toStringCommaSeparated(_create),CollectionUtils.toStringCommaSeparated(_extract));
	}	
}
