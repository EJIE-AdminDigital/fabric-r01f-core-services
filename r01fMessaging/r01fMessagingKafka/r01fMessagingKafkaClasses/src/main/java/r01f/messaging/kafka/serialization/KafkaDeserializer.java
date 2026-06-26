package r01f.messaging.kafka.serialization;

import r01f.messaging.serialization.Deserializer;

public interface KafkaDeserializer<T>
	extends Deserializer<T>,
	        org.apache.kafka.common.serialization.Deserializer<T> {
	//
}
