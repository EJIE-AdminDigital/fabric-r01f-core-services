package r01f.messaging.producer;

import lombok.experimental.Accessors;

@Accessors(prefix="_")
public class ProducerServiceResponseOK<T>
	 extends ProducerServiceResponseBase<T>
  implements ProducerServiceResponse<T> {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
	public ProducerServiceResponseOK(final T to) {
		super(to, 
			  true);	// success
	}
}