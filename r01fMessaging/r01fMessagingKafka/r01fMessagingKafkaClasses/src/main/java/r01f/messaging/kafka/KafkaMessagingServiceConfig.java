package r01f.messaging.kafka;

import java.util.Collection;
import java.util.Properties;

import r01f.messaging.MessagingServiceConfig;
import r01f.messaging.kafka.KafkaIDs.KafkaBootstrapServer;

public interface KafkaMessagingServiceConfig
		 extends MessagingServiceConfig {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	public Collection<KafkaBootstrapServer> getBootstrapServers();

	public Properties asProperties();
}
