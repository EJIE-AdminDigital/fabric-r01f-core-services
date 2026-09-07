package r01f.messaging.rabbitmq;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import r01f.annotations.Immutable;
import r01f.guids.OIDTyped;
import r01f.objectstreamer.annotations.MarshallType;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class RabbitMQIds {
/////////////////////////////////////////////////////////////////////////////////////////
//	BASE
/////////////////////////////////////////////////////////////////////////////////////////
	public static interface RabbitMQObjecID
					extends OIDTyped<String> {
		// just a marker interface
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	 RabbitMQChannel
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * RabbitMQChannel
	 */
	@Immutable
	@MarshallType(as="rabbitMQChannelID")
	public record RabbitMQChannel(@Getter String id)
	   implements RabbitMQObjecID {
		
		public static RabbitMQChannel from(final String id) {
			return new RabbitMQChannel(id);
		}
		public static RabbitMQChannel forId(final String id) {
			return new RabbitMQChannel(id);
		}
		public static RabbitMQChannel valueOf(final String str) {
			return new RabbitMQChannel(str);
		}
		public static RabbitMQChannel fromString(final String id) {
			return new RabbitMQChannel(id);
		}

		// Default channel existes for RabbitMQ named "" (empty string)
		public static final RabbitMQChannel DEFAULT = RabbitMQChannel.forId("");
		
		@Override
		public String asString() {
			return this.id;
		}
		@Override
		public String toString() {
			return this.id;
		}
	}
	/**
	 * RabbitMQChannel
	 */
	@Immutable
	@MarshallType(as="rabbitMQQueue")
	public record RabbitMQQueue(@Getter String id)
	   implements RabbitMQObjecID {

		public static RabbitMQQueue from(final String id) {
			return new RabbitMQQueue(id);
		}
		public static RabbitMQQueue forId(final String id) {
			return new RabbitMQQueue(id);
		}
		public static RabbitMQQueue valueOf(final String str) {
			return new RabbitMQQueue(str);
		}
		public static RabbitMQQueue fromString(final String id) {
			return new RabbitMQQueue(id);
		}
		public static final RabbitMQQueue DEFAULT = RabbitMQQueue.forId("default");
		
		@Override
		public String asString() {
			return this.id;
		}
		@Override
		public String toString() {
			return this.id;
		}
	}
}
