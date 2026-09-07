package r01f.cloud.aws.s3.track.test;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;
import r01f.cloud.aws.s3.bootstrap.spring.AWSS3SpringBootstrapConfiguration;
import r01f.cloud.aws.s3.client.api.AWSS3ClientAPI;
import r01f.cloud.aws.s3.client.api.AWSS3ClientAPIProvider;
import r01f.cloud.aws.s3.track.client.api.AWSS3ClientAPIForTrack;
import r01f.cloud.aws.s3.track.client.api.AWSS3ClientAPIProviderForTrack;
import r01f.cloud.aws.s3.track.spring.service.polling.AWSS3MetadataTrackPollingConfig;
import r01f.internal.R01FAppCodes;
import r01f.objectstreamer.Marshaller;
import r01f.objectstreamer.MarshallerBuilder;

@Slf4j
@Configuration
public class Z99SpringBootstrapConfigurationForTest  {

/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	 @Bean
	   public Marshaller marshaller() {
	    return MarshallerBuilder.findTypesToMarshallAt(R01FAppCodes.APP_CODE).build();
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
	protected AWSS3ClientAPIProvider s3apiProvider(final AWSS3SpringBootstrapConfiguration s3Config) {
        if (s3Config == null ) {
            throw new IllegalArgumentException("In order to create S3 API Provider, a bootstrap config with endpoints must be provided");
        }

        log.warn("######################################################################");
        log.warn("... Initializing S3 API Provider from MULTIPLE YAML endpoints");
        log.warn("######################################################################");
   
        return new AWSS3ClientAPIProvider(s3Config.toClientConfigSet());
    }
////////////////////////////////////////////////////////////////////////
///  S3  TRACK
////////////////////////////////////////////////////////////////////////
	
    @Bean    
    protected AWSS3ClientAPIProviderForTrack s3TrackApi(final AWSS3ClientAPIProvider s3Api) {    	
    	return new AWSS3ClientAPIProviderForTrack(s3Api);
    }
    
	@Bean
	@ConfigurationProperties(prefix = AWSS3MetadataTrackPollingConfig.PROPERTIES_PREFIX) 
	protected AWSS3MetadataTrackPollingConfig s3PollingConfig() {
	    return new AWSS3MetadataTrackPollingConfig();
	}
}
