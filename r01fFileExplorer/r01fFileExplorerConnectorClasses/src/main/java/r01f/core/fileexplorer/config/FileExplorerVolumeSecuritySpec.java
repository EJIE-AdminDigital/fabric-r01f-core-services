package r01f.core.fileexplorer.config;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.debug.Debuggable;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.patterns.Memoized;
import r01f.types.Path;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;
import r01f.util.types.collections.Lists;

@MarshallType(as="constraint")
@Accessors(prefix="_")
@Builder
public class FileExplorerVolumeSecuritySpec 
  implements Debuggable {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	@MarshallField(as="locked",
				   whenXml=@MarshallFieldAsXml(attr = true))
	@Getter @Setter protected boolean _locked;
	
	@MarshallField(as="readable",
				   whenXml=@MarshallFieldAsXml(attr = true))
	@Getter @Setter protected boolean _readable;
	
	@MarshallField(as="writable",
				   whenXml=@MarshallFieldAsXml(attr = true))
	@Getter @Setter protected boolean _writable;
	
	@MarshallField(as="read-only-files",
				   whenXml = @MarshallFieldAsXml(collectionElementName = "file-pattern"))
	@Getter @Setter protected Collection<String> _readOnlyFilesPatterns;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public FileExplorerVolumeSecuritySpec() {
		// default no-args constructor
	}
	public FileExplorerVolumeSecuritySpec(final boolean locked,final boolean readable,final boolean writable) {
		_locked = locked;
		_readable = readable;
		_writable = writable;
	}
	public FileExplorerVolumeSecuritySpec(final boolean locked,final boolean readable,final boolean writable,
									  	  final Collection<String> readOnlyFilesPatterns) {
		this(locked,readable,writable);
		_readOnlyFilesPatterns = readOnlyFilesPatterns;
	}
	public FileExplorerVolumeSecuritySpec(final boolean locked,final boolean readable,final boolean writable,
									  	  final String... readOnlyFilesPatterns) {
		this(locked,readable,writable);
		_readOnlyFilesPatterns = Lists.newArrayList(readOnlyFilesPatterns);
	}
	public static final FileExplorerVolumeSecuritySpec DEFAULT = new FileExplorerVolumeSecuritySpec(true,	// locked
																									true,	// readable
																									false);	// writable
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	private final Memoized<Collection<Pattern>> _readOnlyFilesPatternsMemo = Memoized.using(() -> {
																								return CollectionUtils.hasData(_readOnlyFilesPatterns)
																											? _readOnlyFilesPatterns.stream()
																																	.map(patternStr -> Pattern.compile(patternStr))
																																	.toList()
																											: Collections.emptyList();
																						   }); 
	public boolean isReadOnlyFile(final Path path) {
		if (_locked || !_writable) return true;
		
		// checks if the ABSOLUTE path from ROOT matches any pattern
		Collection<Pattern> patterns = _readOnlyFilesPatternsMemo.get();
		return CollectionUtils.hasData(patterns) ? patterns.stream()
														   .anyMatch(pattern -> {
															   			Matcher m = pattern.matcher(path.asAbsoluteString());
															   			return m.find();
														   		    })
													   : false;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	DEBUG
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CharSequence debugInfo() {
		StringBuilder sb = new StringBuilder();
		sb.append(Strings.customized(" Security: locked={} readable={} writable={}\n",
									 this.isLocked(),this.isReadable(),this.isWritable()));
		sb.append(Strings.customized("Read-only files: {}",
							 		 CollectionUtils.hasData(_readOnlyFilesPatterns) ? _readOnlyFilesPatterns : "none"));
		return sb;
	}
}
