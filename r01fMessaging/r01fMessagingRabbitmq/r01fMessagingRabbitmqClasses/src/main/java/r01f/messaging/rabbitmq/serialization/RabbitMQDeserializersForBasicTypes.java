package r01f.messaging.rabbitmq.serialization;

import java.util.Date;

import lombok.experimental.Accessors;
import r01f.messaging.serialization.DeserializersForBasicTypes.DateRequestTypeMapperBase;
import r01f.messaging.serialization.DeserializersForBasicTypes.MarshalledObjectDeserializerBase;
import r01f.mime.MimeType;
import r01f.objectstreamer.Marshaller;

/**
 * Deserializers for basic types
 */
public class RabbitMQDeserializersForBasicTypes {
/////////////////////////////////////////////////////////////////////////////////////////
//	Date Deserializer
/////////////////////////////////////////////////////////////////////////////////////////
	public static  class KafkaDateRequestTypeMapperBase
			extends DateRequestTypeMapperBase
			implements RabbitMQDeserializer<Date> {

		// just extends

	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Accessors(prefix="_")
	public static abstract class KafkaMarshalledObjectDeserializerBase<T>
			extends MarshalledObjectDeserializerBase<T>
		 implements RabbitMQDeserializer<T> {

		public KafkaMarshalledObjectDeserializerBase(final Class<T>  classType,
												     final MimeType  mimeType,
												     final Marshaller modelObjectsMarshaller) {
			super(classType,mimeType,modelObjectsMarshaller);
		}

		// just extends
	}
}
