package r01f.messaging.kafka.test;


import java.time.Duration;
import java.util.List;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import com.google.common.collect.Lists;

import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.cloud.aws.s3.events.consumer.AWSS3EventNotification;
import r01f.cloud.aws.s3.events.kafka.consumer.AWSS3KafkaConsumerService;
import r01f.cloud.aws.s3.model.AWSS3ObjectKey;
import r01f.internal.R01FAppCodes;
import r01f.objectstreamer.Marshaller;
import r01f.objectstreamer.MarshallerBuilder;

@Accessors(prefix="_")
@Slf4j
public class Z99S3KafkaEventNotificationConsumer {
	
	 static   Marshaller _marshaller = MarshallerBuilder.findTypesToMarshallAt(R01FAppCodes.APP_CODE).build();
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	@SuppressWarnings("resource")
	public static AWSS3KafkaConsumerService getService( final Z99S3KafkaEventNotificationConsumerConfig config,
			                                            final Marshaller marshaller){
		return new Z99S3EventNotificationConsumer(config,marshaller);
		
	}
	

/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	

	
	public static void main(final String[]  argv) {
		// [ 1 ] - Consume Service
		AWSS3KafkaConsumerService  service = getService(new Z99S3KafkaEventNotificationConsumerConfig(),
				                                                            _marshaller);
		KafkaConsumer<AWSS3ObjectKey,AWSS3EventNotification> consumer = service.getKafkaConsumer();
	
		try {
			String topic = "dena-proxy-metadata-applicant-kafka-topic";
			List<String> topics =  Lists.newArrayList();
			topics.add(topic);

			consumer.subscribe(topics);
			 while (true) {
				  ConsumerRecords<AWSS3ObjectKey, AWSS3EventNotification> records =
						  consumer.poll(Duration.ofMillis(5000));
				  if (records.count() == 0) {
					 log.warn("nothing");
				  }
				  for (ConsumerRecord<AWSS3ObjectKey, AWSS3EventNotification> record : records) {
					  log.warn("\n\n");
					  log.warn("#####################################################################################");
					  log.warn(record.key().asString());
					  log.warn("#####################################################################################");
					
					  AWSS3EventNotification  notification =  record.value();
					  String notificationAsJson =  _marshaller.forWriting().toJson(notification);
					  
					  log.warn(notificationAsJson);
					  Thread.sleep(5000);
				  }
			  }
		  } catch (final Throwable e) {
			  e.printStackTrace();
		  }
	}
	
}
