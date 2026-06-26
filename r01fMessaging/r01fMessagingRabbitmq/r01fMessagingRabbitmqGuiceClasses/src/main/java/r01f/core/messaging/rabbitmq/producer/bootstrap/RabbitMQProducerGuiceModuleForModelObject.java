package r01f.core.messaging.rabbitmq.producer.bootstrap;

import com.google.inject.Binder;

import r01f.facets.HasOID;
import r01f.guids.OID;
import r01f.messaging.rabbitmq.producer.RabbitMQProducerServiceConfig;
import r01f.model.ModelObject;


public class RabbitMQProducerGuiceModuleForModelObject<O extends OID, M extends ModelObject & HasOID<O>>
		extends RabbitMQProducerGuiceModuleForModelObjectBase<O,M> {
/////////////////////////////////////////////////////////////////////////////////////////
//CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public RabbitMQProducerGuiceModuleForModelObject(final Class<O> keyClassType,
			                                         final Class<M> valueClassType,
			                                         final RabbitMQProducerServiceConfig cfg) {
		super(keyClassType, valueClassType, cfg);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	BINDINGS TO IMPLEMENT
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected void _bindProducerService(final Binder binder) {
		// .. bind RabbiMQProducerService to RabbitMQProducerServiceImpls
		binder.bind(_producerServiceAsTypeLiteral())
					.to(_producerServiceImplAsTypeLiteral());
	}
	@Override
	protected void _bindProducerSerializers(final Binder binder) {
		// ... bind serializer to model object serializer
		 binder.bind(_serializerForModelObjectAsTypeLiteral())
					.to(_serializerForModelObjectImplAsTypeLiteral());

		// ... bind serializer to oid object serializer
		 binder.bind(_serializerForOidAsTypeLiteral())
					.to(_serializerForOidImplAsTypeLiteral());

		 //	 ..bind class type for serialized required constructor [ model marshaller is binded in common guice modules..]
		 binder.bind(_modelObjectTypedClassForSerializerAsTypeLiteral())
	 				.toInstance(_valueClassType);
	}
}
