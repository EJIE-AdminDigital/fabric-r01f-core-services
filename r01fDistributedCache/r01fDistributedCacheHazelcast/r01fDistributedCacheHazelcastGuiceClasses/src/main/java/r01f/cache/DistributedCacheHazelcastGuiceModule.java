package r01f.cache;

import jakarta.inject.Singleton;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;

import r01f.cache.hazelcast.DistributedCacheHazelcastConfig;
import r01f.cache.hazelcast.DistributedCacheServiceHazelcastImpl;

/**
 * This guice module is to be used when using the {@link DistributedCacheService} in a
 * standalone way (ie testing) something like:
 *
 * <pre class='brush:java'>
 * 		 DistributedCacheConfig cfg = DistributedCacheHazelcastConfig.createFrom(props);
 *	     Injector GUICE_INJECTOR = Guice.createInjector(new DistributedCacheHazelcastGuiceModule(cfg);
 *
 *		ServicesLifeCycleUtil.startServices(GUICE_INJECTOR); // Hazelcast doesn't need anything special to start,
 *															 // but it MUST be stopped: its important to bind a handler.
 *		DistributedCacheService cacheService = GUICE_INJECTOR.getInstance(DistributedCacheService.class);
 *		MockObject theMockObject = new MockObject();
 *		cacheService.getCacheForModelObject(MockObject.class)
 *						.put(theMockObject.getOid(), theMockObject);
 *		MockObject mockObjectFromCache =  cacheService.getCacheForModelObject(MockObject.class)
 *				                                      .get(oid);
 *		ServicesLifeCycleUtil.stopServices(GUICE_INJECTOR);
 * </pre>
 *
 * It's VERY important to bind the XMLPropertiesGuiceModule: *
 * <pre class='brush:java'>
 * 		binder.install(new XMLPropertiesGuiceModule());
 * </pre>
 */
public class DistributedCacheHazelcastGuiceModule
			implements Module {

	final DistributedCacheConfig _cfg;
/////////////////////////////////////////////////////////////////////////////////////////
// 	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public DistributedCacheHazelcastGuiceModule(final DistributedCacheConfig cfg) {
		_cfg = cfg;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Provides @Singleton // beware the service is a singleton
	public DistributedCacheService provideDistributedCacheService() {
		return new DistributedCacheServiceHazelcastImpl(_cfg.as(DistributedCacheHazelcastConfig.class));
	}
	@Override
	public void configure(final Binder binder) {
		// TODO Auto-generated method stub

	}
}
