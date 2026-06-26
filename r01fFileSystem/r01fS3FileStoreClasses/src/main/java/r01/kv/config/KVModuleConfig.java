package r01.kv.config;


import r01f.config.ContainsConfigData;
import r01f.services.ids.ServiceIDs.CoreAppCode;
import r01f.services.ids.ServiceIDs.CoreModule;

/**
 * KV module config
 */
public interface KVModuleConfig
		 extends ContainsConfigData {

	public static final CoreModule KVPERSISTENCE = CoreModule.forId("kvpersistence");
	
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return the app code
	 */
	public CoreAppCode getAppCode();
	/**
	 * @return the app module
	 */
	public CoreModule getAppModule();
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return the {@link S3ClientConfig}
	 */
	public ContainsConfigData getClientConfig();

	public <K extends KVModuleConfig  > K as (final Class<K> impl);
}

