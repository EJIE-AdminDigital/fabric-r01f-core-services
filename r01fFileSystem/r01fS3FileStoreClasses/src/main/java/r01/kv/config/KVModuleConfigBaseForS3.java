package r01.kv.config;

import lombok.experimental.Accessors;
import r01f.cloud.aws.s3.client.api.AWSS3ClientConfig;
import r01f.config.ContainsConfigData;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.services.ids.ServiceIDs.CoreAppCode;
import r01f.services.ids.ServiceIDs.CoreModule;


@MarshallType(as="moduleConfigForS3")
@Accessors(prefix="_")
public class KVModuleConfigBaseForS3
	 extends KVModuleConfigBase {
////////////////////////////////////////////////////////////////////
// 	CONSTRUCTOR
////////////////////////////////////////////////////////////////////
	public KVModuleConfigBaseForS3(final CoreAppCode appCode,
			                       	   final CoreModule appModule,
			                       	   final KVModuleConfigForBucket moduleConfigForBucket,
			                       	   final AWSS3ClientConfig clientConfig) {
		super(appCode,appModule,
			  moduleConfigForBucket,
			  clientConfig);
	}
////////////////////////////////////////////////////////////////////
// 	METHODS
////////////////////////////////////////////////////////////////////
	@Override @SuppressWarnings("unchecked")
	public <K extends KVModuleConfig> K as(final Class<K> impl) {
		return (K) this;
	}
	@Override
	public ContainsConfigData getClientConfig() {
		return this;
	}
}
