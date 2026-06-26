package r01f.bootstrap.kafka;

import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.MessageListenerContainer;



/**
 * Base Class for every Kafka Consumer based on Spring.
 * --------------------------------------------------------------------------------
 *  [ Steps]
 *    [ 1 ] Enable Kafka at Comfiguration Componenent:   @EnableKafka
 *    [ 2 ] Inject ServiceBootstrapSpringHandler     :   ServiceBootstrapSpringHandler
 *    [ 3 ] Expose as beans :   ConsumerFactory & KafkaListenerContainerFactory
 *
 * <pre class='brush:java'>
 *
 *		@EnableKafka
		@ComponentScan(basePackages = { "demo01c.listener" }) //package of listener
		@Configuration
		public class DM01SpringConfigForKafkaConsumer
				extends SpringConfigForKafkaConsumerBase<DeliverRequestOID,DM01DeliverRequest> {
		///////////////////////////////////////////////////////////////////////////
		//	MEMBERS
		//////////////////////////////////////////////////////////////////////////
			@Inject
			public DM01SpringConfigForKafkaConsumer( final ServiceBootstrapSpringHandler servicesBootstrap) {
				super(servicesBootstrap);
			}
		///////////////////////////////////////////////////////////////////////////
		// CONSUMER FACTORY AND LISTENER CONTAINER
		//////////////////////////////////////////////////////////////////////////
			@Bean
			@Override
			public ConsumerFactory<DeliverRequestOID, DM01DeliverRequest> consumerFactory() {
				Key<KafkaConsumerService<DeliverRequestOID,DM01DeliverRequest>> key =
						   new Key<KafkaConsumerService<DeliverRequestOID,DM01DeliverRequest>>() { };
			     return new KafkaConsumerSpringFactoryBase<>(_servicesBootstrap.getInjector()
						                                                        .getInstance(key));

			}

			@Bean
			public ConcurrentKafkaListenerContainerFactory<DeliverRequestOID, DM01DeliverRequest>    kafkaListenerContainerFactory() {
		        ConcurrentKafkaListenerContainerFactory<DeliverRequestOID, DM01DeliverRequest> factory =
		          new ConcurrentKafkaListenerContainerFactory<>();
		        factory.getContainerProperties().setAckMode(AckMode.MANUAL_IMMEDIATE);
		        factory.setConsumerFactory(consumerFactory());
		        return factory;
		    }
 *
 * </pre>
 */
public abstract class SpringConfigForKafkaConsumerBase<K,V>
		implements  SpringConfigForKafkaConsumer<K,V> {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////	
	//protected final ServiceBootstrapSpringHandler _servicesBootstrap;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTORS
/////////////////////////////////////////////////////////////////////////////////////////	
	public SpringConfigForKafkaConsumerBase(
			                                   //final ServiceBootstrapSpringHandler servicesBootstrap
			                                   ) {
		//_servicesBootstrap = servicesBootstrap;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSUMMER FACTORY
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public abstract ConsumerFactory<K,V> consumerFactory();

	@Override
	public abstract <C  extends MessageListenerContainer> KafkaListenerContainerFactory<C> kafkaListenerContainerFactory();
}
