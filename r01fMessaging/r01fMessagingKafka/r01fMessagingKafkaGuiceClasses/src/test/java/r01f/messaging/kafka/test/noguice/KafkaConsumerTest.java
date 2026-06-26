package r01f.messaging.kafka.test.noguice;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;
import r01f.messaging.kafka.KafkaIDs.KafkaTopic;
import r01f.messaging.kafka.consumer.KafkaConsumerService;
import r01f.messaging.kafka.consumer.KafkaConsumerServiceForModelObjectImpl;
import r01f.messaging.kafka.serialization.KafkaDeserializerForModelObjectBase;
import r01f.messaging.kafka.serialization.KafkaDeserializerForOIDBase;
import r01f.messaging.kafka.test.model.KafkaConsumerConfigSample;
import r01f.messaging.kafka.test.model.MyOIDs.MyTestOID;
import r01f.messaging.kafka.test.model.MyTestModelObject;
import r01f.objectstreamer.Marshaller;

@Slf4j
public class KafkaConsumerTest {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	@SuppressWarnings("resource")
	public static KafkaConsumerService<MyTestOID,MyTestModelObject> getService(final Marshaller marshaller){
		return new KafkaConsumerServiceForModelObjectImpl<>(// A sample config.
															new KafkaConsumerConfigSample(),
															// deserializer
															new KafkaDeserializerForOIDBase<>(MyTestOID.class),
															new KafkaDeserializerForModelObjectBase<>(MyTestModelObject.class,
																									  marshaller));
	}
	public static KafkaConsumer<String, String> geDefaultStringSerialzierDeserializer(){
		Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "consumer-group");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        
      
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

     
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        return consumer;
	}
	
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	

	
	public static void main(final String[] argv) {
		// [ 0 ] - Marshaller
		Marshaller marshaller = KafkaProducerTest.getMarshaller();

		// [ 1 ] - Consume Service (Configurado para extraer el valor como String)
		// NOTA: Asegúrate de que tu método auxiliar 'getService' o tu infraestructura 
		// acepte la parametrización <MyTestOID, String> y use StringDeserializer.
		//KafkaConsumerService<MyTestOID, String> impl = getService(marshaller);
		//KafkaConsumer<MyTestOID, String> consumer = impl.getKafkaConsumer();
		
		try {
			String topic = "dena-proxy-metadata-applicant-kafka-topic";
			List<String> topics = Lists.newArrayList();
			topics.add(topic);
			

	      
	        KafkaConsumer<String, String> consumer = geDefaultStringSerialzierDeserializer();
			consumer.subscribe(topics);
			while (true) {
				ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(5000));
				
				if (records.count() == 0) {
					log.info("nothing...");
				}
				
				for (ConsumerRecord<String, String> record : records) {
					log.warn("\n\n");
					log.warn("#####################################################################################");
					log.warn("Processing message -> Partition: [" + record.partition() + "] | Offset: [" + record.offset() + "]");
					
				
					String rawJson = record.value();

					try {
					
						log.warn("Payload recibido:\n\n " + rawJson);
						
						
						Thread.sleep(20000); 
						
					} catch (final InterruptedException e) {
						Thread.currentThread().interrupt();
					} catch (final Throwable e) {
						log.error("Error local procesando el contenido del mensaje", e);
					}
					
					log.warn("#####################################################################################");
				}
			}
		} catch (final Throwable e) {
			e.printStackTrace();
		}
	}
	
	
	/*public static void main(final String[]  argv) {
		// [ 0 ] - Marshaller
		Marshaller marshaller = KafkaProducerTest.getMarshaller();

		// [ 1 ] - Consume Service
		KafkaConsumerService<MyTestOID,MyTestModelObject> impl = getService(marshaller);
		KafkaConsumer<MyTestOID,MyTestModelObject> consumer = impl.getKafkaConsumer();
		try {
			String topic = "mi-topico-kafka"; //KafkaTopic.DEFAULT.asString();
			List<String> topics =  Lists.newArrayList();
			topics.add(topic);

			consumer.subscribe(topics);
			  while (true) {
				  ConsumerRecords<MyTestOID, MyTestModelObject> records =
						  consumer.poll(Duration.ofMillis(5000));
				  if (records.count() == 0) {
					 log.warn("nothing");
				  }
				  for (ConsumerRecord<MyTestOID, MyTestModelObject> record : records) {
					  log.warn("\n\n");
					  log.warn("#####################################################################################");
					  log.warn(record.value().getName());
					  log.warn("#####################################################################################");
					  Thread.sleep(5000);
				  }
			  }
		  } catch (final Throwable e) {
			  e.printStackTrace();
		  }
	}
	*/
}
