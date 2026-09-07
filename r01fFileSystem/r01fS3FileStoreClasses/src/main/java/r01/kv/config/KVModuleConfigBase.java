package r01.kv.config;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.cloud.aws.s3.client.api.AWSS3ClientConfig;
import r01f.guids.CommonOIDs.IsAppCode;
import r01f.guids.CommonOIDs.IsAppComponent;


@Accessors(prefix="_")
public abstract class KVModuleConfigBase
    	   implements KVModuleConfig {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter protected final IsAppCode _appCode;
	@Getter protected final IsAppComponent _appModule;
	@Getter protected final KVModuleConfigForBucket _moduleConfigForBucket;
	@Getter protected final AWSS3ClientConfig _s3ClientConfig;
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public KVModuleConfigBase(final IsAppCode appCode,final IsAppComponent appModule,
							  final KVModuleConfigForBucket moduleConfigForBucket,
							  final AWSS3ClientConfig s3ClientConfig) {
		_appCode = appCode;
		_appModule = appModule;
		_s3ClientConfig = s3ClientConfig;
		_moduleConfigForBucket = moduleConfigForBucket;
	}
}
