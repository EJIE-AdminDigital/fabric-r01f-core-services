package r01.api.filestore.model.oids;

import java.util.Collection;

import r01f.patterns.Memoized;
import r01f.types.IsPath;
import r01f.types.Path;
import r01f.types.PathFactory;
import r01f.util.types.collections.CollectionUtils;

public class S3KeyPath
        extends Path {

	private static final long serialVersionUID = -5044372692867075189L;
	
//////////////////////////////////////////////////////////////////////////////////
// CONSTRUCTORS
//////////////////////////////////////////////////////////////////////////////////
	public S3KeyPath() {
		super();
	}
	public S3KeyPath(final String path) {
		super(path);
	}
	public S3KeyPath(final String... path) {
		super(path);
	}
	public S3KeyPath(final IsPath path) {
		super(path);
	}
	public S3KeyPath(final Collection<String> elements) {
		super(elements);
	}
//////////////////////////////////////////////////////////////////////////////////
// FACTORY METHODS
//////////////////////////////////////////////////////////////////////////////////
	private static final PathFactory<S3KeyPath> _createKeyPathFactory() {
		return new PathFactory<>() {
					@Override
					public S3KeyPath createPathFrom(final Collection<String> elements) {
						return new S3KeyPath (elements);
					}
				};
	}

    private final Memoized<PathFactory<S3KeyPath>> _memoizedPathFactory = new Memoized<>() {
																				@Override
																				public PathFactory<S3KeyPath> supply() {
																					return _createKeyPathFactory();
																				}
																		};
	@Override @SuppressWarnings("unchecked")
	public <P extends IsPath> PathFactory<P> getPathFactory() {
		return (PathFactory<P>)_memoizedPathFactory.get();
	}

//////////////////////////////////////////////////////////////////////////////
// PARSE METHODS
/////////////////////////////////////////////////////////////////////////////
	public static S3KeyPath valueOf(final String path) {
		return new S3KeyPath(path);
	}
	/**
	 * Factory from path components
	 * @param elements
	 * @return the {@link Path} object
	 */
	public static S3KeyPath from(final String... elements) {
		if (CollectionUtils.isNullOrEmpty(elements)) return null;
		return new S3KeyPath(elements);
	}
	/**
	 * Factory from other {@link Path} object
	 * @param other
	 * @return the new {@link Path} object
	 */
	public static <P extends IsPath> S3KeyPath from(final P other) {
		if (other == null) return null;
		S3KeyPath outPath = new S3KeyPath(other);
		return outPath;
	}
	 
/////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////
	public static void main(final String[] argv) {
		S3KeyPath b =  new  S3KeyPath();
		b = b.joinedWith("folder1");
		b = b.joinedWith("folder2");
		System.out.println(b.asString());

		S3KeyPath c =  S3KeyPath.from("folder1/folder2");
		System.out.println(c);
	}

}
