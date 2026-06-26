package r01f.messaging.kafka.serialization;

import jakarta.inject.Inject;
import lombok.experimental.Accessors;
import r01f.guids.OID;
import r01f.messaging.serialization.DeserializersForModelObjects.OIDDeserializer;

@Accessors(prefix="_")
public class KafkaDeserializerForOIDBase<O extends OID>
	 extends OIDDeserializer<O>
  implements KafkaDeserializer<O> {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
	@Inject
	public KafkaDeserializerForOIDBase( final Class<O> oidType) {
		super(oidType);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	DESERIALIZE
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public O deserialize(final String topic, final byte[] data) {
		return super.deserialize(data);
	}
}
