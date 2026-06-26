package r01f.spring.kafka.consumer;

import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.kafka.core.ConsumerFactory;

import com.google.common.collect.Maps;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.messaging.kafka.KafkaIDs.KafkaGroupID;
import r01f.messaging.kafka.consumer.KafkaConsumerService;

@Accessors(prefix="_")
public  class KafkaConsumerSpringFactoryBase<K,V>
		implements ConsumerFactory<K, V> {

	@Getter	@Setter	protected  Map<String, Object> _configurationProperties = Maps.newHashMap();
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter	protected final  boolean _autoCommit;
    @Getter	protected final KafkaConsumerService<K, V> _consumerService;
/////////////////////////////////////////////////////////////////////////////////////////
// CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	 public KafkaConsumerSpringFactoryBase( final KafkaConsumerService<K, V> consumerService ) {
		 _consumerService = consumerService;
		 _autoCommit =  consumerService.getConsumerServiceConfig().isEnabledAutoCommit();
	 }
/////////////////////////////////////////////////////////////////////////////////////////
// METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Consumer<K, V> createConsumer(final String groupId,
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
