package r01f.messaging.kafka.serialization;

import jakarta.inject.Inject;
import lombok.experimental.Accessors;
import r01f.messaging.serialization.DeserializersForModelObjects.ModelObjectDeserializerBase;
import r01f.mime.MimeType;
import r01f.mime.MimeTypes;
import r01f.model.ModelObject;
import r01f.model.annotations.ModelObjectsMarshaller;
import r01f.objectstreamer.Marshaller;

@Accessors(prefix="_")
public  class KafkaDeserializerForModelObjectBase<M extends ModelObject>
	  extends ModelObjectDeserializerBase<M>
   implements KafkaDeserializer<M> {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
	@Inject
	public KafkaDeserializerForModelObjectBase( 	                    final Class<M> mappedType,
						                        @ModelObjectsMarshaller final Marshaller marshaller) {
		super(mappedType,
			  marshaller,
			  MimeTypes.APPLICATION_JSON);
	}
	public KafkaDeserializerForModelObjectBase(final Class<M> mappedType,
			                                final Marshaller marshaller,
								            final MimeType mimeType) {
		super(mappedType,
			  marshaller,
			  mimeType );
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	DESERIALIZE
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public M deserialize(final String topic, final byte[] data) {
		return super.deserialize(data);
	}
}