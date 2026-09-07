package r01f.cloud.aws.s3.events.kafka;


import java.util.Collection;

import lombok.AccessLevel;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.url.Host;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;
import lombok.Getter;
import r01f.annotations.Immutable;
import r01f.guids.OID;
public abstract class AWSS3KafkaIDs {

/////////////////////////////////////////////////////////////////////////////////////////
//KAFKA BOOTSTRAP SERVER
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	* Kafka Bootstrap Server : Host and PORT
	*/

	@MarshallType(as="kafkaBootstrapServer")
	@Immutable
	public  static  record KafkaBootstrapServer( @Getter String id) implements IsAWSS3KafkaModelID {
		
		public static final KafkaBootstrapServer DEFAULT = forLoopbackAddressAndPort("[::1]",9092); // [::1] represents a ipv6 loopback

		
		public static KafkaBootstrapServer from(final String idAsString) {
			return new KafkaBootstrapServer(idAsString);
		}
		public static KafkaBootstrapServer forId(final String idAsString) {
			return new KafkaBootstrapServer(idAsString);
		}
		public static KafkaBootstrapServer valueOf(final String idAsString) {
			return new KafkaBootstrapServer(idAsString);
		}
		public static KafkaBootstrapServer fromString(final String idAsString) {
			return new KafkaBootstrapServer(idAsString);
		}
		public static <O extends OID> KafkaBootstrapServer of(final O oid) {
			return new KafkaBootstrapServer(oid.asString());
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
	public static  record KafkaGroupID(@Getter String id)
			implements IsAWSS3KafkaModelID {
		private static final long serialVersionUID = -6110073635719213157L;
		
		public static final KafkaGroupID DEFAULT = KafkaGroupID.forId("DEFAULT");
		
		public static KafkaGroupID forId(final String id) {
			return new KafkaGroupID(id);
		}
		public static KafkaGroupID valueOf(final String str) {
			return new KafkaGroupID(str);
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
//	KAFKA PARTITION
/////////////////////////////////////////////////////////////////////////////////////////
	@Immutable
	@MarshallType(as="kafkaPartition")
	public static final record KafkaPartition(@Getter String id)
			implements IsAWSS3KafkaModelID {
		private static final long serialVersionUID = -6110073635719213157L;
		
	///////////////////////////////////////////////////
	// BUILDERS
	//////////////////////////////////////////////////
		public static KafkaPartition forId(final Integer partition) {
			return new KafkaPartition(Integer.toString(partition));
		}
		public static KafkaPartition valueOf(final Integer partition) {
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
	public static final record KafkaTopic(@Getter String id)
			implements IsAWSS3KafkaModelID {
		private static final long serialVersionUID = -6110073635719213157L;
		
		// To create topic:
		//   $KAFKA_HOME/bin/kafka-topics.sh --create --topic   default-topic  --bootstrap-server localhost:9092
		public static final KafkaTopic DEFAULT = forId("default-topic");
	
		
	///////////////////////////////////////////////////
	// BUILDERS
	//////////////////////////////////////////////////
		public static KafkaTopic forId(final String id) {
			return new KafkaTopic(id);
		}
		public static KafkaTopic valueOf(final String id) {
			return new KafkaTopic(id);
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
}
