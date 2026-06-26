package r01f.spring.rabbitmq.listener;

/**
 *
 * Base Class for every Kafka Listener based on Spring.
 * --------------------------------------------------------------------------------
 *  [ Steps]
 *    [ 1 ] Ensure that SpringConfigForKafkaConsumer has been included at bootstrap.
 *    [ 2 ] Develop Listener extending RabbitMQListenerBase *
 *
 * <pre class='brush:java'>
 *		@Service
 *		@Component
 *		public class DM01KafkaListenerSample
 *				extends RabbitMQListenerBase {
 *		/////////////////////////////////////////////////////////////////////
 *		// 	CONSTRUCTOR
 *		/////////////////////////////////////////////////////////////////////
 *			@Inject
 *			public DM01KafkaListenerSample(final ServiceBootstrapSpringHandler servicesBootstrap) {
 *				super(servicesBootstrap); // servicesBootstrapHandler for using services
 *			}
 *		/////////////////////////////////////////////////////////////////////
 *		// 	LISTEN
 *		/////////////////////////////////////////////////////////////////////
 *			@KafkaListener(topics = "default-topic"  , groupId = "DEFAULT")
 *			public void listen( final DM01DeliverRequest deliverRequest  , Acknowledgment ack ) {
 *				System.out.println("======================================================================");
 *				System.out.println("======================================================================");
 *				System.out.println("======================================================================");
 *			    System.out.println(" > [ X ] Deliver Request OID :  " +  deliverRequest.getOid());
 *				System.out.println("======================================================================");
 *				System.out.println("======================================================================");
 *				ack.acknowledge();
 *			}
 *		}
 * </pre>
 *
 * See :
 * 		https://www.stackchief.com/blog/Spring%20Boot%20Kafka%20Consumer
 *		https://luiscualquiera.medium.com/c%C3%B3mo-gestionar-los-commit-en-kafka-4a9ff18763c3
 */
public class RabbitMQListenerBase {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////	
	//protected final ServiceBootstrapSpringHandler _servicesBootstrap;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
	public RabbitMQListenerBase(
			                      //final ServiceBootstrapSpringHandler servicesBootstrap
			                      ){
		//_servicesBootstrap = servicesBootstrap;
	}
}
