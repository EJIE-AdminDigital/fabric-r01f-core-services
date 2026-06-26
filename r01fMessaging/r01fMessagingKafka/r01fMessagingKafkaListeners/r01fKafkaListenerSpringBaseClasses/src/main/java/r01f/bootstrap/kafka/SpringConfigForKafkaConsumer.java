package r01f.bootstrap.kafka;

import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.MessageListenerContainer;

public interface SpringConfigForKafkaConsumer<K,V> {

	public ConsumerFactory<K, V> consumerFactory();

	public <C  extends MessageListenerContainer> KafkaListenerContainerFactory<C> kafkaListenerContainerFactory();


}
