package r01f.cloud.aws.s3.events.kafka.serialization;

import r01f.guids.OID;
import r01f.messaging.kafka.serialization.KafkaSerializer;
import r01f.messaging.serialization.SerializersForModelObjects.OIDSerializerBase;

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