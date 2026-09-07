package r01f.cloud.aws.s3.events.kafka.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.annotations.Immutable;
import r01f.cloud.aws.s3.events.kafka.AWSS3KafkaIDs.KafkaPartition;
import r01f.cloud.aws.s3.events.kafka.AWSS3KafkaIDs.KafkaTopic;
import r01f.cloud.aws.s3.events.model.AWSS3EventServiceSubscriber;
import r01f.cloud.aws.s3.events.model.AWSS3EventServiceSubscriberBase;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallFrom;
import r01f.objectstreamer.annotations.MarshallType;

/**
 * KafkaBootstrapServerConfig
 */
@Immutable
@NoArgsConstructor
@Accessors(prefix="_")
@MarshallType(as="kafkaMessageSubscriber")
public final class AWSS3KafkaMessageSubscriber
			extends AWSS3EventServiceSubscriberBase 
   implements AWSS3EventServiceSubscriber { //implements AWSS3EventServiceSubcriber
/////////////////////////////////////////////////////////////////////////////////////////
// 	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="kafkaTopic",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter  @Setter KafkaTopic _kafkaTopic;

	@MarshallField(as="kafkaPartition",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter KafkaPartition  _kafkaPartition;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AWSS3KafkaMessageSubscriber(@MarshallFrom("kafkaTopic")       final KafkaTopic kafkaTopic,
	                                   @MarshallFrom("kafkaPartition")   final KafkaPartition kafkaPartition ) {
		_kafkaTopic = kafkaTopic;
		_kafkaPartition = kafkaPartition;
	}
	public static AWSS3KafkaMessageSubscriber create() {
		return new AWSS3KafkaMessageSubscriber();
	}
	public AWSS3KafkaMessageSubscriber forTopic(final KafkaTopic topic) {
		this.setKafkaTopic(topic);
		return this;
	}
	public AWSS3KafkaMessageSubscriber partitition(final KafkaPartition  partition) {
	    this.setKafkaPartition(_kafkaPartition);
		return this;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	public boolean mustBeSentToPartition() {
		return ( this.getKafkaPartition() != null );
	}
}