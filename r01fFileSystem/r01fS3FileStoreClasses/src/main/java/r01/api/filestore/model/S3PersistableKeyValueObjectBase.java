package r01.api.filestore.model;

import lombok.experimental.Accessors;
import r01.api.filestore.model.oids.S3KEYs.IsS3Key;
import r01f.model.PersistableModelObjectBase;

@Accessors(prefix="_")
public abstract class S3PersistableKeyValueObjectBase<K extends IsS3Key,
											            SELF_TYPE extends S3PersistableKeyValueObjectBase<K,SELF_TYPE>>
              extends PersistableModelObjectBase<K,SELF_TYPE>
  	 	   implements S3PersistableKeyValueObject<K> {

	private static final long serialVersionUID = 7579054159448752329L;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public S3PersistableKeyValueObjectBase() {
		// default no-args constructor
	}
	public S3PersistableKeyValueObjectBase(final K key) {
		super(key);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	KEY
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void setKey(final K key) {
		this.setOid(key);
	}
	@Override
	public K getKey() {
		return this.getOid();
	}
	@SuppressWarnings("unchecked")
	public SELF_TYPE withKey(final K key) {
		this.setKey(key);
		return (SELF_TYPE)key;
	}
}
