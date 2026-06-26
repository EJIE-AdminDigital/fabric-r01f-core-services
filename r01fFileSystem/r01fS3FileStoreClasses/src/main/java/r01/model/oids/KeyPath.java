package r01.model.oids;

import java.util.Collection;

import r01f.patterns.Memoized;
import r01f.types.IsPath;
import r01f.types.Path;
import r01f.types.PathFactory;
import r01f.util.types.collections.CollectionUtils;

public class KeyPath
        extends Path {

	private static final long serialVersionUID = -5044372692867075189L;
	
//////////////////////////////////////////////////////////////////////////////////
// CONSTRUCTORS
//////////////////////////////////////////////////////////////////////////////////
	public KeyPath() {
		super();
	}
	public KeyPath(final String path) {
		super(path);
	}
	public KeyPath(final String... path) {
		super(path);
	}
	public KeyPath(final IsPath path) {
		super(path);
	}
	public KeyPath(final Collection<String> elements) {
		super(elements);
	}
//////////////////////////////////////////////////////////////////////////////////
// FACTORY METHODS
//////////////////////////////////////////////////////////////////////////////////
	private static final PathFactory<KeyPath> _createKeyPathFactory() {
		return new PathFactory<>() {
					@Override
					public KeyPath createPathFrom(final Collection<String> elements) {
						return new KeyPath (elements);
					}
				};
	}

    private final Memoized<PathFactory<KeyPath>> _memoizedPathFactory = new Memoized<>() {
																				@Override
																				public PathFactory<KeyPath> supply() {
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
	public static KeyPath valueOf(final String path) {
		return new KeyPath(path);
	}
	/**
	 * Factory from path components
	 * @param elements
	 * @return the {@link Path} object
	 */
	public static KeyPath from(final String... elements) {
		if (CollectionUtils.isNullOrEmpty(elements)) return null;
		return new KeyPath(elements);
	}
	/**
	 * Factory from other {@link Path} object
	 * @param other
	 * @return the new {@link Path} object
	 */
	public static <P extends IsPath> KeyPath from(final P other) {
		if (other == null) return null;
		KeyPath outPath = new KeyPath(other);
		return outPath;
	}
	 
/////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////
	public static void main(final String[] argv) {
		KeyPath b =  new  KeyPath();
		b = b.joinedWith("folder1");
		b = b.joinedWith("folder2");
		System.out.println(b.asString());

		KeyPath c =  KeyPath.from("folder1/folder2");
		System.out.println(c);
	}

}
