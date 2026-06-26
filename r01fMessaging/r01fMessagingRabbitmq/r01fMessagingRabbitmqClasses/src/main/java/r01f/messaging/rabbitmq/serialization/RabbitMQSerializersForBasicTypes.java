package r01f.messaging.rabbitmq.serialization;


import java.util.Collection;
import java.util.Date;
import java.util.Map;

import lombok.experimental.Accessors;
import r01f.messaging.serialization.SerializersForBasicTypes.BooleanSerializerBase;
import r01f.messaging.serialization.SerializersForBasicTypes.DateSerializerBase;
import r01f.messaging.serialization.SerializersForBasicTypes.LongSerializerBase;
import r01f.messaging.serialization.SerializersForBasicTypes.MarshalledObjectSerializerBase;
import r01f.messaging.serialization.SerializersForBasicTypes.RangeSerializer;
import r01f.mime.MimeType;
import r01f.mime.MimeTypes;
import r01f.objectstreamer.Marshaller;
import r01f.types.Range;

/**
 * {@link Serializer} for basic types as Long, Integer, Boolean, etc
 *
 */
public class RabbitMQSerializersForBasicTypes {
/////////////////////////////////////////////////////////////////////////////////////////
//	Boolean
/////////////////////////////////////////////////////////////////////////////////////////
	public static  class RabbitMQBooleanSerializerBase
		extends BooleanSerializerBase
	  implements RabbitMQSerializer<Boolean> {

		// just extends
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	Date
/////////////////////////////////////////////////////////////////////////////////////////
	public static abstract class RabbitMQDateSerializerBase
			extends DateSerializerBase
	   implements RabbitMQSerializer<Date> {
		// just extends
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	Long
/////////////////////////////////////////////////////////////////////////////////////////
	public static abstract class RabbitMQLongSerializerBase
		extends LongSerializerBase
	  implements RabbitMQSerializer<Long> {
		// just extends
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	Range
/////////////////////////////////////////////////////////////////////////////////////////
	public static  class RabbitMQRangeSerializer
			extends RangeSerializer
	   implements RabbitMQSerializer<Range<?>> {

		// just extends
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	Collection
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("rawtypes")
	public static abstract class RabbitMQCollectionSerializerBase
		                 extends RabbitMQMarshalledObjectSerializerBase<Collection> {
		public RabbitMQCollectionSerializerBase(final Marshaller modelObjectsMarshaller, final MimeType mediaType) {
			super(Collection.class,
				  mediaType,
				  modelObjectsMarshaller);
		}
		public RabbitMQCollectionSerializerBase(final Marshaller modelObjectsMarshaller) {
			super(Map.class,
				  MimeTypes.APPLICATION_JSON,
				  modelObjectsMarshaller);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	Map
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("rawtypes")
	public static abstract class RabbitMQSerializerBase
		                 extends RabbitMQMarshalledObjectSerializerBase<Map> {
		public RabbitMQSerializerBase(final Marshaller modelObjectsMarshaller,
				                 final MimeType mediaType) {
			super(Map.class,
				  mediaType,
				  modelObjectsMarshaller);
		}
		public RabbitMQSerializerBase(final Marshaller modelObjectsMarshaller) {
			super(Map.class,
				  MimeTypes.APPLICATION_JSON,
				  modelObjectsMarshaller);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
// Serializer based on Marshaller  ( ModelObjects, etc...)
/////////////////////////////////////////////////////////////////////////////////////////
	@Accessors(prefix="_")
	public static  class RabbitMQMarshalledObjectSerializerBase<T>
			extends MarshalledObjectSerializerBase<T>
		              implements RabbitMQSerializer<T> {

		public RabbitMQMarshalledObjectSerializerBase(final Class<?> mappedType,
				                                   final MimeType mimeType,
											       final Marshaller modelObjectsMarshaller) {
			super(mappedType,mimeType,modelObjectsMarshaller);
		}
		// just extends
	}
}
