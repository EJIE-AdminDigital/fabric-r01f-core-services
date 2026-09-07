package r01f.cloud.aws.s3.track.test;



import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;

import r01f.cloud.aws.s3.events.consumer.AWSS3EventNotification;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class Z99KafkaProducerConfig {

    // Recupera la dirección de tus brokers (ej: localhost:9092) desde el application.yml
    @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
    private String _bootstrapServers;

    /**
     * Configures the low-level Producer Factory specifying String keys 
     * and a native Jackson JSON Serializer for the notification payload.
     */
    @Bean
    public ProducerFactory<String, AWSS3EventNotification> s3NotificationProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        
      
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, "z99-s3-notification-consumer-group");
        configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
        
   
        configProps.put(ProducerConfig.ACKS_CONFIG, "all");  //
        configProps.put(ProducerConfig.RETRIES_CONFIG, 3);   //
        
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    /**
     * Exposes the KafkaTemplate as a managed Spring Bean ready to be 
     * injected directly into your AWSS3MetadataTrackPollingService.
     */
    @Bean
    public KafkaTemplate<String, AWSS3EventNotification> s3NotificationKafkaTemplate() {
        return new KafkaTemplate<>(s3NotificationProducerFactory());
    }
    
  

    @Bean
    public NewTopic s3EventsTopic() {
        return TopicBuilder.name("dena-proxy-metadata-applicant-kafka-topic")
				                .partitions(3)    
				                .replicas(1)    
				             .build();
    }
}