package r01.api.filestore.model.oids;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import r01f.facets.Facet;
import r01f.guids.OIDTyped;
import r01f.guids.PersistableObjectOID;
import r01f.types.url.UrlPath;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class S3KEYs {
/////////////////////////////////////////////////////////////////////////////////////////
//	KEYS FOR PerisistableKeyValueObject
/////////////////////////////////////////////////////////////////////////////////////////
	public static interface IsS3Key
				    extends OIDTyped<String>,
				    		PersistableObjectOID {	
		public default UrlPath asUrlPath() {
			return UrlPath.from(this.asString());
		}
		public default S3KeyPath getKeyPath() {
			return S3KeyPath.from(this.asUrlPath());
		}
	}
	public interface HasS3Key<K extends IsS3Key>
    	extends Facet {
		void setKey(K key);
		K getKey();
	}
}
