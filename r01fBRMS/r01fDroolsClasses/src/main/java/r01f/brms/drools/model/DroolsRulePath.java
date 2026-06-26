package r01f.brms.drools.model;

import java.util.Collection;

import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.IsPath;
import r01f.types.PathBase;
import r01f.types.PathFactory;

@MarshallType(as="rule")
@Accessors(prefix="_")
@NoArgsConstructor
public class DroolsRulePath	
	 extends PathBase<DroolsRulePath> {
	
 private static final long serialVersionUID = 5972916340798490423L;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR & BUILDER
///////////////////////////////////////////////////////////////////////////////////////// 
	public DroolsRulePath(final String path) {
		super(path);
	}
	public DroolsRulePath(final String... path) {
		super(path);
	}
	public DroolsRulePath(final IsPath path) {
		super(path);
	}
	public DroolsRulePath(final Collection<String> elements) {
		super(elements);
	}
	public static PathFactory<DroolsRulePath> PATH_FACTORY = DroolsRulePath::new;
	
	@Override @SuppressWarnings("unchecked")
	public <P extends IsPath> PathFactory<P> getPathFactory() {
		return (PathFactory<P>)DroolsRulePath.PATH_FACTORY;
	}
}