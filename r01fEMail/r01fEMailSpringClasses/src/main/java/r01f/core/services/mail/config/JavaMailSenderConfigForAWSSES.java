package r01f.core.services.mail.config;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.cloud.aws.ses.AWSSESClientConfig;
import r01f.core.services.mail.JavaMailSenderProperties.AWSProperties;
import r01f.xmlproperties.XMLPropertiesForAppComponent;
import software.amazon.awssdk.regions.Region;

@Accessors(prefix="_")
public class JavaMailSenderConfigForAWSSES
	 extends JavaMailSenderConfigBase {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final AWSSESClientConfig _awsSESClientConfig;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public JavaMailSenderConfigForAWSSES(final AWSSESClientConfig config,
									     final boolean disabled) {
		super(JavaMailSenderImpl.AWS_SES,
			  disabled);
		_awsSESClientConfig = config;
	}
	public static JavaMailSenderConfigForAWSSES createFrom(final XMLPropertiesForAppComponent xmlProps,
												      	   final String propsRootNode) {
		AWSSESClientConfig config = JavaMailSenderConfigForAWSSES.awsSESClientConfigFrom(xmlProps,
																	     				 propsRootNode);
		return new JavaMailSenderConfigForAWSSES(config,
												 false);	// not disabled
	}
	public static JavaMailSenderConfigForAWSSES createFrom(final AWSProperties properties) {
		AWSSESClientConfig config = JavaMailSenderConfigForAWSSES.awsSESClientConfigFrom(properties);
		return new JavaMailSenderConfigForAWSSES(config,
			                                   false);	// not disabled
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	static AWSSESClientConfig awsSESClientConfigFrom(final XMLPropertiesForAppComponent props,
									   				 final String propsRootNode) {
		return AWSSESClientConfig.fromXMLProperties(props,
													propsRootNode);
	}
	static AWSSESClientConfig awsSESClientConfigFrom(final AWSProperties properties) {
		return new 	AWSSESClientConfig(Region.EU_CENTRAL_1,
				                       properties.accessKey(),
				                       properties.accessSecret());			
	}

}
