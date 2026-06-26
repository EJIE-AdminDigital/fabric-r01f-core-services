package r01f.messaging.kafka.consumer;

import r01f.messaging.kafka.KafkaIDs.KafkaGroupID;
import r01f.messaging.kafka.KafkaIDs.KafkaTopic;
import r01f.messaging.kafka.KafkaMessagingServiceConfig;

public interface KafkaConsumerServiceConfig
		 extends KafkaMessagingServiceConfig {
/////////////////////////////////////////////////////////////////////////////////////////
// Custom Properties for Consumer see :
//	 @ org.apache.kafka.clients.consumer.ConsumerConfig
/////////////////////////////////////////////////////////////////////////////////////////	
	public KafkaTopic getDefaultTopic();

	public KafkaGroupID getDefaultGroupId();

	public int getMaxPollRecords();

	public int getAutoCommitInterval();

	public boolean isEnabledAutoCommit();
}
