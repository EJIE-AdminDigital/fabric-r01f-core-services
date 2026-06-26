package r01f.messaging.rabbitmq.serialization;

import jakarta.inject.Inject;

import lombok.experimental.Accessors;
import r01f.guids.OID;
import r01f.messaging.serialization.DeserializersForModelObjects.OIDDeserializer;

@Accessors(prefix="_")
public  class RabbitMQDeserializerForOIDBase<O extends OID>
			extends OIDDeserializer<O>
		implements RabbitMQDeserializer<O> {
	@Inject
	public RabbitMQDeserializerForOIDBase( final Class<O> oidType) {
		super(oidType);
	}
	// just extends
}