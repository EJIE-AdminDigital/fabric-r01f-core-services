package r01f.cloud.aws.s3.client.api;

import java.nio.charset.StandardCharsets;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import r01f.cloud.aws.AWSAccessKey;
import r01f.cloud.aws.AWSAccessSecret;
import r01f.cloud.aws.AWSClientConfig;
import r01f.cloud.aws.AWSClientConfigBuilder;
import r01f.cloud.aws.AWSService;
import r01f.patterns.FactoryFrom;
import r01f.types.url.Url;
import r01f.xmlproperties.XMLPropertiesForAppComponent;
import software.amazon.awssdk.regions.Region;

@NoArgsConstructor(access=AccessLevel.PACKAGE)
public abstract class AWSS3ClientConfigBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static final AWSS3ClientConfig fromXMLProperties(final XMLPropertiesForAppComponent props,
														    final String propsRootNode) {
		// Common  aws props (such as access key, secret key, etc..)
		AWSClientConfig baseCfg = AWSClientConfigBuilder.fromXMLProperties(props,
																	     propsRootNode,AWSService.S3);
		AWSS3ClientConfig clientConfig =  new AWSS3ClientConfig(baseCfg);
	    // Custom S3 props  ( a endpoint is required ! )
		Url endPoint = props.propertyAt(propsRootNode + "/aws/" + AWSService.S3.nameLowerCase() + "/endPoint")
								.asObjectFromString(new FactoryFrom<String,Url>() {
													@Override
													public Url from(final String uriAsString) {
														return Url.from(uriAsString);
													}
											  },
						  			      null);
		 clientConfig.setEndPoint(endPoint);
		 return clientConfig;
	}
	
	public static final AWSS3ClientConfig build( final AWSAccessKey _accessKey,   final AWSAccessSecret accessSecret,
			                                     final Url endPoint) {
		AWSClientConfig baseCfg = new AWSClientConfig(Region.EU_WEST_1, _accessKey, accessSecret, StandardCharsets.UTF_8);
		AWSS3ClientConfig clientConfig =  new AWSS3ClientConfig(baseCfg);
		clientConfig.setEndPoint(endPoint);
		return clientConfig;
	}
	
}
