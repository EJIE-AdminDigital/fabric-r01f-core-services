package r01f.messaging.rabbitmq;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import r01f.annotations.Immutable;
import r01f.guids.OIDBaseMutable;
import r01f.guids.OIDTyped;
import r01f.objectstreamer.annotations.MarshallType;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class RabbitMQIds {
/////////////////////////////////////////////////////////////////////////////////////////
//	BASE
/////////////////////////////////////////////////////////////////////////////////////////
	public static interface RabbitMQModelObjecID
					 extends OIDTyped<String> {
		// just a marker interface
	}
	/**
	 * Base for every RABBIT ID's
	 */
	@Immutable
	@NoArgsConstructor
	public static abstract class RabbitMQModelObjecIDBase
						 extends OIDBaseMutable<String>
					  implements RabbitMQModelObjecID {
		private static final long serialVersionUID = 4162366466990455545L;

		public RabbitMQModelObjecIDBase(final String id) {
			super(id);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	 RabbitMQChannel
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * RabbitMQChannel
	 */
	@Immutable
	@MarshallType(as="rabbitMQChannelID")
	@NoArgsConstructor
	public static final class RabbitMQChannel
					  extends RabbitMQModelObjecIDBase {
		private static final long serialVersionUID = -6110073635719213157L;
		public RabbitMQChannel(final String oid) {
			super(oid);
		}
		public static RabbitMQChannel forId(final String id) {
			return new RabbitMQChannel(id);
		}
		public static RabbitMQChannel valueOf(final String str) {
			return new RabbitMQChannel(str);
		}

		// Default channel existes for RabbitMQ named "" (empty string)
		public static final RabbitMQChannel DEFAULT = RabbitMQChannel.forId("");
	}


	/**
	 * RabbitMQChannel
	 */
	@Immutable
	@MarshallType(as="rabbitMQQueue")
	@NoArgsConstructor
	public static final class RabbitMQQueue
					  extends RabbitMQModelObjecIDBase {

		private static final long serialVersionUID = -6110073635719213157L;
		public RabbitMQQueue(final String oid) {
			super(oid);
		}
		public static RabbitMQQueue forId(final String id) {
			return new RabbitMQQueue(id);
		}
		public static RabbitMQQueue valueOf(final String str) {
			return new RabbitMQQueue(str);
		}
		public static final RabbitMQQueue DEFAULT = RabbitMQQueue.forId("default");
	}



}
