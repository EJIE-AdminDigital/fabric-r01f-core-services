package r01f.messaging.producer;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.exceptions.EnrichedThrowableTypeBase;
import r01f.exceptions.EnrichedThrowableTypeBuilder;
import r01f.exceptions.ExceptionSeverity;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;

/**
 * Producer Response Error Type
 */
@MarshallType(as="producerResponseErrorTypes.java")
@Accessors(prefix="_")
public final class ProducerServiceResponseErrorType
     	   extends EnrichedThrowableTypeBase {

	private static final long serialVersionUID = 3404282545821425990L;
	/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="origin",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter private final ServiceErrorOrigin _origin;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public ProducerServiceResponseErrorType(final ServiceErrorOrigin origin,
									        final String name,
									        final int group,final int code,
									        final ExceptionSeverity severity) {
		super(name,
			  group,code,
			  severity);
		_origin = origin;
	}


	public static EnrichedThrowableTypeBuilder<ProducerServiceResponseErrorType> originatedAt(final ServiceErrorOrigin origin) {
		return new EnrichedThrowableTypeBuilder<ProducerServiceResponseErrorType>() {
						@Override
						protected ProducerServiceResponseErrorType _build(final String name,
															              final int group,final int code,
															              final ExceptionSeverity severity) {
							return new ProducerServiceResponseErrorType(origin,
															            name,
														                group,code,
														                severity);
						}

			   };
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean isServerError() {
		return _origin != null ? _origin == ServiceErrorOrigin.SERVER : false;
	}
	public boolean isClientError() {
		return _origin != null ? _origin == ServiceErrorOrigin.CLIENT : false;
	}

	public enum ServiceErrorOrigin {
		CLIENT,
		SERVER,
		UNKNOWN;
	}

}