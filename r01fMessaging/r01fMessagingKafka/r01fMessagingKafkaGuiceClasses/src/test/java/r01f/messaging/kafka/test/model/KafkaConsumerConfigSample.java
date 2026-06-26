package r01f.messaging.kafka.test.model;
import com.google.common.collect.Lists;

import r01f.messaging.kafka.KafkaIDs.KafkaBootstrapServer;
import r01f.messaging.kafka.KafkaIDs.KafkaGroupID;
import r01f.messaging.kafka.KafkaIDs.KafkaTopic;
import r01f.messaging.kafka.consumer.KafkaConsumerServiceConfig;
import r01f.messaging.kafka.consumer.KafkaConsumerServiceConfigBase;
public class KafkaConsumerConfigSample
	 extends KafkaConsumerServiceConfigBase
  implements  KafkaConsumerServiceConfig {
/////////////////////////////////////////////////////////////////////////////////////////
//	... sample config to avoid any config file.... don't copy!
/////////////////////////////////////////////////////////////////////////////////////////	
	public KafkaConsumerConfigSample() {
		_bootstrapServers =  Lists.newArrayList();
		_bootstrapServers.add(KafkaBootstrapServer.DEFAULT);
		_enabledAutoCommit = true;
		_autoCommitInterval = 10000;
		_maxPollRecords = 1;
		_defaultTopic = KafkaTopic.DEFAULT;
		_defaultGroupId = KafkaGroupID.DEFAULT;
	}
}
