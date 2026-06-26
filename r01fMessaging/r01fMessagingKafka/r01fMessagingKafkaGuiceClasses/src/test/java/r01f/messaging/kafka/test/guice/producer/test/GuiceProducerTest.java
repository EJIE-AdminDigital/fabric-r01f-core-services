
package r01f.messaging.kafka.test.guice.producer.test;


import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

import r01f.core.messaging.kafka.producer.bootstrap.KafkaProducerGuiceModuleForModelObject;
import r01f.messaging.kafka.producer.KafkaProducerServiceConfig;
import r01f.messaging.kafka.test.guice.CommonClientBindingsGuiceModuleForTest;
import r01f.messaging.kafka.test.model.KafkaProducerConfigSample;
import r01f.messaging.kafka.test.model.MyOIDs.MyTestOID;
import r01f.messaging.kafka.test.model.MyTestModelObject;

public class GuiceProducerTest {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	static Injector getInjector(final Module... modules) {
		Injector injector = Guice.createInjector(modules);
		return injector;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	public static void main(final String[] argv) {
		KafkaProducerServiceConfig config = new KafkaProducerConfigSample();
		Injector injector = getInjector(new CommonClientBindingsGuiceModuleForTest(ServiceWithProducerSample.class),
								   	    new KafkaProducerGuiceModuleForModelObject<>(MyTestOID.class,
								   			                                         MyTestModelObject.class,
								   			                                         config));
		ServiceWithProducerSample test = injector.getInstance(ServiceWithProducerSample.class);
		test.doIt();
	}
}
