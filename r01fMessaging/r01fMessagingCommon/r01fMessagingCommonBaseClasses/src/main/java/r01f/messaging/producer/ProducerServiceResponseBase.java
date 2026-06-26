package r01f.messaging.producer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;


@RequiredArgsConstructor
@Accessors(prefix="_")
public abstract class ProducerServiceResponseBase<T>
  		   implements ProducerServiceResponse<T> {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////	
	@Getter protected final T _to;
	@Getter protected final boolean _success;
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public ProducerServiceResponseError<T> asResponseError() {
		if (this instanceof ProducerServiceResponseError)
			return (ProducerServiceResponseError<T>)this;
		throw  new IllegalStateException("Cannot cast a ProducerServiceResponseOK to ProducerServiceResponseError");
	}
	@Override
	public ProducerServiceResponseOK<T> asResponseOK() {
		if (this instanceof ProducerServiceResponseOK) return (ProducerServiceResponseOK<T>)this;
		throw  new IllegalStateException("Cannot cast a ProducerServiceResponseError to ProducerServiceResponseOK");
	}
	@Override
	public boolean wasSuccessful() {
		return _success;
	}
}