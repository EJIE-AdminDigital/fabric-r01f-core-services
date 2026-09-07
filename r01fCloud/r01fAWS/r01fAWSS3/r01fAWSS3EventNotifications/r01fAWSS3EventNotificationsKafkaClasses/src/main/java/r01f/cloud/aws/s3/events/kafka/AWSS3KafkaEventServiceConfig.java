package r01f.cloud.aws.s3.events.kafka;

import java.util.Collection;
import java.util.Properties;

import r01f.cloud.aws.s3.events.AWSS3EventServiceConfig;
import r01f.cloud.aws.s3.events.kafka.AWSS3KafkaIDs.KafkaBootstrapServer;

public interface AWSS3KafkaEventServiceConfig
		 extends AWSS3EventServiceConfig {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	public Collection<KafkaBootstrapServer> getBootstrapServers();

	public Properties asProperties();
}
