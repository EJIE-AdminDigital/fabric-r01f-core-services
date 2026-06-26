package r01f.core.fileexplorer;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.apache.commons.codec.binary.Base64;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.crypto.Hash;
import r01f.file.FileProperties;
import r01f.types.CanBeRepresentedAsString;
import r01f.types.Path;
import r01f.util.types.Objects;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

/**
 * See: https://github.com/Studio-42/elFinder/wiki/Client-Server-API-2.1
 * 
 * Connector uses the following algorithm to create hash from file path:
 *		1. remove root path from file path
 *		2. encrypt resulting path so it could be later decrypted (not implemented yet, but stub is present)
 *		3. encode already encrypted path using base64 with replacement +/= -> -_.
 *		4. remove trailing dots
 *		5. add prefix - unique volume id (must start with [a-z])
 * Resulting string must be valid HTML id attribute (that is why base64 is used).
 * 
 * Using this algorithm even without encryption, client cannot get real file paths on the server only relative to root paths. 
 * 
 * This hash algorithm is recommended but you can use your own implementation as long as it matches these 2 rules:
 * 		- hash must be valid to be stored in the id attribute of an HTML tag
 *		- hash must be reversible by connector
 */
@Accessors(prefix="_")
public class FileExplorerItemPathHash 
  implements CanBeRepresentedAsString {
	
	private static final long serialVersionUID = -2602750976086369663L;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private FileExplorerVolumeID _volumeId;
	@Getter private Hash _relativePathFromRootHash;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public FileExplorerItemPathHash(final FileExplorerVolumeID volId,final Hash relPathHash) {
		_volumeId = volId;
		_relativePathFromRootHash = relPathHash;
	}
	public static FileExplorerItemPathHash from(final FileExplorerVolumeID volId,final Hash relPathHash) {
		return new FileExplorerItemPathHash(volId,relPathHash);
	}
	public static FileExplorerItemPathHash from(final FileExplorerVolumeID volId,final Path relPath) {
		Hash relPathHash = FileExplorerItemPathHash.relativePathFromRootHashOf(relPath);
		return new FileExplorerItemPathHash(volId,relPathHash);
	}
	public static FileExplorerItemPathHash from(final FileExplorerVolume vol,final Path relPath) {
		return FileExplorerItemPathHash.from(vol.getId(),relPath);
	}
	public static FileExplorerItemPathHash from(final FileExplorerVolume vol,final FileProperties item) {
		return vol.itemPathHashOf(item.getPath());
	}
	public static FileExplorerItemPathHash from(final HttpServletRequest req) {
		return FileExplorerItemPathHash.from(req,FileExplorerConstants.REQ_PARAMETER_TARGET);
	}
	public static FileExplorerItemPathHash from(final HttpServletRequest req,final String reqParamName) {
		String pathHashStr = req.getParameter(reqParamName);
		FileExplorerItemPathHash pathHash = Strings.isNOTNullOrEmpty(pathHashStr) ? FileExplorerItemPathHash.from(pathHashStr) : null;
		if (pathHash == null) throw new IllegalArgumentException("The http request does NOT include a parameter named " + reqParamName + " with the path hash!");
		return pathHash;
	}
	public static Collection<FileExplorerItemPathHash> multipleFrom(final HttpServletRequest req) {
		String[] pathsHahesStrs = req.getParameterValues(FileExplorerConstants.REQ_PARAMETER_TARGETS);
		Collection<FileExplorerItemPathHash> pathsHashes = CollectionUtils.hasData(pathsHahesStrs)
														? Stream.of(pathsHahesStrs)
																.map(pathHashStr -> FileExplorerItemPathHash.from(pathHashStr))
																.toList()
														: null;
		if (pathsHashes == null) throw new IllegalArgumentException("The http request does NOT include a parameter named " + FileExplorerConstants.REQ_PARAMETER_TARGETS + " with the paths hashes!");
		return pathsHashes;
	}
	public static FileExplorerItemPathHash from(final String pathHashStr) {
		// Path hash is built as:
		//		{volumeId}_{localHash} 
		// where {localHash} is a hash created from the relative path from volume root
		// of a given path
		String volIdStr = null;
		String localHashStr = null;
		Matcher m = HASH_PATTERN.matcher(pathHashStr);
		if (m.find()) {
			volIdStr = m.group(1);
			localHashStr = m.group(2);
		} else {
			throw new IllegalStateException("The path hash =" + pathHashStr + " is NOT valid: it does NOT match " + HASH_PATTERN.pattern());
		}
		if (volIdStr.length() > 1) throw new IllegalStateException("The volumeId=" + volIdStr + " is NOT valid!!");
		
		FileExplorerVolumeID volId = Strings.isNOTNullOrEmpty(volIdStr) ? FileExplorerVolumeID.from(volIdStr.charAt(0)) : null;
		Hash localHash = Strings.isNOTNullOrEmpty(localHashStr) ? Hash.from(localHashStr) : null;
		
		return FileExplorerItemPathHash.from(volId,localHash);
	}
	private static final Pattern HASH_PATTERN = Pattern.compile("([^_]+)_(.*)");
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public Path getRelativePathFromRoot() {
		return FileExplorerItemPathHash.relativePathFromRootFrom(_relativePathFromRootHash);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	private static final String[][] ESCAPES = {{"+","_P"}, 
											   {"-","_M"}, 
											   {"/","_S"}, 
											   {".","_D"}, 
											   {"=","_E"}};
	public static Hash relativePathFromRootHashOf(final Path relativePath) {
		String relativePathHashStr = new String(Base64.encodeBase64(relativePath.asRelativeString().getBytes()));
		for (String[] pair : ESCAPES) {
			relativePathHashStr = relativePathHashStr.replace(pair[0], pair[1]);
		}
		return Hash.from(relativePathHashStr);
	}
	public static Path relativePathFromRootFrom(final Hash localHash) {
		String localHashStr = localHash != null ? localHash.asString() : null;
		String relativePathStr = null;
		if (localHashStr != null) {
			for (String[] pair : ESCAPES) {
				localHashStr = localHashStr.replace(pair[1], pair[0]);
			}
			relativePathStr = new String(Base64.decodeBase64(localHashStr));
		} else {
			relativePathStr = "";
		}
		return Path.from(relativePathStr);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	CanBeConvertedAsString
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public String asString() {
		return Strings.customized("{}{}",
								  _volumeId,_relativePathFromRootHash != null ? _relativePathFromRootHash : "");
	}
	@Override
	public String toString() {
		return this.asString();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean is(final FileExplorerItemPathHash other) {
		boolean idEqs = Objects.areEqual(this.getVolumeId(),other.getVolumeId(),
										 FileExplorerVolumeID::is);
		boolean hashEqs = Objects.areEqual(this.getRelativePathFromRootHash(),other.getRelativePathFromRootHash(),
										   Hash::is);
		return idEqs && hashEqs;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EQUALS & HASHCODE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean equals(final Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		
		if (!(obj instanceof FileExplorerItemPathHash)) return false;
		
		FileExplorerItemPathHash other = (FileExplorerItemPathHash)obj;
		return this.is(other);
	}
}
