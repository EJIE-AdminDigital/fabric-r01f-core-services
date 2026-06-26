package r01f.messaging.kafka.test.guice;

import com.google.inject.Binder;
import com.google.inject.Module;

import r01f.internal.R01FAppCodes;
import r01f.model.annotations.ModelObjectsMarshaller;
import r01f.objectstreamer.Marshaller;
import r01f.objectstreamer.MarshallerBuilder;

public class CommonClientBindingsGuiceModuleForTest
  implements Module {
	final Class<?> _testService ;
/////////////////////////////////////////////////////////////////////////////////////////
//CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public CommonClientBindingsGuiceModuleForTest(final Class<?> testService) {
		super();
		_testService = testService;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void configure(final Binder binder) {
		_bindModelObjectsMarshaller(binder);
		binder.bind(_testService);
	}
/////////////////////////////////////////////////////////////////////////////////////////
// COMMON BINDINGS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * bindings for a marshaller
	 */
	private static void _bindModelObjectsMarshaller(final Binder binder) {
		// Create the model objects marshaller
		Marshaller marshaller = MarshallerBuilder.findTypesToMarshallAt(R01FAppCodes.APP_CODE)
												 .build();

		// Bind this instance to the model object's marshaller
		binder.bind(Marshaller.class)
			  .annotatedWith(ModelObjectsMarshaller.class)
			  .toInstance(marshaller);
	}
}
