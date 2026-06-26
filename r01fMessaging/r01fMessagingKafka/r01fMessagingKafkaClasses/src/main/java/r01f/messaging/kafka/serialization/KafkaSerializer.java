package r01f.messaging.kafka.serialization;

import r01f.messaging.serialization.Serializer;

public interface KafkaSerializer<T>
	extends Serializer<T>,
	        org.apache.kafka.common.serialization.Serializer<T> {
	//
}
