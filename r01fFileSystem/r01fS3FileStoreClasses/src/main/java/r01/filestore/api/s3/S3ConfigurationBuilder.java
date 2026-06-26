package r01.filestore.api.s3;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import r01f.cloud.aws.s3.client.api.AWSS3BucketConfig;
import r01f.cloud.aws.s3.client.api.AWSS3BucketConfigBuilder;
import r01f.cloud.aws.s3.client.api.AWSS3ClientConfig;
import r01f.cloud.aws.s3.client.api.AWSS3ClientConfigBuilder;
import r01f.util.types.Strings;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

/**
 * Utils for creating HDFS Configuration
 */
@Slf4j
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class S3ConfigurationBuilder {
	public static S3ConfigurationBuilderFromProperties fromXMLPropertiesBuilderUsingDefaultXPathPrefix(final String xpathPrefix) {
		return new S3ConfigurationBuilderFromProperties(xpathPrefix);
	}
	
	@RequiredArgsConstructor
	public static class S3ConfigurationBuilderFromProperties {
		private final String _defaultPropsXPathPrefix;
		
		public AWSS3ClientConfig s3ConfigurationUsing(final XMLPropertiesForAppComponent props,final String xpathPrefix) {
			log.info("[HDFS Config loader]: loading S3 config from properties component={}/{} base xPath={}",
					 props.getAppCode(),
					 props.getAppComponent(),
					 xpathPrefix);
			return AWSS3ClientConfigBuilder.fromXMLProperties(props, Strings.isNOTNullOrEmpty(xpathPrefix)
																		     ?_defaultPropsXPathPrefix
																		     :xpathPrefix);
		}
		public AWSS3BucketConfig s3BucketConfigurationUsing(final XMLPropertiesForAppComponent props,final String xpathPrefix) {
			log.info("[HDFS Config loader]: loading S3 bucket config from properties component={}/{} base xPath={}",
					 props.getAppCode(),
					 props.getAppComponent(),
					 xpathPrefix);
			return AWSS3BucketConfigBuilder.fromXMLProperties(props, Strings.isNOTNullOrEmpty(xpathPrefix)
																		     ?_defaultPropsXPathPrefix
																		     :xpathPrefix);
		}
	}
	
}
