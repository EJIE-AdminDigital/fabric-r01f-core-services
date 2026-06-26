package r01f.core.messaging.kafka.consumer.bootstrap;

import java.lang.reflect.Type;

import com.google.inject.TypeLiteral;

import r01f.facets.HasOID;
import r01f.generics.ParameterizedTypeImpl;
import r01f.guids.OID;
import r01f.messaging.kafka.consumer.KafkaConsumerService;
import r01f.messaging.kafka.consumer.KafkaConsumerServiceConfig;
import r01f.messaging.kafka.consumer.KafkaConsumerServiceForModelObjectImpl;
import r01f.messaging.kafka.serialization.KafkaDeserializer;
import r01f.messaging.kafka.serialization.KafkaDeserializerForModelObjectBase;
import r01f.messaging.kafka.serialization.KafkaDeserializerForOIDBase;
import r01f.model.ModelObject;


public abstract class KafkaConsumerGuiceModuleForModelObjectBase<O extends OID, M extends ModelObject & HasOID<O>>
		extends KafkaConsumerGuiceModuleBase<O,M> {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public KafkaConsumerGuiceModuleForModelObjectBase(final Class<O> keyClassType,
			                                          final Class<M> valueClassType,
			                                          final KafkaConsumerServiceConfig cfg) {
		super(keyClassType, valueClassType, cfg);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSUMER SERVICES  ( as-type-literals)
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unchecked")
	protected TypeLiteral<KafkaConsumerService<O,M>>  _consumerServiceAsTypeLiteral() {
		return (TypeLiteral<KafkaConsumerService<O, M>>) TypeLiteral.get(new ParameterizedTypeImpl(KafkaConsumerService.class,
				                                                                                   new Type[] { _keyClassType, _valueClassType} ,
                                                                                                   null));
	}
	@SuppressWarnings("unchecked")
	protected TypeLiteral<KafkaConsumerServiceForModelObjectImpl<O, M>>  _consumerServiceImplAsTypeLiteral() {	;
		return  (TypeLiteral<KafkaConsumerServiceForModelObjectImpl<O, M>>) TypeLiteral.get(new ParameterizedTypeImpl(KafkaConsumerServiceForModelObjectImpl.class,
                                                                                                                       new Type[]  { _keyClassType, _valueClassType},
                                                                                                                       null));
	}
/////////////////////////////////////////////////////////////////////////////////////////
// DESERIALIZERS  ( as-type-literals)
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unchecked")
	protected TypeLiteral<KafkaDeserializer<M>>  _deserializerForModelObjectAsTypeLiteral() {
		return 	(TypeLiteral<KafkaDeserializer< M>>) TypeLiteral.get(new ParameterizedTypeImpl(KafkaDeserializer.class,
																                               new Type[] {_valueClassType },
                                                                                               null));
	}
	@SuppressWarnings("unchecked")
	protected TypeLiteral<KafkaDeserializerForModelObjectBase<M>>  _deserializerForModelObjectImplAsTypeLiteral() {
		return 	(TypeLiteral<KafkaDeserializerForModelObjectBase< M>>) TypeLiteral.get(new ParameterizedTypeImpl(KafkaDeserializerForModelObjectBase.class,
																                                                 new Type[] {_valueClassType },
                                                                                                                 null));
	}
	@SuppressWarnings("unchecked")
	protected TypeLiteral<KafkaDeserializer<O>>  _deserializerForOidAsTypeLiteral() {
		return 	(TypeLiteral<KafkaDeserializer< O>>) TypeLiteral.get(new ParameterizedTypeImpl(KafkaDeserializer.class,
																                               new Type[] {_keyClassType },
                                                                                               null));
	}

	@SuppressWarnings("unchecked")
	protected TypeLiteral<KafkaDeserializer<O>>  _deserializerForOidImplAsTypeLiteral() {
		return 	(TypeLiteral<KafkaDeserializer< O>>) TypeLiteral.get(new ParameterizedTypeImpl(KafkaDeserializerForOIDBase.class,
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