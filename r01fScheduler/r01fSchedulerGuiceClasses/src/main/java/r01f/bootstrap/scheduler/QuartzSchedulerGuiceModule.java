package r01f.bootstrap.scheduler;

import jakarta.inject.Singleton;

import org.quartz.Scheduler;

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.PrivateBinder;
import com.google.inject.name.Names;

import r01f.scheduler.QuartzSchedulerJobFactory;
import r01f.scheduler.QuartzSchedulerProvider;
import r01f.scheduler.QuartzSchedulerServiceHandler;
import r01f.scheduler.SchedulerConfig;
import r01f.service.ServiceHandler;

public class QuartzSchedulerGuiceModule
  implements Module {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final SchedulerConfig _schedulerCfg;
	private final boolean _insidePrivateBinder;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public QuartzSchedulerGuiceModule(final SchedulerConfig schCfg) {
		this(schCfg,
			 true);	// inside a private binder by default
	}
	public QuartzSchedulerGuiceModule(final SchedulerConfig schCfg,
									  final boolean insidePrivateBinder) {
		_schedulerCfg = schCfg;
		_insidePrivateBinder = insidePrivateBinder;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void configure(final Binder binder) {
		// scheduler config
		binder.bind(SchedulerConfig.class)
			  .toInstance(_schedulerCfg);

		// quartz job factory
		binder.bind(QuartzSchedulerJobFactory.class)
			  .in(Singleton.class);

		// quartz scheduler provider
		binder.bind(Scheduler.class)
			  .toProvider(QuartzSchedulerProvider.class)
			  .in(Singleton.class);

		// quartz scheduler service handler
		_bindServiceHandler(binder,
							QuartzSchedulerServiceHandler.class,
							_schedulerCfg.getSchedulerId().asString());
		// this is IMPORTANT (and cannot be moved to ServicesBootstrapUtil.bindServiceHandler)
		if (_insidePrivateBinder) {
			PrivateBinder privateBinder = (PrivateBinder)binder;
			privateBinder.expose(Key.get(ServiceHandler.class,
										 Names.named(_schedulerCfg.getSchedulerId().asString())));	// expose the binding
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Binds a service handler type and exposes it if it's a private binder
	 * @param binder
	 * @param serviceHandlerType
	 * @param name
	 */
	private static void _bindServiceHandler(final Binder binder,
										  	final Class<? extends ServiceHandler> serviceHandlerType,final String name) {
		binder.bind(ServiceHandler.class)
			  .annotatedWith(Names.named(name))
			  .to(serviceHandlerType)
			  .in(Singleton.class);
	}
}
