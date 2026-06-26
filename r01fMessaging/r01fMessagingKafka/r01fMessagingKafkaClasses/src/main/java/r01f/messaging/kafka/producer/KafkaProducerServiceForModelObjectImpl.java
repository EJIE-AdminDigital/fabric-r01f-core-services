package r01f.messaging.kafka.producer;

import jakarta.inject.Inject;

import r01f.facets.HasOID;
import r01f.guids.OID;
import r01f.messaging.kafka.model.KafkaMessageSubscriber;
import r01f.messaging.kafka.serialization.KafkaSerializer;
import r01f.messaging.producer.MessagingProducerServiceForModelObject;
import r01f.model.ModelObject;

public class KafkaProducerServiceForModelObjectImpl<O extends OID ,M extends ModelObject & HasOID<O>>
			extends KafkaProducerServiceImplBase<O,M>
	implements MessagingProducerServiceForModelObject<O,M,KafkaMessageSubscriber> {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTORS
/////////////////////////////////////////////////////////////////////////////////////////
	@Inject
	public KafkaProducerServiceForModelObjectImpl(final KafkaProducerServiceConfig config,
			                                      final KafkaSerializer<O> keySerializer, final KafkaSerializer<M> valueSerializer ){
		super(config,
			  keySerializer,
			  valueSerializer);
	}
	public KafkaProducerServiceForModelObjectImpl( final KafkaProducerServiceConfig config ){
		super(config,null,null);
	}
/////////////////////////////////////////////////////////////////////////////////////////
// METHODS TO IMPLEMENT
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected O keyFrom(final M value) {
		return value.getOid();
	}
}
