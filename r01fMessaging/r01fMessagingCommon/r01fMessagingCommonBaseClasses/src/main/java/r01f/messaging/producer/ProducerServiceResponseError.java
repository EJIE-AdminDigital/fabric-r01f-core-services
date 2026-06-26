package r01f.messaging.producer;

import lombok.Getter;
import lombok.experimental.Accessors;


@Accessors(prefix="_")
public class ProducerServiceResponseError<T>
  	 extends ProducerServiceResponseBase<T>
  implements ProducerServiceResponse<T> {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter protected final ProducerServiceResponseErrorType _errorType;
	@Getter protected final String  _errorDetail;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////	
	public ProducerServiceResponseError(final T to,
                                        final ProducerServiceResponseErrorType errorType) {
		this(to, errorType, null);

	}
	public ProducerServiceResponseError( final T to,
			                             final ProducerServiceResponseErrorType errorType,final String errorDetail) {
		super(to, false);
		_errorType = errorType;
		_errorDetail = errorDetail;
	}
}