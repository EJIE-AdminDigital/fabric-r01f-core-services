package r01f.cloud.aws.s3.events.kafka.serialization;

import r01f.messaging.serialization.Serializer;

public interface KafkaSerializer<T>
	extends Serializer<T>,
	        org.apache.kafka.common.serialization.Serializer<T> {
	//
}
