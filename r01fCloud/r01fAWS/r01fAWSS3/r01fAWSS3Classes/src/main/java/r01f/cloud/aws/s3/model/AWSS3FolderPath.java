package r01f.cloud.aws.s3.model;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

import lombok.Getter;
import r01f.annotations.Immutable;
import r01f.guids.OIDTyped;
import r01f.types.Path;


@Immutable
public record AWSS3FolderPath(@Getter String id)
   implements OIDTyped<String> {

	public static final String DELIMITER = "/";
/////////////////////////////////////////////////////////////////////////////////////////
// 	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public static AWSS3FolderPath from(final String path) {
		return AWSS3FolderPath.fromString(path);
	}
	public static AWSS3FolderPath forId(final String id) {
		return AWSS3FolderPath.fromString(id);
	}
	public static AWSS3FolderPath valueOf(final String id) {
		return AWSS3FolderPath.fromString(id);
	}
	public static AWSS3FolderPath fromString(final String path) {
		if (!path.endsWith(DELIMITER)) {
			return new AWSS3FolderPath(path + DELIMITER);
		}
		return new AWSS3FolderPath(path);	
	}
	public static AWSS3FolderPath fromPath(final Path path) {
		return new AWSS3FolderPath(path.asString() + DELIMITER);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String asString() {
		return this.id;
	}
	@Override
	public String toString() {
		return this.id;
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	public Path asPath() {
		return Path.from(this.getId());
	}
	public static Collection<AWSS3FolderPath> getAllFoldersForPath(final AWSS3FolderPath path) {
		return StreamSupport.stream(Splitter.on(DELIMITER)
									  	    .split(path.asString())
									  	    .spliterator(),
								    false)	// not parallel
							.map(new Function<String,AWSS3FolderPath>() {
												String _previous;
												
												@Override
												public AWSS3FolderPath apply(final String input) {
													  String rejoined = _previous != null 
															  				? Joiner.on(DELIMITER).join(_previous,input)
															  			    : input;
											           _previous = rejoined;
											          return AWSS3FolderPath.fromPath(Path.valueOf(rejoined));
												}
								 })
							.collect(Collectors.toList());
	}
}