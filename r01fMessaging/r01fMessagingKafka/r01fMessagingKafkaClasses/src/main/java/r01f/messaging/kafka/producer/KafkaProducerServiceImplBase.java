package r01f.messaging.kafka.producer;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Serializer;

import r01f.messaging.kafka.KafkaMessagingServiceConfig;
import r01f.messaging.kafka.model.KafkaMessageSubscriber;
import r01f.messaging.kafka.serialization.KafkaSerializer;
import r01f.messaging.model.MessagingFactory;
import r01f.messaging.producer.ProducerServiceResponse;
import r01f.messaging.producer.ProducerServiceResponseOK;

public abstract class KafkaProducerServiceImplBase<K,V>
		   implements KafkaProducerService<K,V> {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////	
	final KafkaProducer<K,V> _kafkaProducer;
	final KafkaMessagingServiceConfig _config;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
	public KafkaProducerServiceImplBase(final KafkaProducerServiceConfig config,
			                            final KafkaSerializer<K> keySerializer,final KafkaSerializer<V> valueSerializer) {
		_config = config;
		_kafkaProducer = _buildProducer(config,
				                        keySerializer,valueSerializer);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	protected abstract K keyFrom(final V value);
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	protected KafkaProducer<K,V> _buildProducer(final KafkaMessagingServiceConfig config,
		                                        final Serializer<K> keySerializer,final Serializer<V> valueSerializer) {
		Properties props = _buildProperties(config);
		return new KafkaProducer<>(props,
				                   keySerializer,valueSerializer);
	}
	protected static Properties  _buildProperties(final KafkaMessagingServiceConfig config) {
		return config.asProperties();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public ProducerServiceResponse<KafkaMessageSubscriber> send(final KafkaMessageSubscriber subscriber,
			                                                    final MessagingFactory<V> messageToBeDeliveredFactory) {
		try {
			// [ 0 ] The key and the object
			K  key =    keyFrom(messageToBeDeliveredFactory.getValue());
			V  object = messageToBeDeliveredFactory.getValue();
			// [ 1 ]  Compose Producer Record
			ProducerRecord<K,V> productRecord = new ProducerRecord<>(subscriber.getKafkaTopic().asString(),
					                     							 key,
					                     							 object);
			_kafkaProducer.send(productRecord)
					      .get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return new ProducerServiceResponseOK<>(subscriber);
	}
}
