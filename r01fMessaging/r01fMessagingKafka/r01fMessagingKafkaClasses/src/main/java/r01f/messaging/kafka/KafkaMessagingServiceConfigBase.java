package r01f.messaging.kafka;

import java.util.Collection;
import java.util.Properties;

import org.apache.kafka.clients.producer.ProducerConfig;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.messaging.kafka.KafkaIDs.KafkaBootstrapServer;


@Accessors(prefix="_")
public abstract class KafkaMessagingServiceConfigBase
	implements KafkaMessagingServiceConfig {
/////////////////////////////////////////////////////////////////////////////////////////
//FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter	@Setter	protected Collection<KafkaBootstrapServer> _bootstrapServers;
/////////////////////////////////////////////////////////////////////////////////////////
// METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Properties asProperties() {
		Properties properties = new Properties();
		properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
									KafkaBootstrapServer.asCSVColectionString(_bootstrapServers) );


		return properties;
	}
}
