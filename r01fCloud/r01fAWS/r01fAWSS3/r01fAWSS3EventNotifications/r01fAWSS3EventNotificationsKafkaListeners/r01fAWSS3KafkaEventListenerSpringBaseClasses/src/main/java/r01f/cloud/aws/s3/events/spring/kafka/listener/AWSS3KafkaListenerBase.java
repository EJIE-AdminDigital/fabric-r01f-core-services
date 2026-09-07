package r01f.cloud.aws.s3.events.spring.kafka.listener;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;

import lombok.extern.slf4j.Slf4j;
import r01f.cloud.aws.s3.events.consumer.AWSS3EventNotification;
import r01f.cloud.aws.s3.events.listener.AWSS3EventNotificationListenerBase;
import r01f.cloud.aws.s3.events.listener.AWSS3ObjectRetriever;
import r01f.cloud.aws.s3.events.listener.AWSS3ObjectRetrieverAsync;
import r01f.cloud.aws.s3.model.AWSS3ObjectKey;
import r01f.objectstreamer.Marshaller;

/**
 *
 * Base Class for every Kafka Listener based on Spring.
 * --------------------------------------------------------------------------------
 *  [ Steps]
 *    [ 1 ] Ensure that SpringConfigForKafkaConsumer has been included at bootstrap.
 *    [ 2 ] Develop Listener extending KafkaListenerBase *
 *
 * <pre class='brush:java'>
 *		@Service
 *		@Component
 *		public class DM01KafkaListenerSample
 *				extends KafkaListenerBase {
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
 *
 * </pre>
 *
 * See :
 * 		https://www.stackchief.com/blog/Spring%20Boot%20Kafka%20Consumer
 *		https://luiscualquiera.medium.com/c%C3%B3mo-gestionar-los-commit-en-kafka-4a9ff18763c3
 */
@Slf4j
public abstract class AWSS3KafkaListenerBase 
		extends  AWSS3EventNotificationListenerBase { 
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	

	public AWSS3KafkaListenerBase(final Marshaller marshaller) {
		super(marshaller,null,null);
	}
	protected AWSS3KafkaListenerBase(final Marshaller marshaller,
									  final AWSS3ObjectRetriever objectRetriever,
									  final AWSS3ObjectRetrieverAsync objectRetrieverAsync) {
		super(marshaller, objectRetriever, objectRetrieverAsync);
	}
	protected AWSS3KafkaListenerBase(final Marshaller marshaller,												
									 final AWSS3ObjectRetrieverAsync objectRetrieverAsync) {
		super(marshaller, null, objectRetrieverAsync);
	}
	///////////////////////////////////////////////////////////////////////////
	///  
	///////////////////////////////////////////////////////////////////////////
	@Override
	public abstract void  handleEvent(final AWSS3EventNotification notification);
	
	///////////////////////////////////////////////////////////////////////////
	///  
	///////////////////////////////////////////////////////////////////////////
	public void onMessage(final ConsumerRecord<AWSS3ObjectKey,AWSS3EventNotification> record) {
	  try {         
	      AWSS3EventNotification notification = record.value();            
	      handleEvent(notification);
	  } catch (Throwable e) {
	      log.error("Error processing S3 Event Notification inside Spring Listener", e);
	    
	  }
	}
}
