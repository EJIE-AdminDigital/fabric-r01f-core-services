package r01f.messaging.rabbitmq.serialization;

import lombok.experimental.Accessors;
import r01f.guids.OID;
import r01f.messaging.serialization.SerializersForModelObjects.OIDSerializerBase;

/////////////////////////////////////////////////////////////////////////////////////////
//Serializer For OID
////////////////////////////////////////////////////////////////////////////////////////
@Accessors(prefix="_")
public class RabbitMQSerializerForOIDBase<O extends OID>
		extends OIDSerializerBase<O>
	implements RabbitMQSerializer<O> {
// just extends
}