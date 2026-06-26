package r01f.messaging.kafka.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.annotations.Immutable;
import r01f.messaging.kafka.KafkaIDs.KafkaPartition;
import r01f.messaging.kafka.KafkaIDs.KafkaTopic;
import r01f.messaging.model.MessageSubcriberBase;
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
public final class KafkaMessageSubscriber
		   extends MessageSubcriberBase { //implements MessagingSubscriber
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
	public KafkaMessageSubscriber(@MarshallFrom("kafkaTopic")       final KafkaTopic kafkaTopic,
	                              @MarshallFrom("kafkaPartition")   final KafkaPartition kafkaPartition ) {
		_kafkaTopic = kafkaTopic;
		_kafkaPartition = kafkaPartition;
	}
	public static KafkaMessageSubscriber create() {
		return new KafkaMessageSubscriber();
	}
	public KafkaMessageSubscriber forTopic(final KafkaTopic topic) {
		this.setKafkaTopic(topic);
		return this;
	}
	public KafkaMessageSubscriber partitition(final KafkaPartition  partition) {
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