package r01f.messaging.kafka;

import java.util.Collection;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import r01f.annotations.Immutable;
import r01f.guids.OIDBaseMutable;
import r01f.guids.OIDTyped;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.url.Host;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class KafkaIDs {
/////////////////////////////////////////////////////////////////////////////////////////
//	BASE
/////////////////////////////////////////////////////////////////////////////////////////
	public static interface KafkaModelObjecID
					 extends OIDTyped<String> {
		// just a marker interface
	}
	/**
	 * Base for every Kafka ID's
	 */
	@Immutable
	@NoArgsConstructor
	public static abstract class KafkaModelObjecIDBase
						 extends OIDBaseMutable<String>
					  implements KafkaModelObjecID {
		private static final long serialVersionUID = 4162366466990455545L;

		public KafkaModelObjecIDBase(final String id) {
			super(id);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//KAFKA BOOTSTRAP SERVER
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	* Kafka Bootstrap Server : Host and PORT
	*/
	@Immutable
	@MarshallType(as="kafkaBootstrapServer")
	@NoArgsConstructor
	public static final class KafkaBootstrapServer
		extends KafkaModelObjecIDBase {

		private static final long serialVersionUID = -1258361406057125328L;

		public KafkaBootstrapServer(final String oid) {
			super(oid);
		}
		public static KafkaBootstrapServer forHostAndPort(final Host host, int port) {
			return new KafkaBootstrapServer(Strings.customized("{}:{}", host.asString(),port));
		}
		public static KafkaBootstrapServer forLoopbackAddressAndPort(final String loopbackAddress , int port) {
			/*
			 * The only fix I found that doesn't involve changing configs every time you reboot (per the "172.*" suggestion above)
			 *  is to use the IPv6 loopback address ::1 in both the Kafka server config running in Linux and the Java client in Windows.
			 *  https://stackoverflow.com/questions/64177422/unable-to-produce-to-kafka-topic-that-is-running-on-wsl-2-from-windows
			 */
			return new KafkaBootstrapServer(Strings.customized("{}:{}", loopbackAddress,port));
		}

		public static String asCSVColectionString(final Collection<KafkaBootstrapServer> bootstrapServers ) {
			StringBuffer hosts = new StringBuffer();
			if (! CollectionUtils.isNullOrEmpty(bootstrapServers))
				bootstrapServers.forEach( c -> {
								    	  hosts.append(hosts.length() == 0 ? c.toString()
								    			  						    	:
													    			        Strings.customized(", {} ", c.toString()));

							      });
			return hosts.toString();
		}
		public static final KafkaBootstrapServer DEFAULT = KafkaBootstrapServer.forLoopbackAddressAndPort("[::1]",9092); // [::1] represents a ipv6 loopback


	}
/////////////////////////////////////////////////////////////////////////////////////////
//	KAFKA GROUP ID
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * GroupID
	 */
	@Immutable
	@MarshallType(as="groupId")
	@NoArgsConstructor
	public static final class KafkaGroupID
					  extends KafkaModelObjecIDBase {
		private static final long serialVersionUID = -6110073635719213157L;
		public KafkaGroupID(final String oid) {
			super(oid);
		}
		public static KafkaGroupID forId(final String id) {
			return new KafkaGroupID(id);
		}
		public static KafkaGroupID valueOf(final String str) {
			return new KafkaGroupID(str);
		}

		public static final KafkaGroupID DEFAULT = KafkaGroupID.forId("DEFAULT");
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	KAFKA PARTITION
/////////////////////////////////////////////////////////////////////////////////////////
	@Immutable
	@MarshallType(as="kafkaPartition")
	public static final class KafkaPartition
					  extends KafkaModelObjecIDBase {
		private static final long serialVersionUID = -6110073635719213157L;
	///////////////////////////////////////////////////
	// CONSTRUCTOR
	//////////////////////////////////////////////////
		public KafkaPartition(final String oid) {
			super(oid);
		}
	///////////////////////////////////////////////////
	// BUILDERS
	//////////////////////////////////////////////////
		public static KafkaPartition forId(final Integer partition) {
			return new KafkaPartition(Integer.toString(partition));
		}
		public static KafkaPartition valueOf(final Integer partition) {
			return new KafkaPartition(Integer.toString(partition));
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	KAFKA TOPIC
/////////////////////////////////////////////////////////////////////////////////////////
	@Immutable
	@MarshallType(as="kafkaTopic")
	public static final class KafkaTopic
					  extends KafkaModelObjecIDBase {
		private static final long serialVersionUID = -6110073635719213157L;
	///////////////////////////////////////////////////
	// CONSTRUCTOR
	//////////////////////////////////////////////////
		public KafkaTopic(final String oid) {
			super(oid);
		}
	///////////////////////////////////////////////////
	// BUILDERS
	//////////////////////////////////////////////////
		public static KafkaTopic forId(final String id) {
			return new KafkaTopic(id);
		}
		public static KafkaTopic valueOf(final String str) {
			return new KafkaTopic(str);
		}
		// To create topic:
		//   $KAFKA_HOME/bin/kafka-topics.sh --create --topic   default-topic  --bootstrap-server localhost:9092
		public static final KafkaTopic DEFAULT = KafkaTopic.forId("default-topic");
	}
}
