package r01f.core.fileexplorer.config;

import lombok.experimental.Accessors;
import r01f.cloud.aws.s3.client.api.AWSS3BucketConfig;
import r01f.cloud.aws.s3.client.api.AWSS3BucketConfigBuilder;
import r01f.cloud.aws.s3.client.api.AWSS3ClientConfig;
import r01f.cloud.aws.s3.client.api.AWSS3ClientConfigBuilder;
import r01f.filestore.api.FileStoreType;
import r01f.types.Path;
import r01f.util.types.Strings;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

@Accessors(prefix="_")
public class FileExplorerStoreConfigForS3
	 extends FileExplorerStoreConfigBase {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private XMLPropertiesForAppComponent _props;
	private String _xPathPrefix;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public FileExplorerStoreConfigForS3(final Path fsRootFullPath) {
		super(FileStoreType.S3,
			  fsRootFullPath);
	}
	public FileExplorerStoreConfigForS3(final XMLPropertiesForAppComponent xmlProps,final String xPathPrefix) {
		this(_fileExplorerRootFullPathFrom(xmlProps,xPathPrefix));
		_props = xmlProps;
		_xPathPrefix = xPathPrefix;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////	
	public AWSS3ClientConfig getS3Config() {
		
		String xpath = Strings.isNOTNullOrEmpty(_xPathPrefix) 
							? Path.from(_xPathPrefix,"fileStore[@impl='" + FileStoreType.S3 + "']")
								  .asAbsoluteString()
							: "cms/fileStore/[@impl='S3']";
		
		AWSS3ClientConfig s3Config = AWSS3ClientConfigBuilder.fromXMLProperties(_props, xpath + "/moduleConfigForS3/s3ClientConfig");
		
		return s3Config;
	}
	
	public AWSS3BucketConfig getS3BucketConfig() {
		
		String xpath = Strings.isNOTNullOrEmpty(_xPathPrefix) 
							? Path.from(_xPathPrefix,"fileStore[@impl='" + FileStoreType.S3 + "']")
								  .asAbsoluteString()
							: "cms/fileStore/[@impl='S3']";
		
		AWSS3BucketConfig s3BucketConfig = AWSS3BucketConfigBuilder.fromXMLProperties(_props, xpath + "/moduleConfigForS3/moduleConfigForBucket");
		
		return s3BucketConfig;
	}
	
	public static FileExplorerStoreConfigForS3 from(final XMLPropertiesForAppComponent xmlProps,final String xPathPrefix) {
		return new FileExplorerStoreConfigForS3(xmlProps,xPathPrefix);
	}	
}