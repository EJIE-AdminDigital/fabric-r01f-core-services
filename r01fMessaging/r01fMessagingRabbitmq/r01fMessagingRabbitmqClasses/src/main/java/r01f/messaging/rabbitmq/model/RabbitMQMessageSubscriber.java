package r01f.messaging.rabbitmq.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.annotations.Immutable;
import r01f.messaging.model.MessageSubcriberBase;
import r01f.messaging.rabbitmq.RabbitMQIds.RabbitMQChannel;
import r01f.messaging.rabbitmq.RabbitMQIds.RabbitMQQueue;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallFrom;
import r01f.objectstreamer.annotations.MarshallType;

/**
 * RabbitMQMessageSubscriber
 */
@Immutable
@MarshallType(as="rabbitMQMessageSubscriber")
@Accessors(prefix="_")
public final class RabbitMQMessageSubscriber
		   extends MessageSubcriberBase { 
/////////////////////////////////////////////////////////////////////////////////////////
// MEMBERS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="queue",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter  @Setter RabbitMQQueue _queue;

	@MarshallField(as="channel",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter RabbitMQChannel _channel;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public RabbitMQMessageSubscriber() {
		// default no-args constructor
	}
	public RabbitMQMessageSubscriber(@MarshallFrom("queue") final RabbitMQQueue queue,
	                                 @MarshallFrom("channel") final RabbitMQChannel channel ) {
		_queue = queue;
		_channel = channel;
	}	
	public static RabbitMQMessageSubscriber create() {
		return new RabbitMQMessageSubscriber();
	}
	public RabbitMQMessageSubscriber forQueue(final RabbitMQQueue queue) {
		this.setQueue(queue);
		return this;
	}
	public RabbitMQMessageSubscriber partitition(final RabbitMQChannel  channel) {
	    this.setChannel(channel);
		return this;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	public boolean mustBeSentToChannel() {
		return this.getChannel() != null;
	}
}