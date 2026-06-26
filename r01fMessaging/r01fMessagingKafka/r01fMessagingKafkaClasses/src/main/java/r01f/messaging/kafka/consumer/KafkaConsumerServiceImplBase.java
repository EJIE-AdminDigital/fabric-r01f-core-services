package r01f.messaging.kafka.consumer;

import java.util.Properties;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.Deserializer;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.messaging.kafka.serialization.KafkaDeserializer;

@Accessors(prefix="_")
public abstract class KafkaConsumerServiceImplBase<K,V>
		   implements KafkaConsumerService<K,V> {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////	
	@Getter final KafkaConsumer<K,V> _kafkaConsumer;
	@Getter final KafkaConsumerServiceConfig _consumerServiceConfig;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
	public KafkaConsumerServiceImplBase(final KafkaConsumerServiceConfig config,
			                            final KafkaDeserializer<K> keySerializer,final KafkaDeserializer<V> valueSerializer){
		_consumerServiceConfig = config;
		_kafkaConsumer = _buildConsumer(config,keySerializer,valueSerializer);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	protected KafkaConsumer<K,V> _buildConsumer(final KafkaConsumerServiceConfig config,
		                                        final Deserializer<K> keySerializer,final Deserializer<V> valueSerializer) {
		Properties props = _buildProperties(config);
		return new KafkaConsumer<>(props,keySerializer,valueSerializer);
	}
	protected static Properties  _buildProperties(final KafkaConsumerServiceConfig config) {
		return config.asProperties();
	}
}
