
package r01f.messaging.kafka.test.guice.consumer.test;


import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

import r01f.core.messaging.kafka.consumer.bootstrap.KafkaConsumerGuiceModuleForModelObject;
import r01f.messaging.kafka.consumer.KafkaConsumerServiceConfig;
import r01f.messaging.kafka.test.guice.CommonClientBindingsGuiceModuleForTest;
import r01f.messaging.kafka.test.model.KafkaConsumerConfigSample;
import r01f.messaging.kafka.test.model.MyOIDs.MyTestOID;
import r01f.messaging.kafka.test.model.MyTestModelObject;

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
		KafkaConsumerServiceConfig config = new KafkaConsumerConfigSample();
		// [ 1 ]  Create a injector with  KafkaConsumerGuiceModule
		Injector injector =
				getInjector ( // .. a common bindings module usual in every proyect ( for this sample is important @Modelmarshaller.
						    new CommonClientBindingsGuiceModuleForTest(ServiceWithConsumerSample.class),
                             // ... a Kafka Consumer Module for MyTestModelObject
					   	    new KafkaConsumerGuiceModuleForModelObject<>(MyTestOID.class,
					   			                                         MyTestModelObject.class,
					   			                                         config));
		// [ 2 ]  Get from injector a sample service using KafkaConsumer
		ServiceWithConsumerSample test = injector.getInstance(ServiceWithConsumerSample.class);
		test.doIt();
	}

}
