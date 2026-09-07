package r01f.cloud.aws.s3.events.kafka;

import java.util.Collection;
import java.util.Properties;

import org.apache.kafka.clients.producer.ProducerConfig;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.cloud.aws.s3.events.kafka.AWSS3KafkaIDs.KafkaBootstrapServer;


@Accessors(prefix="_")
public abstract class AWSS3KafkaEventServiceConfigBase
	implements AWSS3KafkaEventServiceConfig {
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
