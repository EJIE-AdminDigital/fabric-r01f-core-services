package r01.model;

import lombok.experimental.Accessors;
import r01.model.oids.KEYs.KEY;
import r01f.model.PersistableModelObjectBase;

@Accessors(prefix="_")
public abstract class PersistableKeyValueObjectBase<K extends KEY,
											            SELF_TYPE extends PersistableKeyValueObjectBase<K,SELF_TYPE>>
              extends PersistableModelObjectBase<K,SELF_TYPE>
  	 	   implements PersistableKeyValueObject<K> {

	private static final long serialVersionUID = 7579054159448752329L;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public PersistableKeyValueObjectBase() {
		// default no-args constructor
	}
	public PersistableKeyValueObjectBase(final K key) {
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
