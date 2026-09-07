package r01f.cloud.aws.s3.events.kafka.consumer;

import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.cloud.aws.s3.events.kafka.AWSS3KafkaEventServiceConfigBase;
import r01f.cloud.aws.s3.events.kafka.AWSS3KafkaIDs.KafkaBootstrapServer;
import r01f.cloud.aws.s3.events.kafka.AWSS3KafkaIDs.KafkaGroupID;
import r01f.cloud.aws.s3.events.kafka.AWSS3KafkaIDs.KafkaTopic;


@Accessors(prefix="_")
public abstract class AWSS3KafkaConsumerServiceConfigBase
		extends AWSS3KafkaEventServiceConfigBase
	implements AWSS3KafkaConsumerServiceConfig {
/////////////////////////////////////////////////////////////////////////////////////////
//FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter	@Setter	protected KafkaTopic _defaultTopic;
	@Getter	@Setter	protected KafkaGroupID _defaultGroupId;
	@Getter	@Setter	protected int   _maxPollRecords;
	@Getter	@Setter	protected int   _autoCommitInterval;
	@Getter	@Setter	protected boolean _enabledAutoCommit;
/////////////////////////////////////////////////////////////////////////////////////////
// METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Properties asProperties() {
		Properties properties = super.asProperties();
		properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,KafkaBootstrapServer.asCSVColectionString(_bootstrapServers) );
		properties.put(ConsumerConfig.GROUP_ID_CONFIG, _defaultGroupId.asString());
		properties.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, _maxPollRecords);
		properties.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, _autoCommitInterval);
		properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, _enabledAutoCommit);

		// default.topic :https://developers.redhat.com/articles/2022/06/02/how-create-kafka-consumers-and-producers-java#working_with_the_kafkaclient_properties
		properties.put("default.topic", _defaultTopic);
		return properties;
	}
}
