
package r01f.messaging.rabbitmq.test.guice.consumer.test;


import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

import r01f.core.messaging.rabbitmq.consumer.bootstrap.RabbitMQConsumerGuiceModuleForModelObject;
import r01f.messaging.rabbitmq.test.guice.CommonClientBindingsGuiceModuleForTest;
import r01f.messaging.rabbitmq.test.model.MyOIDs.MyTestOID;
import r01f.messaging.rabbitmq.test.model.MyTestModelObject;
import r01f.messaging.rabbitmq.test.model.RabbitMQConsumerConfigSample;

public class GuiceConsumerTest {
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
		RabbitMQConsumerConfigSample config = new RabbitMQConsumerConfigSample();
		// [ 1 ]  Create a injector with  RabbitMQConsumerGuiceModule
		Injector injector =getInjector(// .. a common bindings module usual in every proyect ( for this sample is important @Modelmarshaller.
									   new CommonClientBindingsGuiceModuleForTest(ServiceWithConsumerSample.class),
			                           // ... a Kafka Consumer Module for MyTestModelObject
								   	   new RabbitMQConsumerGuiceModuleForModelObject<>(MyTestOID.class,
								   			                                           MyTestModelObject.class,
								   			                                           config));
		// [ 2 ]  Get from injector a sample service using RabbitMQConsumer
		ServiceWithConsumerSample test = injector.getInstance(ServiceWithConsumerSample.class);
		test.doIt();
	}
}
