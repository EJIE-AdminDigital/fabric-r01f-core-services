package r01f.messaging.kafka.serialization;

import r01f.messaging.serialization.SerializersForModelObjects.OIDSerializerBase;
import r01f.oids.OID;

public class KafkaSerializerForOIDBase<O extends OID>
	 extends OIDSerializerBase<O>
  implements KafkaSerializer<O> {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public byte[] serialize(final String topic, final OID data) {
		return super.serialize(data);
	}
}