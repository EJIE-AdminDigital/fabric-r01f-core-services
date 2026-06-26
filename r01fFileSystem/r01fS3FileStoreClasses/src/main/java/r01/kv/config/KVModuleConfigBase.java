package r01.kv.config;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.cloud.aws.s3.client.api.AWSS3ClientConfig;
import r01f.services.ids.ServiceIDs.CoreAppCode;
import r01f.services.ids.ServiceIDs.CoreModule;


@Accessors(prefix="_")
public abstract class KVModuleConfigBase
    	   implements KVModuleConfig {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter protected final CoreAppCode _appCode;
	@Getter protected final CoreModule _appModule;
	@Getter protected final KVModuleConfigForBucket _moduleConfigForBucket;
	@Getter protected final AWSS3ClientConfig _s3ClientConfig;
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public KVModuleConfigBase(final CoreAppCode appCode,final CoreModule appModule,
							  final KVModuleConfigForBucket moduleConfigForBucket,
							  final AWSS3ClientConfig s3ClientConfig) {
		_appCode = appCode;
		_appModule = appModule;
		_s3ClientConfig = s3ClientConfig;
		_moduleConfigForBucket = moduleConfigForBucket;
	}
}
