package r01f.core.messaging.rabbitmq.producer.bootstrap;

import java.lang.reflect.Type;

import com.google.inject.TypeLiteral;

import r01f.facets.HasOID;
import r01f.generics.ParameterizedTypeImpl;
import r01f.guids.OID;
import r01f.messaging.rabbitmq.producer.RabbitMQProducerService;
import r01f.messaging.rabbitmq.producer.RabbitMQProducerServiceConfig;
import r01f.messaging.rabbitmq.producer.RabbitMQProducerServiceForModelObjectImpl;
import r01f.messaging.rabbitmq.serialization.RabbitMQSerializer;
import r01f.messaging.rabbitmq.serialization.RabbitMQSerializerForModelObjectBase;
import r01f.messaging.rabbitmq.serialization.RabbitMQSerializerForOIDBase;
import r01f.model.ModelObject;


public abstract class RabbitMQProducerGuiceModuleForModelObjectBase<O extends OID, M extends ModelObject & HasOID<O>>
		extends RabbitMQProducerGuiceModuleBase<O,M> {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public RabbitMQProducerGuiceModuleForModelObjectBase(final Class<O> keyClassType,
			                                             final Class<M> valueClassType,
			                                             final RabbitMQProducerServiceConfig cfg) {
		super(keyClassType, valueClassType, cfg);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	PRODUCER SERVICES  ( as-type-literals)
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unchecked")
	protected TypeLiteral<RabbitMQProducerService<M>>  _producerServiceAsTypeLiteral() {
		return (TypeLiteral<RabbitMQProducerService<M>>) TypeLiteral.get(new ParameterizedTypeImpl(RabbitMQProducerService.class,
				                                                                                   new Type[] { _keyClassType, _valueClassType} ,
                                                                                                   null));
	}

	@SuppressWarnings("unchecked")
	protected TypeLiteral<RabbitMQProducerServiceForModelObjectImpl<O,M>>  _producerServiceImplAsTypeLiteral() {	;
		return  (TypeLiteral<RabbitMQProducerServiceForModelObjectImpl<O,M>>) TypeLiteral.get(new ParameterizedTypeImpl(RabbitMQProducerServiceForModelObjectImpl.class,
                                                                                                                        new Type[]  { _keyClassType, _valueClassType},
                                                                                                                        null));
	}
/////////////////////////////////////////////////////////////////////////////////////////
// SERIALIZERS  ( as-type-literals)
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unchecked")
	protected TypeLiteral<RabbitMQSerializer<M>>  _serializerForModelObjectAsTypeLiteral() {
		return 	(TypeLiteral<RabbitMQSerializer< M>>) TypeLiteral.get(new ParameterizedTypeImpl(RabbitMQSerializer.class,
																                                new Type[] {_valueClassType },
                                                                                                null));
	}
	@SuppressWarnings("unchecked")
	protected TypeLiteral<RabbitMQSerializerForModelObjectBase<M>>  _serializerForModelObjectImplAsTypeLiteral() {
		return 	(TypeLiteral<RabbitMQSerializerForModelObjectBase<M>>) TypeLiteral.get(new ParameterizedTypeImpl(RabbitMQSerializerForModelObjectBase.class,
																                                                 new Type[] {_valueClassType },
                                                                                                                 null));
	}
	@SuppressWarnings("unchecked")
	protected TypeLiteral<RabbitMQSerializer<O>>  _serializerForOidAsTypeLiteral() {
		return 	(TypeLiteral<RabbitMQSerializer< O>>) TypeLiteral.get(new ParameterizedTypeImpl(RabbitMQSerializer.class,
																                                new Type[] {_keyClassType },
                                                                                                null));
	}
	@SuppressWarnings("unchecked")
	protected TypeLiteral<RabbitMQSerializerForOIDBase<O>>  _serializerForOidImplAsTypeLiteral() {
		return 	(TypeLiteral<RabbitMQSerializerForOIDBase< O>>) TypeLiteral.get(new ParameterizedTypeImpl(RabbitMQSerializerForOIDBase.class,
																                                          new Type[] {_keyClassType },
                                                                                                          null));
	}
	@SuppressWarnings("unchecked")
	protected TypeLiteral<Class<M>>  _modelObjectTypedClassForSerializerAsTypeLiteral() {
		return	(TypeLiteral<Class< M>>) TypeLiteral.get(new ParameterizedTypeImpl(Class.class,
				                                                                   new Type[] {_valueClassType },
															                       null));
	}
}
