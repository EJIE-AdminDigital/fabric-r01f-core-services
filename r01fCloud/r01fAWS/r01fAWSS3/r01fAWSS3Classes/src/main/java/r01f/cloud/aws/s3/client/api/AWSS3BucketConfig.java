package r01f.cloud.aws.s3.client.api;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.config.ContainsConfigData;
import r01f.debug.Debuggable;
import r01f.util.types.Strings;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

@Accessors(prefix="_")
public class AWSS3BucketConfig
	 implements Debuggable, 
	 			ContainsConfigData {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
    @Getter @Setter private String _defaultBucket;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AWSS3BucketConfig(final String defaultBucket) {
		this._defaultBucket = defaultBucket;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static final AWSS3BucketConfig fromXMLProperties(final XMLPropertiesForAppComponent props,
															final String propsRootNode) {
		return AWSS3BucketConfigBuilder.fromXMLProperties(props, propsRootNode);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CharSequence debugInfo() {
		StringBuilder outDbgInfo = new StringBuilder();
	    outDbgInfo.append("\n S3 Config :{ \n ").append(Strings.customized(" Default Bucket {}",
	    																   _defaultBucket ));
		outDbgInfo.append(" } \n")	;
	    return outDbgInfo.toString();
	}
}
