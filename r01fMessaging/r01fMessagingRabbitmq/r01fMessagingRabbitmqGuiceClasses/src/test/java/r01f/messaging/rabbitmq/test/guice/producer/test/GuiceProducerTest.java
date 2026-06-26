
package r01f.messaging.rabbitmq.test.guice.producer.test;


import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

import r01f.core.messaging.rabbitmq.producer.bootstrap.RabbitMQProducerGuiceModuleForModelObject;
import r01f.messaging.rabbitmq.test.guice.CommonClientBindingsGuiceModuleForTest;
import r01f.messaging.rabbitmq.test.model.MyOIDs.MyTestOID;
import r01f.messaging.rabbitmq.test.model.MyTestModelObject;
import r01f.messaging.rabbitmq.test.model.RabbitMQProducerConfigSample;

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
		RabbitMQProducerConfigSample config = new RabbitMQProducerConfigSample();
		Injector injector = getInjector(new CommonClientBindingsGuiceModuleForTest(ServiceWithProducerSample.class),
								   	    new RabbitMQProducerGuiceModuleForModelObject<>(MyTestOID.class,
								   			                                            MyTestModelObject.class,
								   			                                            config));
		ServiceWithProducerSample test = injector.getInstance(ServiceWithProducerSample.class);
		test.doIt();
	}
}
