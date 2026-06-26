package r01f.cloud.aws;

import java.nio.charset.Charset;
import java.util.Properties;
import java.util.Set;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.config.ContainsConfigData;
import software.amazon.awssdk.regions.Region;

@Slf4j
@Accessors(prefix="_")
public class AWSClientConfig
  implements ContainsConfigData {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter protected final Region _region;
	@Getter protected final AWSAccessKey _accessKey;
	@Getter protected final AWSAccessSecret _accessSecret;
	@Getter protected final Charset _charset;
	@Getter protected final AWSClientHttpSettings _httpSettings;
	@Getter private   final Properties _systemProperties;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AWSClientConfig(final Region region,
						   final AWSAccessKey accessKey,final AWSAccessSecret accessSecret,
						   final Charset charset) {
		this(region,
			 accessKey,accessSecret,
			 charset,
			 null,   // no http settings
			 null);	// no system properties
	}

	public AWSClientConfig(final Region region,
						   final AWSAccessKey accessKey,final AWSAccessSecret accessSecret,
						   final Charset charset,
						   final AWSClientHttpSettings httpSettings) {
		this(region,
			 accessKey,accessSecret,
	     	 charset,
		     httpSettings,
		     null);	// no system properties
	}

	public AWSClientConfig(final Region region,
						   final AWSAccessKey accessKey,final AWSAccessSecret accessSecret,
						   final Charset charset,
						   final AWSClientHttpSettings httpSettings,
						   final Properties systemProperties) {
		_region = region;
		_accessKey = accessKey;
		_accessSecret = accessSecret;
		_charset = charset;
		_httpSettings = httpSettings;
		_systemProperties = systemProperties;

		if (_systemProperties != null) {
			log.warn("WARNING!!!!!! Updating System Properties " +
					"(check: https://aws-amplify.github.io/aws-sdk-android/docs/reference/com/amazonaws/SDKGlobalConfiguration.html!");

			Set<String> keys = _systemProperties.stringPropertyNames();
			for (final String key : keys) {
				log.warn("\t-Setting property {} to value {}!!!",
						 key,
						 _systemProperties.getProperty(key));
	 			System.setProperty(key,_systemProperties.getProperty(key));
			};
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	FROM PROPERTIES
/////////////////////////////////////////////////////////////////////////////////////////
	public static AWSClientConfigBuilder builder() {
		return new AWSClientConfigBuilder() { /* nothing */ };
	}
/////////////////////////////////////////////////////////////////////////////////////////
// UTILITY METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean hasHttpSettings() {
		return _httpSettings != null;
	}
}
