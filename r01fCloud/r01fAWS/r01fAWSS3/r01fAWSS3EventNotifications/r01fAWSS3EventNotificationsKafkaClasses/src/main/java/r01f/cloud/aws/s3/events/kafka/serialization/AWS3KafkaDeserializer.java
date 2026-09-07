package r01f.cloud.aws.s3.events.kafka.serialization;

import r01f.cloud.aws.s3.events.serialization.AWSS3Deserializer;

public interface AWS3KafkaDeserializer<T>
	extends AWSS3Deserializer<T>,
	        org.apache.kafka.common.serialization.Deserializer<T> {
	//
}
