package r01f.core.fileexplorer.config;

import java.util.regex.Pattern;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.debug.Debuggable;
import r01f.filestore.api.FileStoreType;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.patterns.Memoized;
import r01f.types.Path;
import r01f.util.types.StringEncodeUtils;
import r01f.util.types.Strings;
import r01f.validation.ObjectValidationResult;
import r01f.validation.ObjectValidationResultBuilder;
import r01f.validation.SelfValidates;

@MarshallType(as="volume")
@Accessors(prefix="_")
public class FileExplorerVolumeSpec
  implements SelfValidates<FileExplorerVolumeSpec>,
  			 Debuggable {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRAINT
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="storeType",
			  	   whenXml=@MarshallFieldAsXml(attr = true))
	@Getter @Setter protected FileStoreType _storeType;
	
	@MarshallField(as="name",
			  	   whenXml=@MarshallFieldAsXml(attr = true))
	@Getter @Setter protected String _name;
	
	@MarshallField(as="alias",
			  	   whenXml=@MarshallFieldAsXml(attr = true))
	@Getter @Setter protected String _alias;
	
	@MarshallField(as="tmpDir",
			  	   whenXml=@MarshallFieldAsXml(attr = true))
	@Getter @Setter protected String _tmpDir;
	
	@MarshallField(as="source",
				   whenXml=@MarshallFieldAsXml(attr = true))
	@Getter @Setter protected String _source;
	
	@MarshallField(as="relPathFromStoreRoot",
				   whenXml=@MarshallFieldAsXml(attr = true))
	@Getter @Setter protected Path _path;		// relative from the store root path
	
	@MarshallField(as="default",
				   whenXml=@MarshallFieldAsXml(attr = true))
	@Getter @Setter protected boolean _default;
	
	@MarshallField(as="security")
	@Getter @Setter protected FileExplorerVolumeSecuritySpec _security;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public FileExplorerVolumeSpec() {
		// no-args constructor
	}
	public FileExplorerVolumeSpec(final FileStoreType fsType,
								  final String name,
								  final String alias,
								  final String tmpDir,
								  final String source) {
		this(fsType,
			 name,
			 alias,
			 tmpDir,
			 source,
			 null,	// no rel path from store root
			 null);	// nosecurity
	}
	public FileExplorerVolumeSpec(final FileStoreType fsType,
								  final String name,
								  final String alias,
								  final String tmpDir,
								  final String source,
								  final FileExplorerVolumeSecuritySpec secSpec) {
		this(fsType,
			 name,
			 alias,
			 tmpDir,
			 source,
			 null,		// no rel path from store root
			 secSpec);
	}
	public FileExplorerVolumeSpec(final FileStoreType fsType,
								  final String name,
								  final String alias,
								  final String tmpDir,
								  final String source,
								  final Path path) {
		this(fsType,
			 name,
			 alias,
			 tmpDir,
			 source,
			 path,
			 null);	// nosecurity
	}
	public FileExplorerVolumeSpec(final FileStoreType fsType,
								  final String name,
								  final String alias,
								  final String tmpDir,
								  final String source,
								  final Path path,
								  final FileExplorerVolumeSecuritySpec secSpec) {
		this(fsType,
			 name,
			 alias,
			 tmpDir,
			 source,
			 path,
			 true,
			 secSpec);
	}
	public FileExplorerVolumeSpec(final FileStoreType fsType,
								  final String name,
								  final String alias,
								  final String tmpDir,
								  final String source,
								  final Path path,
								  final boolean isDefault,
								  final FileExplorerVolumeSecuritySpec secSpec) {
		_storeType = fsType;
		_name =  FileExplorerVolumeSpec.sanitizeVolumeName(name);
		_alias = FileExplorerVolumeSpec.sanitizeVolumeAlias(alias);
		_tmpDir = tmpDir;
		_source = source;
		_path = path;
		_default = isDefault;
		_security = secSpec;
	}
	/**
	 * Clone with a new alias and new root path 
	 * @param alias
	 * @param path
	 * @return
	 */
	public FileExplorerVolumeSpec cloneWith(final String alias,
											final Path path) {
		String theAlias = Strings.isNOTNullOrEmpty(alias) ? alias : _alias;
		return new FileExplorerVolumeSpec(this.getStoreType(),
										  this.getName(),
										  theAlias,
										  this.getTmpDir(),
										  this.getSource(),
										  path,
										  this.isDefault(),
										  this.getSecurity());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	SECURITY
/////////////////////////////////////////////////////////////////////////////////////////
	// regex that matches any character that
	// occurs zero or more times (finds any character sequence)
	private static final String FILE_EXPLORER_VOLUME_SERCURITY_REGEX = ".*";
	
	private final Memoized<Pattern> _securityPattern = Memoized.using(() -> Pattern.compile(Strings.customized("{}_{}",
																											   this.getAlias(),FILE_EXPLORER_VOLUME_SERCURITY_REGEX)));
	
	public Pattern getSecurityPattern() {
		return _securityPattern.get();
	}
	public boolean isLocked() {
		return _security != null ? _security.isLocked()
								 : true;
	}
	public boolean isReadable() {
		return _security != null ? _security.isReadable()
								 : true;
	}
	public boolean isWritable() {
		return _security != null ? _security.isWritable()
								 : false;
	}
	public boolean isReadOnlyFile(final Path path) {
		return _security != null ? _security.isReadOnlyFile(path)
								 : true;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	VALIDATION
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public ObjectValidationResult<FileExplorerVolumeSpec> validate() {
		if (_storeType == null) return ObjectValidationResultBuilder.on(this)
																	.isNotValidBecause("[File Explorer] [store] type cannot be null");
		if (Strings.isNullOrEmpty(_source)) return ObjectValidationResultBuilder.on(this)
																			    .isNotValidBecause("[File Explorer] volume [source] cannot be null");
		if (Strings.isNullOrEmpty(_alias)) return ObjectValidationResultBuilder.on(this)
																			    .isNotValidBecause("[File Explorer] volume [alias] cannot be null");
		return ObjectValidationResultBuilder.on(this)
											.isValid();
	}
	public boolean isValid() {
		return this.validate()
				   .isValid();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	UTIL
/////////////////////////////////////////////////////////////////////////////////////////
	public static String sanitizeVolumeAlias(final String name) {
		if (Strings.isNullOrEmpty(name)) return "vol";
		
		CharSequence outName = name.trim();
		if (outName.length() > 20) outName = outName.subSequence(0,19) + "...";
		outName = StringEncodeUtils.replaceAccentedCharsByNonAccentedEquivalents(outName);
		
		return outName.toString();
	}
	public static String sanitizeVolumeName(final String name) {
		if (Strings.isNullOrEmpty(name)) return null;
		if (name.endsWith("_"))  return name;
		return name + "_";
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	DEBUG
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public CharSequence debugInfo() {
		StringBuilder sb = new StringBuilder();
		sb.append(Strings.customized("Volume: storeType={} source={} name={} alias={} rootPath={} default={}\n",
									 this.getStoreType(),this.getSource(),this.getName(),this.getAlias(),this.getPath(),
									 this.isDefault()));
		if (this.getSecurity() != null) {
			sb.append(this.getSecurity().debugInfo());
		} else {
			sb.append(FileExplorerVolumeSecuritySpec.DEFAULT.debugInfo());
		}
		return sb;
	}
}