package r01f.messaging;

import com.google.inject.Binder;
import com.google.inject.Module;

/**
 * This guice module is to be used when using the {@link MessagingServiceHandler} in a
 * standalone way (ie testing) something like:
 *
 * <pre class='brush:java'>
	     Injector GUICE_INJECTOR = Guice.createInjector(new XMLPropertiesGuiceModule(),
				                                        new DistributedCacheBootstrapModule(arg1,arg2));

		ServicesLifeCycleUtil.startServices(GUICE_INJECTOR); // Hazelcast doesnt need anything special to start, but YES to stop, so its important to bind a handler.
		MessagingServiceHandler cacheService = GUICE_INJECTOR.getInstance(MessagingServiceHandler.class);
		MockObject theMockObject = new MockObject();
		cacheService.getCacheForModelObject(MockObject.class)
						.put(theMockObject.getOid(), theMockObject);
		MockObject mockObjectFromCache =  cacheService.getCacheForModelObject(MockObject.class)
				                                        .get(oid);
		ServicesLifeCycleUtil.stopServices(GUICE_INJECTOR);
 * </pre>
 *
 * It's VERY important to bind the XMLPropertiesGuiceModule: *
 * <pre class='brush:java'>
 * 		binder.install(new XMLPropertiesGuiceModule());
 * </pre>
 */

public abstract class MessagingGuiceModuleBase
           implements Module {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	protected final MessagingServiceConfig _config;
//////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
//////////////////////////////////////////////////////////////////////////////////////////
	static String R01_PREFIX = "R01.MESSSAGING.";

/////////////////////////////////////////////////////////////////////////////////////////
// 	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public MessagingGuiceModuleBase(final MessagingServiceConfig cfg) {
		_config = cfg;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public abstract MessagingServiceHandler provideMessaginServiceHandler();

	@Override
	public void configure(final Binder binder) {
		// Service handler used to control (start/stop) the Persistence Service (see ServletContextListenerBase)
		// do NO forget!!  -- doesnt need anything special to start, but YES to stop, so its important to bind a handler.
		/*binder.bind(ServiceHandler.class)
        	  .annotatedWith(Names.named(Strings.customized("{}.{}",
        			  										_config.getAppCode(),_config.getAppComponent())))
        	  .to(MessagingServiceHandler.class)
        	  .in(Singleton.class);*/
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////


}
