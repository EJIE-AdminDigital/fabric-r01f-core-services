package r01f.cloud.aws.s3.client.api;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

@NoArgsConstructor(access=AccessLevel.PACKAGE)
public abstract class AWSS3BucketConfigBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static final AWSS3BucketConfig fromXMLProperties(final XMLPropertiesForAppComponent props,
														    final String propsRootNode) {
		String defaultBucket = props.propertyAt(propsRootNode + "/defaultBucket")
					  	 	 	    .asString("cms");
		AWSS3BucketConfig bucketConfig =  new AWSS3BucketConfig(defaultBucket);
		return bucketConfig;
	}
}
