package r01f.messaging.serialization;

import java.io.ByteArrayInputStream;
import java.util.Date;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.mime.MimeType;
import r01f.mime.MimeTypes;
import r01f.objectstreamer.Marshaller;
import r01f.util.types.Strings;
import r01f.util.types.datetime.DateTimeMarshaller;

/**
 * Deserializers for basic types
 */
public class DeserializersForBasicTypes {
/////////////////////////////////////////////////////////////////////////////////////////
//	Date Deserializer
/////////////////////////////////////////////////////////////////////////////////////////
	public static  class DateRequestTypeMapperBase
		    implements Deserializer<Date> {
		@Override
		public Date deserialize(final byte[] data) {
			String dateMillisStr = new String(data);
			Date outDate = null;
			if (Strings.isNOTNullOrEmpty(dateMillisStr)) {
				outDate = Date.from(DateTimeMarshaller.forUnMarshallingFrom(dateMillisStr)
													  .asInstantAsummingEPOCHMillisFormat()
													  .orElse(null));
			}
			return outDate;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Accessors(prefix="_")
	public static abstract class MarshalledObjectDeserializerBase<T>
		  	 		  implements Deserializer<T> {

		private final Class<T>  _mappedType;
		private final MimeType  _mimeType;
		@Getter private final Marshaller _objectsMarshaller;

		public MarshalledObjectDeserializerBase(final Class<T> mappedType,
												final MimeType  mimeType,
												final Marshaller modelObjectsMarshaller) {
			_mappedType = mappedType;
			_mimeType =   mimeType;
			_objectsMarshaller = modelObjectsMarshaller;
		}

		@Override
		public T deserialize( final byte[] data) {
			T outObj = null;
			if (_mimeType.is(MimeTypes.APPLICATION_JSON)) {
				outObj = data != null ? this.getObjectsMarshaller()
											.forReading().fromJson(new ByteArrayInputStream(data),
													              _mappedType)
							  		 	: null;
			} else if (_mimeType.is(MimeTypes.APPLICATION_XML)) {
				outObj = data != null ? this.getObjectsMarshaller()
											.forReading().fromXml(new ByteArrayInputStream(data),
																   _mappedType)
								       : null;
			} else {
				throw new IllegalArgumentException("Received media type is not compatible");
			}
			if (outObj == null) {
				throw new IllegalArgumentException("Received <T> object is null or cannot be marshalled");
			}
			return outObj;
		}
	}

}
