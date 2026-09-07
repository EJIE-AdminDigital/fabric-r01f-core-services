package r01f.messaging.kafka;

import java.util.Collection;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import r01f.annotations.Immutable;
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
/////////////////////////////////////////////////////////////////////////////////////////
//KAFKA BOOTSTRAP SERVER
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	* Kafka Bootstrap Server : Host and PORT
	*/
	@Immutable
	@MarshallType(as="kafkaBootstrapServer")
	public record KafkaBootstrapServer(@Getter String id)
	   implements KafkaModelObjecID {

		public static KafkaBootstrapServer from(final String id) {
			return new KafkaBootstrapServer(id);
		}
		public static KafkaBootstrapServer forId(final String id) {
			return new KafkaBootstrapServer(id);
		}
		public static KafkaBootstrapServer valueOf(final String id) {
			return new KafkaBootstrapServer(id);
		}
		public static KafkaBootstrapServer fromString(final String id) {
			return new KafkaBootstrapServer(id);
		}
		public static KafkaBootstrapServer forHostAndPort(final Host host, final int port) {
			return new KafkaBootstrapServer(Strings.customized("{}:{}", host.asString(),port));
		}
		public static KafkaBootstrapServer forLoopbackAddressAndPort(final String loopbackAddress , final int port) {
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

		@Override
		public String asString() {
			return this.id;
		}
		@Override
		public String toString() {
			return this.id;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	KAFKA GROUP ID
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * GroupID
	 */
	@Immutable
	@MarshallType(as="groupId")
	public record KafkaGroupID(@Getter String id)
	   implements KafkaModelObjecID {
		
		public static KafkaGroupID from(final String id) {
			return new KafkaGroupID(id);
		}
		public static KafkaGroupID forId(final String id) {
			return new KafkaGroupID(id);
		}
		public static KafkaGroupID valueOf(final String str) {
			return new KafkaGroupID(str);
		}
		public static KafkaGroupID fromString(final String id) {
			return new KafkaGroupID(id);
		}

		public static final KafkaGroupID DEFAULT = KafkaGroupID.forId("DEFAULT");
		
		@Override
		public String asString() {
			return this.id;
		}
		@Override
		public String toString() {
			return this.id;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	KAFKA PARTITION
/////////////////////////////////////////////////////////////////////////////////////////
	@Immutable
	@MarshallType(as="kafkaPartition")
	public record KafkaPartition(@Getter String id)
	   implements KafkaModelObjecID {
		
		public static KafkaPartition from(final Integer partition) {
			return new KafkaPartition(Integer.toString(partition));
		}
		public static KafkaPartition forId(final Integer partition) {
			return new KafkaPartition(Integer.toString(partition));
		}
		public static KafkaPartition valueOf(final Integer partition) {
			return new KafkaPartition(Integer.toString(partition));
		}
		public static KafkaPartition fromString(final Integer partition) {
			return new KafkaPartition(Integer.toString(partition));
		}
		
		@Override
		public String asString() {
			return this.id;
		}
		@Override
		public String toString() {
			return this.id;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	KAFKA TOPIC
/////////////////////////////////////////////////////////////////////////////////////////
	@Immutable
	@MarshallType(as="kafkaTopic")
	public record KafkaTopic(@Getter String id)
	   implements KafkaModelObjecID {
		
		public static KafkaTopic from(final String id) {
			return new KafkaTopic(id);
		}
		public static KafkaTopic forId(final String id) {
			return new KafkaTopic(id);
		}
		public static KafkaTopic valueOf(final String str) {
			return new KafkaTopic(str);
		}
		public static KafkaTopic fromString(final String id) {
			return new KafkaTopic(id);
		}
		// To create topic:
		//   $KAFKA_HOME/bin/kafka-topics.sh --create --topic   default-topic  --bootstrap-server localhost:9092
		public static final KafkaTopic DEFAULT = KafkaTopic.forId("default-topic");
		
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
