package r01f.cloud.aws.s3.events.bootstrap.kafka;

import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.MessageListenerContainer;

public interface AWSS3SpringConfigForKafkaConsumer<K,V> {

	public ConsumerFactory<K, V> consumerFactory();

	public <C  extends MessageListenerContainer> KafkaListenerContainerFactory<C> kafkaListenerContainerFactory();


}
