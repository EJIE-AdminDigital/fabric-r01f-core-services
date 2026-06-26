package r01f.messaging.kafka.serialization;


import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.apache.kafka.common.serialization.Serializer;

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
public class KafkaSerializersForBasicTypes {
/////////////////////////////////////////////////////////////////////////////////////////
//	Boolean
/////////////////////////////////////////////////////////////////////////////////////////
	public static  class KafkaBooleanSerializerBase
		extends BooleanSerializerBase
	  implements KafkaSerializer<Boolean> {
		@Override
		public byte[] serialize(final String topic, final Boolean theBoolean) {
			return super.serialize(theBoolean);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	Date
/////////////////////////////////////////////////////////////////////////////////////////
	public static abstract class KafkaDateSerializerBase
			extends DateSerializerBase
	   implements KafkaSerializer<Date> {
		@Override
		public byte[] serialize(final String topic, Date theDate) {
			return  super.serialize(theDate);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	Long
/////////////////////////////////////////////////////////////////////////////////////////
	public static abstract class KafkaLongSerializerBase
		extends LongSerializerBase
	  implements KafkaSerializer<Long> {
		@Override
		public byte[] serialize(final String topic, Long theLong) {
			return super.serialize(theLong);

		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	Range
/////////////////////////////////////////////////////////////////////////////////////////
	public static  class KafkaRangeSerializer
			extends RangeSerializer
	   implements KafkaSerializer<Range<?>> {
		@Override
		public byte[] serialize(final String topic, final  Range<?> theRange) {
		    return super.serialize(theRange);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	Collection
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("rawtypes")
	public static abstract class CollectionSerializerBase
		                 extends KafkaMarshalledObjectSerializerBase<Collection> {
		public CollectionSerializerBase(final Marshaller modelObjectsMarshaller, final MimeType mediaType) {
			super(Collection.class,
				  mediaType,
				  modelObjectsMarshaller);
		}
		public CollectionSerializerBase(final Marshaller modelObjectsMarshaller) {
			super(Map.class,
				  MimeTypes.APPLICATION_JSON,
				  modelObjectsMarshaller);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	Map
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("rawtypes")
	public static abstract class MapSerializerBase
		                 extends KafkaMarshalledObjectSerializerBase<Map> {
		public MapSerializerBase(final Marshaller modelObjectsMarshaller,
				                 final MimeType mediaType) {
			super(Map.class,
				  mediaType,
				  modelObjectsMarshaller);
		}
		public MapSerializerBase(final Marshaller modelObjectsMarshaller) {
			super(Map.class,
				  MimeTypes.APPLICATION_JSON,
				  modelObjectsMarshaller);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
// Serializer based on Marshaller  ( ModelObjects, etc...)
/////////////////////////////////////////////////////////////////////////////////////////
	@Accessors(prefix="_")
	public static  class KafkaMarshalledObjectSerializerBase<T>
			extends MarshalledObjectSerializerBase<T>
		              implements KafkaSerializer<T> {

		public KafkaMarshalledObjectSerializerBase(final Class<?> mappedType,
				                                   final MimeType mimeType,
											       final Marshaller modelObjectsMarshaller) {
			super(mappedType,mimeType,modelObjectsMarshaller);
		}
		@Override
		public byte[] serialize(final String topic,
				                final T obj) {
			return super.serialize(obj);
		}
	}
}
