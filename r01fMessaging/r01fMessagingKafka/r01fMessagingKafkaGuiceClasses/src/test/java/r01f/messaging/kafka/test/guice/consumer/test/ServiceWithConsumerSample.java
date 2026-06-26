package r01f.messaging.kafka.test.guice.consumer.test;

import java.time.Duration;
import java.util.List;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import com.google.common.collect.Lists;

import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import r01f.messaging.kafka.consumer.KafkaConsumerService;
import r01f.messaging.kafka.test.model.MyOIDs.MyTestOID;
import r01f.messaging.kafka.test.model.MyTestModelObject;

/**
 * [ Sample ] Some sample service using a injected KafkaConsumerService
 */
@Slf4j
public class ServiceWithConsumerSample {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	public final KafkaConsumerService<MyTestOID,MyTestModelObject> _consumerService;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
	@Inject
	public ServiceWithConsumerSample( final KafkaConsumerService<MyTestOID,MyTestModelObject> consumerService) {
		_consumerService  = consumerService;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	SOMETHING TO DO BASED ON CONSUMER SERVICE
/////////////////////////////////////////////////////////////////////////////////////////	
	@SuppressWarnings({ })
	public void  doIt() {
		 try {
			 // Don't use this! just to test guice
			 String topic = "default-topic";
			 List<String> topics =  Lists.newArrayList();
			 topics.add(topic);

			 KafkaConsumer<MyTestOID,MyTestModelObject> kafkaConsumer =_consumerService.getKafkaConsumer();
			 kafkaConsumer.subscribe(topics);
	         while (true) {
	              ConsumerRecords<MyTestOID, MyTestModelObject> records =
	                      kafkaConsumer.poll(Duration.ofMillis(5000));
	              if (records.count() == 0) {
	                  log.warn(" --- NOTHING ----");
	              }
	              for (ConsumerRecord<MyTestOID, MyTestModelObject> record : records) {
	            	  log.warn(record.value().getName());
	            	  Thread.sleep(5000);
	              }
	          }
	      } catch (Throwable e) {
	    	  //
	      }
	}
}
