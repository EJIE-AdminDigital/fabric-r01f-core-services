package r01f.cloud.aws.s3.track.test;

import java.util.Collection;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import r01f.cloud.aws.s3.events.consumer.AWSS3EventNotification;
import r01f.cloud.aws.s3.track.client.api.AWSS3ClientAPIForTrack;
import r01f.cloud.aws.s3.track.client.api.AWSS3ClientAPIProviderForTrack;
import r01f.cloud.aws.s3.track.model.AWSS3ObjectTrackSummary;
import r01f.cloud.aws.s3.track.model.AWSS3ObjectTrackSummaryToEventNotificationFactory;
import r01f.cloud.aws.s3.track.spring.service.polling.AWSS3MetadataTrackPollingConfig;
import r01f.cloud.aws.s3.track.spring.service.polling.AWSS3MetadataTrackPollingServiceBase;

@Slf4j
@Service
public class Z99AWSS3MetadataTrackPollingService
	extends AWSS3MetadataTrackPollingServiceBase {
	
	final NewTopic _topic;
    final KafkaTemplate<String, AWSS3EventNotification> _s3NotificationKafkaTemplate;

	@Inject
	public Z99AWSS3MetadataTrackPollingService(final AWSS3MetadataTrackPollingConfig config,
			                                   final AWSS3ClientAPIProviderForTrack s3ClientApi,  
			                                   final NewTopic topic,
			                                   final  KafkaTemplate<String, AWSS3EventNotification>  s3NotificationKafkaTemplate) {
    	
		super(config, s3ClientApi);
		_topic = topic;
		_s3NotificationKafkaTemplate = s3NotificationKafkaTemplate;
		
		
	}
	
	 /**  
     * Processes discovered metadata objects, batches them into a single native 
     * AWSS3EventNotification structure, and publishes the payload to Kafka...or whatever you want.
     */
    @Override
	protected void processDiscoveredObjects(final Collection<AWSS3ObjectTrackSummary> objects) {
        if (objects == null 
        		|| objects.isEmpty()) {
            log.info("[AWSS3MetadataTrackPollingService] No orphan objects discovered for reconciliation.");
            return;
        }

        log.warn("[AWSS3MetadataTrackPollingService] Processing metadata tracking for {} keys. Synthesizing bulk event...", 
                 objects.size());
        
        try {
            // [1.] Convert the entire collection into a single, unified S3 event notification payload
            AWSS3EventNotification bulkNotification = AWSS3ObjectTrackSummaryToEventNotificationFactory.from(objects);

            //[2] Define a partition key (using the bucket name ensures all batch records land on the same partition)
            String partitionKey = _config.getBucket().asString();

            // 3. Publish the bulk payload asynchronously to the target Kafka topic
            _s3NotificationKafkaTemplate.send(_topic.name(),
							            		partitionKey, bulkNotification)
							                          .whenComplete((result, ex) -> {
							                              if (ex == null) {
							                                  log.warn("[KAFKA] Bulk notification containing {} records successfully sent to partition {}", 
							                                           objects.size(), result.getRecordMetadata().partition());
							                              } else {
							                                  log.warn("[KAFKA] Failed to dispatch bulk reconciliation event notification payload", ex);
							                              }
							                          });

        } catch (final Throwable e) {
            log.error("[AWSS3MetadataTrackPollingService] Critical error during bulk mapping or Kafka streaming operational lifecycle", e);
        }
    }
		
}
