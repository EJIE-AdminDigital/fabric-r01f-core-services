package r01f.cloud.aws.s3.model;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

import lombok.NoArgsConstructor;
import r01f.annotations.Immutable;
import r01f.guids.OIDBaseMutable;
import r01f.types.Path;


@Immutable
@NoArgsConstructor
public class AWSS3FolderPath
	 extends OIDBaseMutable<String> {

	private static final long serialVersionUID = 4162366466990455545L;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTANT
/////////////////////////////////////////////////////////////////////////////////////////
	public static final String DELIMITER = "/";
/////////////////////////////////////////////////////////////////////////////////////////
// 	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	AWSS3FolderPath(final String id) {
		super(id);
	}
	public static AWSS3FolderPath fromPath(final Path path) {
		return new AWSS3FolderPath(path.asString() + DELIMITER);
	}
	public static AWSS3FolderPath fromString(final String path) {
		if (!path.endsWith(DELIMITER)) {
			return new AWSS3FolderPath(path + DELIMITER);
		}
		return new AWSS3FolderPath(path);	
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	METHODS
/////////////////////////////////////////////////////////////////////////////////////////
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