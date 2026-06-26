package r01f.messaging.rabbitmq.serialization;

import jakarta.inject.Inject;
import lombok.experimental.Accessors;
import r01f.messaging.serialization.SerializersForModelObjects.ModelObjectSerializerBase;
import r01f.mime.MimeType;
import r01f.mime.MimeTypes;
import r01f.model.ModelObject;
import r01f.model.annotations.ModelObjectsMarshaller;
import r01f.objectstreamer.Marshaller;

@Accessors(prefix="_")
public class RabbitMQSerializerForModelObjectBase<M extends ModelObject>
	 extends ModelObjectSerializerBase<M>
  implements RabbitMQSerializer<M> {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTORS
/////////////////////////////////////////////////////////////////////////////////////////	
	@Inject
	public RabbitMQSerializerForModelObjectBase(		final Class<M> classType,
								@ModelObjectsMarshaller final Marshaller marshaller) {
		super(classType,
			  marshaller,
			  MimeTypes.APPLICATION_JSON );
	}
	public RabbitMQSerializerForModelObjectBase(final Class<M> classType,
			                                    final Marshaller marshaller,
								                final MimeType mimeType) {
		super(classType,
			  marshaller,
			  mimeType);
	}
}

