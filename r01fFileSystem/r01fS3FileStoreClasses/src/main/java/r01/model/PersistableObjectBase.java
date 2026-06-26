package r01.model;

import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import r01f.guids.PersistableObjectOID;
import r01f.model.PersistableModelObjectBase;


@NoArgsConstructor
@Accessors(prefix="_")
public abstract class PersistableObjectBase<O extends PersistableObjectOID,
											    SELF_TYPE extends PersistableObjectBase<O,SELF_TYPE>>
              extends PersistableModelObjectBase<O,SELF_TYPE>
  	 	   implements PersistableObject<O> {

	private static final long serialVersionUID = 7119851413553178928L;

}
