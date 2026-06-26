package r01f.core.messaging.rabbitmq.consumer.bootstrap;

import java.lang.reflect.Type;

import com.google.inject.TypeLiteral;

import r01f.facets.HasOID;
import r01f.generics.ParameterizedTypeImpl;
import r01f.guids.OID;
import r01f.messaging.rabbitmq.consumer.RabbitMQConsumerService;
import r01f.messaging.rabbitmq.consumer.RabbitMQConsumerServiceConfig;
import r01f.messaging.rabbitmq.consumer.RabbitMQConsumerServiceForModelObjectImpl;
import r01f.messaging.rabbitmq.serialization.RabbitMQDeserializer;
import r01f.messaging.rabbitmq.serialization.RabbitMQDeserializerForModelObjectBase;
import r01f.messaging.rabbitmq.serialization.RabbitMQDeserializerForOIDBase;
import r01f.model.ModelObject;


public abstract class RabbitMQConsumerGuiceModuleForModelObjectBase<O extends OID, M extends ModelObject & HasOID<O>>
		extends RabbitMQConsumerGuiceModuleBase<O,M> {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public RabbitMQConsumerGuiceModuleForModelObjectBase(final Class<O> keyClassType,
			                                             final Class<M> valueClassType,
			                                             final RabbitMQConsumerServiceConfig cfg) {
		super(keyClassType, valueClassType, cfg);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSUMER SERVICES  ( as-type-literals)
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unchecked")
	protected TypeLiteral<RabbitMQConsumerService<M>>  _consumerServiceAsTypeLiteral() {
		return (TypeLiteral<RabbitMQConsumerService<M>>) TypeLiteral.get(new ParameterizedTypeImpl(RabbitMQConsumerService.class,
				                                                                                   new Type[] { _valueClassType } ,
                                                                                                   null));
	}
	@SuppressWarnings("unchecked")
	protected TypeLiteral<RabbitMQConsumerServiceForModelObjectImpl<O, M>>  _consumerServiceImplAsTypeLiteral() {	;
		return  (TypeLiteral<RabbitMQConsumerServiceForModelObjectImpl<O, M>>) TypeLiteral.get(new ParameterizedTypeImpl(RabbitMQConsumerServiceForModelObjectImpl.class,
                                                                                                                         new Type[]  {_keyClassType, _valueClassType},
                                                                                                                         null));
	}
/////////////////////////////////////////////////////////////////////////////////////////
// DESERIALIZERS  ( as-type-literals)
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unchecked")
	protected TypeLiteral<RabbitMQDeserializer<M>>  _deserializerForModelObjectAsTypeLiteral() {
		return 	(TypeLiteral<RabbitMQDeserializer< M>>) TypeLiteral.get(new ParameterizedTypeImpl(RabbitMQDeserializer.class,
																                                  new Type[] {_valueClassType },
                                                                                                  null));
	}
	@SuppressWarnings("unchecked")
	protected TypeLiteral<RabbitMQDeserializerForModelObjectBase<M>>  _deserializerForModelObjectImplAsTypeLiteral() {
		return 	(TypeLiteral<RabbitMQDeserializerForModelObjectBase< M>>) TypeLiteral.get(new ParameterizedTypeImpl(RabbitMQDeserializerForModelObjectBase.class,
																                                                    new Type[] {_valueClassType },
                                                                                                                    null));
	}
	@SuppressWarnings("unchecked")
	protected TypeLiteral<RabbitMQDeserializer<O>>  _deserializerForOidAsTypeLiteral() {
		return 	(TypeLiteral<RabbitMQDeserializer< O>>) TypeLiteral.get(new ParameterizedTypeImpl(RabbitMQDeserializer.class,
																                                  new Type[] {_keyClassType },
                                                                                                  null));
	}
	@SuppressWarnings("unchecked")
	protected TypeLiteral<RabbitMQDeserializerForOIDBase<O>>  _deserializerForOidImplAsTypeLiteral() {
		return 	(TypeLiteral<RabbitMQDeserializerForOIDBase< O>>) TypeLiteral.get(new ParameterizedTypeImpl(RabbitMQDeserializerForOIDBase.class,
																                                            new Type[] {_keyClassType },
                                                                                                            null));
	}
	@SuppressWarnings("unchecked")
	protected TypeLiteral<Class<M>>  _modelObjectTypedClassForDeserializerAsTypeLiteral() {
		return	(TypeLiteral<Class< M>>) TypeLiteral.get(new ParameterizedTypeImpl(Class.class,
				                                                                   new Type[] {_valueClassType },
															                       null));
	}
	@SuppressWarnings("unchecked")
	protected TypeLiteral<Class<O>>  _oidTypedClassForDeserializerAsTypeLiteral() {
		return	(TypeLiteral<Class< O>>) TypeLiteral.get(new ParameterizedTypeImpl(Class.class,
				                                                                   new Type[] {_keyClassType },
															                       null));
	}
}
