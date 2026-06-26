package r01f.core.notifier;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Accessors(prefix="_")
@RequiredArgsConstructor
public abstract class NotifierResponseResultBase<T> 
     	   implements NotifierResponseResult<T> {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public NotifierResponseResultImpl<T> asSingle() {
		if (!(this instanceof NotifierResponseResultImpl)) throw new IllegalArgumentException("Cannot cast multiple to single");
		return (NotifierResponseResultImpl<T>)this;
	}	
	@Override
	public NotifierResponseResultImplMultiple<T> asMultiple() {		
		if (!(this instanceof NotifierResponseResultImplMultiple)) throw new IllegalArgumentException("Cannot cast single to multiple");
		return (NotifierResponseResultImplMultiple<T>)this;
	}
}
