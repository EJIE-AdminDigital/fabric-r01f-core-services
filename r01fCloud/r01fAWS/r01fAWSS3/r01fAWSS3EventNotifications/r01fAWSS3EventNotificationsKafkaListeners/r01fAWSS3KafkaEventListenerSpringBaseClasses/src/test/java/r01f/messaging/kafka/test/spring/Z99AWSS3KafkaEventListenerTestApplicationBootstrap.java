package r01f.messaging.kafka.test.spring;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import jakarta.inject.Provider;
import lombok.extern.slf4j.Slf4j;
import r01f.cloud.aws.s3.bootstrap.spring.AWSS3SpringBootstrapConfiguration;
import r01f.cloud.aws.s3.client.api.AWSS3ClientAPIProvider;
import r01f.cloud.aws.s3.events.consumer.AWSS3EventNotification;
import r01f.cloud.aws.s3.events.kafka.serialization.AWSS3KafkaDeserializerForS3EventNotification;
import r01f.cloud.aws.s3.events.kafka.serialization.AWSS3KafkaDeserializerForS3ObjectKey;
import r01f.cloud.aws.s3.events.listener.AWSS3ObjectRetrieverAsync;
import r01f.cloud.aws.s3.events.listener.AWSS3ObjectRetrieverAsyncConfig;
import r01f.cloud.aws.s3.events.listener.AWSS3ObjectRetrieverAsyncImpl;
import r01f.cloud.aws.s3.model.AWSS3ObjectKey;
import r01f.internal.R01FAppCodes;
import r01f.messaging.kafka.test.model.Z99SecurityContextMock;
import r01f.model.annotations.ModelObjectsMarshaller;
import r01f.objectstreamer.Marshaller;
import r01f.objectstreamer.MarshallerBuilder;
import r01f.securitycontext.SecurityContext;
import r01f.services.bootstrap.annotations.api.SecurityContextProviderForSystemUser;

@Slf4j
@Configuration
@EnableKafka
public class Z99AWSS3KafkaEventListenerTestApplicationBootstrap {
	
    @Bean @ModelObjectsMarshaller
    public Marshaller marshaller() {
    	return MarshallerBuilder.findTypesToMarshallAt(R01FAppCodes.APP_CODE).build();
    }
    
    @Bean 
	@SecurityContextProviderForSystemUser // 
	public Provider<SecurityContext> systemSecurityContextProvider(){		
		return new Provider<SecurityContext>() {
						@Override
						public SecurityContext get() {				
							return new Z99SecurityContextMock();
						}
			
		};
	}    
////////////////////////////////////////////////////////////////////////
///  KAFKA
////////////////////////////////////////////////////////////////////////
    @SuppressWarnings("static-method")
    @Bean
    public ConcurrentKafkaListenerContainerFactory<AWSS3ObjectKey, AWSS3EventNotification> kafkaListenerContainerFactory( final  Marshaller  marshaller) {

        // 1. Configuración manual de las propiedades básicas de Kafka en un mapa de Java
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "z99-s3-notification-consumer-group");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        // 2. Crear la factoría pasando las instancias reales de tus objetos deserializadores
        ConsumerFactory<AWSS3ObjectKey, AWSS3EventNotification> consumerFactory = 
                new DefaultKafkaConsumerFactory<>(props, 
						                	 	 new AWSS3KafkaDeserializerForS3ObjectKey(), 
						                		 new AWSS3KafkaDeserializerForS3EventNotification(marshaller));
        
        

        // 3. Spring Container to Handle @KafkaListener
        ConcurrentKafkaListenerContainerFactory<AWSS3ObjectKey, AWSS3EventNotification> factory = 
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        
        return factory;
    }
////////////////////////////////////////////////////////////////////////
///  S3 
////////////////////////////////////////////////////////////////////////
	@Bean
	@ConfigurationProperties(prefix = AWSS3SpringBootstrapConfiguration.PROPERTIES_PREFIX) 
	protected AWSS3SpringBootstrapConfiguration s3ServiceCOnfig() {
		return new AWSS3SpringBootstrapConfiguration();
	}
	
	@Bean
	@ConfigurationProperties(prefix = AWSS3ObjectRetrieverAsyncConfig.PROPERTIES_PREFIX) 
	protected AWSS3ObjectRetrieverAsyncConfig s3RetrieverAsyncConfig() {
		return new AWSS3ObjectRetrieverAsyncConfig();
	}
	
	
	@Bean
	protected AWSS3ClientAPIProvider s3apiProvider(final AWSS3SpringBootstrapConfiguration s3Config) {
		if (s3Config == null ) {
			throw new IllegalArgumentException("In order to create S3 API Provider, a bootstrap config with endpoints must be provided");
		}		
		log.warn("######################################################################");
		log.warn("... Initializing S3 API Provider from MULTIPLE YAML endpoints");
		log.warn("######################################################################");
		
		return new AWSS3ClientAPIProvider(s3Config.toClientConfigSet());
	}
	
	@Bean	
	protected AWSS3ObjectRetrieverAsync s3objectRetrieverAsync(final AWSS3ClientAPIProvider s3ClientAPIProvider,
			                                                   final AWSS3ObjectRetrieverAsyncConfig asyncConfig ) {
		return new AWSS3ObjectRetrieverAsyncImpl(s3ClientAPIProvider, asyncConfig);
	}
	
	

}
