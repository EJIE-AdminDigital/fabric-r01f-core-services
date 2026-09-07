package r01f.messaging.serialization;


import java.util.Collection;
import java.util.Date;
import java.util.Map;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.mime.MimeType;
import r01f.mime.MimeTypes;
import r01f.objectstreamer.HasMarshaller;
import r01f.objectstreamer.Marshaller;
import r01f.types.Range;
import r01f.util.types.datetime.DateTimeMarshaller;

/**
 * {@link Serializer} for basic types as Long, Integer, Boolean, etc
 *
 */
public class SerializersForBasicTypes {
/////////////////////////////////////////////////////////////////////////////////////////
//	Boolean
/////////////////////////////////////////////////////////////////////////////////////////
	public static  class BooleanSerializerBase
	         implements Serializer<Boolean> {
		@Override
		public byte[] serialize(final Boolean theBoolean) {
			if (theBoolean != null) return Boolean.toString(theBoolean).getBytes();
            // Default
			return Boolean.toString(Boolean.FALSE).getBytes();
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	Date
/////////////////////////////////////////////////////////////////////////////////////////
	public static abstract class DateSerializerBase
	  		          implements Serializer<Date> {
		@Override
		public byte[] serialize(final Date theDate) {
			if (theDate != null) {
				return Long.toString(DateTimeMarshaller.forMarshalling(theDate.toInstant())
													   .asEPOHMillis())
													   .getBytes();
			}
            // Default
			return Long.toString(0).getBytes();
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	Long
/////////////////////////////////////////////////////////////////////////////////////////
	public static abstract class LongSerializerBase
	  		          implements Serializer<Long> {
		@Override
		public byte[] serialize(final Long theLong) {
			if (theLong != null)
				return Long.toString(theLong).getBytes();
            //Default
			return Long.toString(0).getBytes();
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	Range
/////////////////////////////////////////////////////////////////////////////////////////
	public static  class RangeSerializer
			  implements Serializer<Range<?>> {
		@Override
		public byte[] serialize(final Range<?> theRange) {
			if (theRange != null) return theRange.asString().getBytes();
		    return ("0".getBytes());
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	Collection
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("rawtypes")
	public static abstract class CollectionSerializerBase
		                 extends MarshalledObjectSerializerBase<Collection> {
		public CollectionSerializerBase(final Marshaller modelObjectsMarshaller,final MimeType mediaType) {
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
		                 extends MarshalledObjectSerializerBase<Map> {
		public MapSerializerBase(final Marshaller modelObjectsMarshaller, final MimeType mediaType) {
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
	public static  class MarshalledObjectSerializerBase<T>
              implements Serializer<T>,
              			 HasMarshaller {
				@SuppressWarnings("unused")
				private final Class<?> _mappedType;
				private final MimeType _mimeType;

		@Getter private final Marshaller _modelObjectsMarshaller;

		public MarshalledObjectSerializerBase(final Class<?> mappedType,
				                              final MimeType mimeType,
											  final Marshaller modelObjectsMarshaller) {
			_mappedType = mappedType;
			_modelObjectsMarshaller = modelObjectsMarshaller;
			_mimeType = mimeType;
		}
		@Override
		public byte[] serialize(final T obj) {
			String outString = null;
			if (_mimeType.is(MimeTypes.APPLICATION_JSON)) {
				outString = obj != null ? this.getModelObjectsMarshaller().forWriting().toJson(obj)
							  		 	: null;
			} else if (_mimeType.is(MimeTypes.APPLICATION_XML)) {
				outString = obj != null ? this.getModelObjectsMarshaller().forWriting().toXml(obj)
							  		 	   : null;
			} else {
				throw new IllegalArgumentException("Received media type is not compatible");
			}
			if (outString == null) {
				throw new IllegalArgumentException("Received <T> object is null or cannot be marshalled");
			}
			return outString.getBytes();
		}
	}
}
