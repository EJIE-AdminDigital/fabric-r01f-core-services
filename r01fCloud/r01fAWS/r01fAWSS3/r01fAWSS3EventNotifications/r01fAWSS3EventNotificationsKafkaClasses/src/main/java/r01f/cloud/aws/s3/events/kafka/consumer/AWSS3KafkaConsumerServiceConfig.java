package r01f.cloud.aws.s3.events.kafka.consumer;

import r01f.cloud.aws.s3.events.kafka.AWSS3KafkaEventServiceConfig;
import r01f.cloud.aws.s3.events.kafka.AWSS3KafkaIDs.KafkaGroupID;
import r01f.cloud.aws.s3.events.kafka.AWSS3KafkaIDs.KafkaTopic;


public interface AWSS3KafkaConsumerServiceConfig
		 extends AWSS3KafkaEventServiceConfig {
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
