package r01f.cloud.aws.s3.events.kafka.consumer;

import java.util.Properties;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.Deserializer;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.cloud.aws.s3.events.consumer.AWSS3EventNotification;
import r01f.cloud.aws.s3.events.kafka.serialization.AWS3KafkaDeserializer;
import r01f.cloud.aws.s3.events.kafka.serialization.AWSS3KafkaDeserializerForS3EventNotification;
import r01f.cloud.aws.s3.events.kafka.serialization.AWSS3KafkaDeserializerForS3ObjectKey;
import r01f.cloud.aws.s3.model.AWSS3ObjectKey;
import r01f.objectstreamer.Marshaller;

@Accessors(prefix="_")
public  abstract class AWSS3KafkaConsumerServiceImplBase
		   implements AWSS3KafkaConsumerService {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////	
	@Getter final KafkaConsumer<AWSS3ObjectKey,AWSS3EventNotification> _kafkaConsumer;
	@Getter final AWSS3KafkaConsumerServiceConfig _consumerServiceConfig;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
	public AWSS3KafkaConsumerServiceImplBase(final AWSS3KafkaConsumerServiceConfig config,
			                                 final Marshaller marshaller){
		_consumerServiceConfig = config;
		_kafkaConsumer = _buildConsumer(config,
				                        new AWSS3KafkaDeserializerForS3ObjectKey(),
				                        new AWSS3KafkaDeserializerForS3EventNotification(marshaller));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	@SuppressWarnings("static-method")
	protected KafkaConsumer<AWSS3ObjectKey,AWSS3EventNotification> _buildConsumer(final AWSS3KafkaConsumerServiceConfig config,
		                                                                          final AWSS3KafkaDeserializerForS3ObjectKey keySerializer,
		                                                                          final AWSS3KafkaDeserializerForS3EventNotification valueSerializer) {
		Properties props = _buildProperties(config);
		return new KafkaConsumer<>(props,keySerializer,valueSerializer);
	}
	protected static Properties  _buildProperties(final AWSS3KafkaConsumerServiceConfig config) {
		return config.asProperties();
	}
}
