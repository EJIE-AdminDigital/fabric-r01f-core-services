package r01.kv.config;

import java.util.Properties;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import r01.kv.transform.namestrategy.KVNameStrategies;
import r01f.cloud.aws.s3.client.api.AWSS3ClientConfig;
import r01f.patterns.IsBuilder;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class KVModuleConfigBuilder
		   implements IsBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//  BUILDERS
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unchecked")
	public static final <CFG extends KVModuleConfig> CFG kvConfigFor(final XMLPropertiesForAppComponent kvProps) {

		AWSS3ClientConfig clientForS3Api = AWSS3ClientConfig.fromXMLProperties(kvProps,"/moduleConfigForS3/s3ClientConfig");

		KVModuleConfigForBucket kvConfigForBucket = new KVModuleConfigForBucket(kvProps.propertyAt("/moduleConfigForS3/moduleConfigForBucket/defaultBucket")
						                                                                       .asObjectFromString(source -> KVBucketName.forId(source),
						                                                                                           KVBucketName.forId("defaultBucket")),
        		                                                                kvProps.propertyAt("/moduleConfigForS3/moduleConfigForBucket/nameStrategy")
        		                                                                	   .asEnumElement(KVNameStrategies.class, KVNameStrategies.Default),
        		                                                                kvProps.propertyAt("/moduleConfigForS3/moduleConfigForBucket/properties")
        		                                                                	   .asProperties(new Properties()));
		return (CFG)new KVModuleConfigBaseForS3(kvProps.getAppCode(),
			                                    kvProps.getAppComponent(),
			                                    kvConfigForBucket,
			                                    clientForS3Api);
	}
	/*@SuppressWarnings("unchecked")
	public static <KVCFG extends KVModuleConfig> KVCFG kvModuleConfigFrom(final ServicesCoreModuleBootstrapConfig coreCfg) {
		return (KVCFG)coreCfg.getSubModuleConfigFor(KVModuleConfig.KVPERSISTENCE);
	}
	public static KVModuleConfigBaseForS3  s3ModuleConfigFrom(final ServicesCoreModuleBootstrapConfig coreCfg) {
		return (KVModuleConfigBaseForS3)kvModuleConfigFrom(coreCfg);
	}*/
}
