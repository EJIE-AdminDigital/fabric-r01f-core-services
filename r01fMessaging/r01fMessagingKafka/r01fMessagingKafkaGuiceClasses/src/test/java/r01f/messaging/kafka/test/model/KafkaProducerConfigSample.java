package r01f.messaging.kafka.test.model;
import com.google.common.collect.Lists;

import r01f.messaging.kafka.KafkaIDs.KafkaBootstrapServer;
import r01f.messaging.kafka.producer.KafkaProducerServiceConfig;
import r01f.messaging.kafka.producer.KafkaProducerServiceConfigBase;
public class KafkaProducerConfigSample
	 extends KafkaProducerServiceConfigBase
  implements KafkaProducerServiceConfig {
/////////////////////////////////////////////////////////////////////////////////////////
//	... sample config to avoid any config file.... don't copy!
/////////////////////////////////////////////////////////////////////////////////////////	
	public KafkaProducerConfigSample() {
		_bootstrapServers =  Lists.newArrayList();
		_bootstrapServers.add(KafkaBootstrapServer.DEFAULT);
	}
}
