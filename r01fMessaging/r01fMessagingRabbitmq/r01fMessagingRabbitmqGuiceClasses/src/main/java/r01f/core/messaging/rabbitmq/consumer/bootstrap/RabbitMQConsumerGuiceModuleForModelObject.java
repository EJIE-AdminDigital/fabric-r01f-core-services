package r01f.core.messaging.rabbitmq.consumer.bootstrap;

import com.google.inject.Binder;

import r01f.facets.HasOID;
import r01f.guids.OID;
import r01f.messaging.rabbitmq.consumer.RabbitMQConsumerServiceConfig;
import r01f.model.ModelObject;


public class RabbitMQConsumerGuiceModuleForModelObject<O extends OID, M extends ModelObject & HasOID<O>>
		extends RabbitMQConsumerGuiceModuleForModelObjectBase<O,M> {
/////////////////////////////////////////////////////////////////////////////////////////
//CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public RabbitMQConsumerGuiceModuleForModelObject(final Class<O> keyClassType,
			                                         final Class<M> valueClassType,
			                                         final RabbitMQConsumerServiceConfig cfg) {
		super(keyClassType, valueClassType, cfg);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	BINDINGS TO IMPLEMENT
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected void _bindConsumerService(final Binder binder) {
		// .. bind RabbitMQConsumerService to RabbitMQConsumerServiceImpls
		binder.bind(_consumerServiceAsTypeLiteral())
					.to(_consumerServiceImplAsTypeLiteral());
	}
	@Override
	protected void _bindConsumerDeserializers(final Binder binder) {
		// ... bind deserializer to model object serializer
		 binder.bind(_deserializerForModelObjectAsTypeLiteral())
					.to(_deserializerForModelObjectImplAsTypeLiteral());

		// ... bind deserializer to oid object serializer
		 binder.bind(_deserializerForOidAsTypeLiteral())
					.to(_deserializerForOidImplAsTypeLiteral());

		 //	 ..bind class type for deserializer required constructor [ model marshaller is binded in common guice modules..]
		 binder.bind(_modelObjectTypedClassForDeserializerAsTypeLiteral())
	 				.toInstance(_valueClassType);

		 //	 ..bind class type for deserializer required constructor [ model marshaller is binded in common guice modules..]
		 binder.bind(_oidTypedClassForDeserializerAsTypeLiteral())
	 				.toInstance(_keyClassType);
	}
}
