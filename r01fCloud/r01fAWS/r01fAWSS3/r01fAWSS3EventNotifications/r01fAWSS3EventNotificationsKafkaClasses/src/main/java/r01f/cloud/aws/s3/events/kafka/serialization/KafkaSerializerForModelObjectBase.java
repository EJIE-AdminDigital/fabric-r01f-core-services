package r01f.cloud.aws.s3.events.kafka.serialization;

import jakarta.inject.Inject;
import lombok.experimental.Accessors;
import r01f.messaging.kafka.serialization.KafkaSerializer;
import r01f.messaging.serialization.SerializersForModelObjects.ModelObjectSerializerBase;
import r01f.mime.MimeType;
import r01f.mime.MimeTypes;
import r01f.model.ModelObject;
import r01f.model.annotations.ModelObjectsMarshaller;
import r01f.objectstreamer.Marshaller;

@Accessors(prefix="_")
public class KafkaSerializerForModelObjectBase<M extends ModelObject>
	 extends ModelObjectSerializerBase<M>
  implements KafkaSerializer<M> {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
	@Inject
	public KafkaSerializerForModelObjectBase(        final Class<M> classType,
							@ModelObjectsMarshaller  final Marshaller marshaller) {
		super(classType,
		      marshaller,
		      MimeTypes.APPLICATION_JSON );
	}
	public KafkaSerializerForModelObjectBase(final Class<M> classType,
											 final Marshaller marshaller,
											 final MimeType mimeType) {
		super(classType,
		      marshaller,
		      mimeType);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	SERIALIZE
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public byte[] serialize(final String topic,final M data) {
		return super.serialize(data);
	}
}