package r01f.cloud.aws.s3.events.kafka.serialization;


import java.util.Date;

import lombok.experimental.Accessors;
import r01f.messaging.serialization.DeserializersForBasicTypes.DateRequestTypeMapperBase;
import r01f.messaging.serialization.DeserializersForBasicTypes.MarshalledObjectDeserializerBase;
import r01f.mime.MimeType;
import r01f.objectstreamer.Marshaller;

/**
 * Deserializers for basic types
 */
public class KafkaDeserializersForBasicTypes {
/////////////////////////////////////////////////////////////////////////////////////////
//	Date Deserializer
/////////////////////////////////////////////////////////////////////////////////////////
	public static  class KafkaDateRequestTypeMapperBase
			     extends DateRequestTypeMapperBase
			  implements AWS3KafkaDeserializer<Date> {
		@Override
		public Date deserialize(final String topic,byte[] data) {
			return super.deserialize(data);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Accessors(prefix="_")
	public static abstract class KafkaMarshalledObjectDeserializerBase<T>
			extends MarshalledObjectDeserializerBase<T>
		 implements AWS3KafkaDeserializer<T> {

		public KafkaMarshalledObjectDeserializerBase(final Class<T>  classType,
												     final MimeType  mimeType,
												     final Marshaller modelObjectsMarshaller) {
			super(classType,mimeType,modelObjectsMarshaller);
		}
		@Override
		public T deserialize(final String topic, final byte[] data) {
			return super.deserialize(data);
		}
	}
}
