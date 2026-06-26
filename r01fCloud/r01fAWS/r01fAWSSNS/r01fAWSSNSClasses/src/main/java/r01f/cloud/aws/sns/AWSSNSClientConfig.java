package r01f.cloud.aws.sns;

import java.nio.charset.Charset;

import lombok.experimental.Accessors;
import r01f.cloud.aws.AWSAccessKey;
import r01f.cloud.aws.AWSAccessSecret;
import r01f.cloud.aws.AWSClientConfig;
import r01f.cloud.aws.AWSClientConfigBuilder;
import r01f.cloud.aws.AWSService;
import r01f.xmlproperties.XMLPropertiesForAppComponent;
import software.amazon.awssdk.regions.Region;

@Accessors(prefix="_")
public class AWSSNSClientConfig
	 extends AWSClientConfig {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AWSSNSClientConfig(final Region region,
							  final AWSAccessKey accessKey,final AWSAccessSecret accessSecret) {
		this(region,
			 accessKey,accessSecret,
			 Charset.defaultCharset());
	}
	public AWSSNSClientConfig(final Region region,
							  final AWSAccessKey accessKey,final AWSAccessSecret accessSecret,
							  final Charset charset) {
		super(region,
			  accessKey,accessSecret,
			  charset);
	}
	public AWSSNSClientConfig(final AWSClientConfig cfg) {
		super(cfg.getRegion(),
			  cfg.getAccessKey(),cfg.getAccessSecret(),
			  cfg.getCharset());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static final AWSSNSClientConfig fromXMLProperties(final XMLPropertiesForAppComponent props,
															 final String propsRootNode) {
		AWSClientConfig baseCfg = AWSClientConfigBuilder.fromXMLProperties(props,
																    	   propsRootNode,AWSService.SNS);
		return new AWSSNSClientConfig(baseCfg);
	}
}
