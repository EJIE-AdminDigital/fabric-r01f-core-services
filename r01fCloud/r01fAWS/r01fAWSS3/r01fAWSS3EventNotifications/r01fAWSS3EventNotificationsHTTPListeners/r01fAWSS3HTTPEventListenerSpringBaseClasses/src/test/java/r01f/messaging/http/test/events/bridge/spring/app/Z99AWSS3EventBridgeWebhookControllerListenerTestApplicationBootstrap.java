package r01f.messaging.http.test.events.bridge.spring.app;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.inject.Provider;
import lombok.extern.slf4j.Slf4j;
import r01f.cloud.aws.s3.bootstrap.spring.AWSS3SpringBootstrapConfiguration;
import r01f.cloud.aws.s3.client.api.AWSS3ClientAPIProvider;
import r01f.cloud.aws.s3.events.listener.AWSS3ObjectRetrieverAsync;
import r01f.cloud.aws.s3.events.listener.AWSS3ObjectRetrieverAsyncConfig;
import r01f.cloud.aws.s3.events.listener.AWSS3ObjectRetrieverAsyncImpl;
import r01f.internal.R01FAppCodes;
import r01f.messaging.http.test.model.Z99SecurityContextMock;
import r01f.model.annotations.ModelObjectsMarshaller;
import r01f.objectstreamer.Marshaller;
import r01f.objectstreamer.MarshallerBuilder;
import r01f.securitycontext.SecurityContext;
import r01f.services.bootstrap.annotations.api.SecurityContextProviderForSystemUser;

@Slf4j
@Configuration
public class Z99AWSS3EventBridgeWebhookControllerListenerTestApplicationBootstrap {
	
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
