package r01f.messaging.kafka.test;
import com.google.common.collect.Lists;

import r01f.cloud.aws.s3.events.kafka.AWSS3KafkaIDs.KafkaBootstrapServer;
import r01f.cloud.aws.s3.events.kafka.AWSS3KafkaIDs.KafkaGroupID;
import r01f.cloud.aws.s3.events.kafka.AWSS3KafkaIDs.KafkaTopic;
import r01f.cloud.aws.s3.events.kafka.consumer.AWSS3KafkaConsumerServiceConfig;
import r01f.cloud.aws.s3.events.kafka.consumer.AWSS3KafkaConsumerServiceConfigBase;
public class Z99S3KafkaEventNotificationConsumerConfig
	 extends  AWSS3KafkaConsumerServiceConfigBase
  implements  AWSS3KafkaConsumerServiceConfig {
	
/////////////////////////////////////////////////////////////////////////////////////////
//	... sample config to avoid any config file.... don't copy!
/////////////////////////////////////////////////////////////////////////////////////////	
	public Z99S3KafkaEventNotificationConsumerConfig() {
		_bootstrapServers =  Lists.newArrayList();
		_bootstrapServers.add(KafkaBootstrapServer.DEFAULT);
		_enabledAutoCommit = true;
		_autoCommitInterval = 10000;
		_maxPollRecords = 1;
		_defaultTopic = KafkaTopic.DEFAULT;
		_defaultGroupId = KafkaGroupID.DEFAULT;
	}
}
