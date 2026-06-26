package r01f.core.messaging.kafka.producer.bootstrap;

import java.lang.reflect.Type;

import com.google.inject.TypeLiteral;

import r01f.facets.HasOID;
import r01f.generics.ParameterizedTypeImpl;
import r01f.guids.OID;
import r01f.messaging.kafka.producer.KafkaProducerService;
import r01f.messaging.kafka.producer.KafkaProducerServiceConfig;
import r01f.messaging.kafka.producer.KafkaProducerServiceForModelObjectImpl;
import r01f.messaging.kafka.serialization.KafkaSerializerForOIDBase;
import r01f.messaging.kafka.serialization.KafkaSerializer;
import r01f.messaging.kafka.serialization.KafkaSerializerForModelObjectBase;
import r01f.model.ModelObject;


public abstract class KafkaProducerGuiceModuleForModelObjectBase<O extends OID, M extends ModelObject & HasOID<O>>
		extends KafkaProducerGuiceModuleBase<O,M> {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public KafkaProducerGuiceModuleForModelObjectBase(final Class<O> keyClassType,
			                                          final Class<M> valueClassType,
			                                          final KafkaProducerServiceConfig cfg) {
		super(keyClassType, valueClassType, cfg);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	PRODUCER SERVICES  ( as-type-literals)
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unchecked")
	protected TypeLiteral<KafkaProducerService<O,M>>  _producerServiceAsTypeLiteral() {
		return (TypeLiteral<KafkaProducerService<O, M>>) TypeLiteral.get(new ParameterizedTypeImpl(KafkaProducerService.class,
				                                                                                   new Type[] { _keyClassType, _valueClassType} ,
                                                                                                   null));
	}

	@SuppressWarnings("unchecked")
	protected TypeLiteral<KafkaProducerServiceForModelObjectImpl<O, M>>  _producerServiceImplAsTypeLiteral() {	;
		return  (TypeLiteral<KafkaProducerServiceForModelObjectImpl<O, M>>) TypeLiteral.get(new ParameterizedTypeImpl(KafkaProducerServiceForModelObjectImpl.class,
                                                                                                                       new Type[]  { _keyClassType, _valueClassType},
                                                                                                                       null));
	}
/////////////////////////////////////////////////////////////////////////////////////////
// SERIALIZERS  ( as-type-literals)
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unchecked")
	protected TypeLiteral<KafkaSerializer<M>>  _serializerForModelObjectAsTypeLiteral() {
		return 	(TypeLiteral<KafkaSerializer< M>>) TypeLiteral.get(new ParameterizedTypeImpl(KafkaSerializer.class,
																                             new Type[] {_valueClassType },
                                                                                             null));
	}
	@SuppressWarnings("unchecked")
	protected TypeLiteral<KafkaSerializerForModelObjectBase<M>>  _serializerForModelObjectImplAsTypeLiteral() {
		return 	(TypeLiteral<KafkaSerializerForModelObjectBase< M>>) TypeLiteral.get(new ParameterizedTypeImpl(KafkaSerializerForModelObjectBase.class,
																                                               new Type[] {_valueClassType },
                                                                                                               null));
	}
	@SuppressWarnings("unchecked")
	protected TypeLiteral<KafkaSerializer<O>>  _serializerForOidAsTypeLiteral() {
		return 	(TypeLiteral<KafkaSerializer< O>>) TypeLiteral.get(new ParameterizedTypeImpl(KafkaSerializer.class,
																                             new Type[] {_keyClassType },
                                                                                             null));
	}

	@SuppressWarnings("unchecked")
	protected TypeLiteral<KafkaSerializer<O>>  _serializerForOidImplAsTypeLiteral() {
		return 	(TypeLiteral<KafkaSerializer< O>>) TypeLiteral.get(new ParameterizedTypeImpl(KafkaSerializerForOIDBase.class,
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
