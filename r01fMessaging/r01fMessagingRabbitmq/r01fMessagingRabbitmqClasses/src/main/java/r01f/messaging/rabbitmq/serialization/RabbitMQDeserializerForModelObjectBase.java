package r01f.messaging.rabbitmq.serialization;

import jakarta.inject.Inject;

import lombok.experimental.Accessors;
import r01f.messaging.serialization.DeserializersForModelObjects.ModelObjectDeserializerBase;
import r01f.mime.MimeType;
import r01f.mime.MimeTypes;
import r01f.model.ModelObject;
import r01f.model.annotations.ModelObjectsMarshaller;
import r01f.objectstreamer.Marshaller;

/////////////////////////////////////////////////////////////////////////////////////////
//Deserializer For Model Object
////////////////////////////////////////////////////////////////////////////////////////
@Accessors(prefix="_")
public  class RabbitMQDeserializerForModelObjectBase<M extends ModelObject>
		extends ModelObjectDeserializerBase<M>
	implements RabbitMQDeserializer<M> {

	@Inject
	public RabbitMQDeserializerForModelObjectBase( 					final Class<M> mappedType,
											@ModelObjectsMarshaller final Marshaller marshaller) {
		super(mappedType,
			  marshaller,
			  MimeTypes.APPLICATION_JSON);
	}
	public RabbitMQDeserializerForModelObjectBase( final Class<M> mappedType,
												   final Marshaller marshaller,
												   final MimeType mimeType) {
		super(mappedType,
			  marshaller,
			  mimeType );
	}
// just extends
}