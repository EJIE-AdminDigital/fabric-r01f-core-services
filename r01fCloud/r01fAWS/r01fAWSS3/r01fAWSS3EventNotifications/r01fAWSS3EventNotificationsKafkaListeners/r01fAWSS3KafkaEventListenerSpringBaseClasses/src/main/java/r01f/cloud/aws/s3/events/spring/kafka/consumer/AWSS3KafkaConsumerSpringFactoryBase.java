package r01f.cloud.aws.s3.events.spring.kafka.consumer;

import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.kafka.core.ConsumerFactory;

import com.google.common.collect.Maps;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.cloud.aws.s3.events.consumer.AWSS3EventNotification;
import r01f.cloud.aws.s3.events.kafka.consumer.AWSS3KafkaConsumerService;
import r01f.cloud.aws.s3.model.AWSS3ObjectKey;
import r01f.messaging.kafka.KafkaIDs.KafkaGroupID;


@Accessors(prefix="_")
public  class AWSS3KafkaConsumerSpringFactoryBase
		implements ConsumerFactory<AWSS3ObjectKey,AWSS3EventNotification> {

	@Getter	@Setter	protected  Map<String, Object> _configurationProperties = Maps.newHashMap();
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter	protected final  boolean _autoCommit;
    @Getter	protected final  AWSS3KafkaConsumerService _consumerService;
/////////////////////////////////////////////////////////////////////////////////////////
// CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	 public AWSS3KafkaConsumerSpringFactoryBase( final AWSS3KafkaConsumerService consumerService ) {
		 _consumerService = consumerService;
		 _autoCommit =  consumerService.getConsumerServiceConfig().isEnabledAutoCommit();
	 }
/////////////////////////////////////////////////////////////////////////////////////////
// METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Consumer<AWSS3ObjectKey,AWSS3EventNotification> createConsumer(final String groupId,
			                                                              final String clientIdPrefix, final String clientIdSuffix ,final Properties properties) {
		 // ..requires to set group-id ( just for internal validation ).
		_configurationProperties.put(ConsumerConfig.GROUP_ID_CONFIG, KafkaGroupID.DEFAULT.asString());
		// ...obtain KafkaConsumer from  KafakaConsumerService
		if (_consumerService == null ) {
			throw new IllegalStateException(" Cannot get instance of  KafkaConsumerService");
		}
		return _consumerService.getKafkaConsumer();
	}

}
