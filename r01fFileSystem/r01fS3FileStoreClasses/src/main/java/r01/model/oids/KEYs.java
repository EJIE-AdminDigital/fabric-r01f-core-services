package r01.model.oids;

import lombok.NoArgsConstructor;
import r01f.annotations.Immutable;
import r01f.facets.Facet;
import r01f.guids.OIDBaseMutable;
import r01f.guids.PersistableObjectOID;
import r01f.types.url.UrlPath;


public abstract class KEYs {
/////////////////////////////////////////////////////////////////////////////////////////
//	KEYS FOR PerisistableKeyValueObject
/////////////////////////////////////////////////////////////////////////////////////////
	public static interface KEY
				    extends PersistableObjectOID {	//OIDTyped<String> {
		public UrlPath asUrlPath();
		public KeyPath getKeyPath();
	}
	public interface HasKEY<K extends KEY>
    	extends Facet {
		void setKey(K key);
		K getKey();
	}
	
	/**
	 * Base for every key objects
	 */
	@Immutable
	@NoArgsConstructor
	public static abstract class KeyBase
	              		 extends OIDBaseMutable<KeyPath>
			   		  implements KEY {

		private static final long serialVersionUID = 2534023831964634224L;
		
		public KeyBase (final KeyPath keyPath) {
			super(keyPath);
		}
		public KeyBase(final String keyPathAsString) {
			this(KeyPath.from(keyPathAsString));
		}
		@Override
		public KeyPath getKeyPath() {
			return KeyPath.from(this.asUrlPath());
		}
	}
	/**
	 * Base for every Key objects
	 */
	@Immutable
	@NoArgsConstructor
	public static abstract class ModelObjectKeyBase
	                     extends KeyBase {

		private static final long serialVersionUID = -4595848617342901219L;
		
		public ModelObjectKeyBase (final KeyPath keyPath) {
			super(keyPath);
		}
		public ModelObjectKeyBase(final String keyPathAsString) {
			super(keyPathAsString);
		}
		@Override
		public UrlPath asUrlPath() {
			return UrlPath.from(this.asString());
		}
	}
}
