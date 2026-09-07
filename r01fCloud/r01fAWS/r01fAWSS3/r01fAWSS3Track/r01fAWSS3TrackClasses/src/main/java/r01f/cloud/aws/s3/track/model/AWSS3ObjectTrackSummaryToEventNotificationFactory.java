package r01f.cloud.aws.s3.track.model;



import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import r01f.cloud.aws.s3.events.consumer.AWSS3EventNotification;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AWSS3ObjectTrackSummaryToEventNotificationFactory {

    /**
     * Converts a collection of tracking summary objects into a single unified S3 event notification.
     * Useful for batching multiple detected reconciliation items into a single Kafka message payload.
     *
     * @param summaries Collection of tracking states mapped from the S3/MinIO engine.
     * @return An immutable AWSS3EventNotification instance containing all associated records.
     */
    public static AWSS3EventNotification from(final Collection<AWSS3ObjectTrackSummary> summaries) {
        if (summaries == null 
        		|| summaries.isEmpty()) {
            return new AWSS3EventNotification(List.of());
        }
        // Map each object summary to its corresponding standalone S3 Event Record
        List<AWSS3EventNotification.S3EventRecord> records = summaries.stream()
                .map(AWSS3ObjectTrackSummaryToEventNotificationFactory::buildRecordFrom)
                .collect(Collectors.toList());

        // Return the root envelope wrapper holding the complete list
        return new AWSS3EventNotification(records);
    }

    /**
     * Constructs the hierarchical structure of an S3EventRecord from a single track summary instance.
     */
    private static AWSS3EventNotification.S3EventRecord buildRecordFrom(final AWSS3ObjectTrackSummary summary) {
        String bucketName = summary.getBucket() != null ? summary.getBucket().asString()
        		                                        : "unknown-bucket";
        String objectKey = summary.getKey() != null ? summary.getKey().asString()
        											: "";
        
        String eventTimeStr = summary.getLastModified() != null 
                ? summary.getLastModified().toString() 
                : Instant.now().toString();

        // If the object is a directory and the key lacks a trailing slash, append it per standard S3 specification
        if (summary.isFolder() && !objectKey.isEmpty() && !objectKey.endsWith("/")) {
            objectKey = objectKey + "/";
        }

        // 1. Instantiating low-level leaf metadata entities
        AWSS3EventNotification.ObjectInfo objectInfo = new AWSS3EventNotification.ObjectInfo(
            objectKey,
            0L,     // Default size payload (structural tracking summary does not always expose bytes)
            null,   // eTag
            null,   // versionId
            Long.toHexString(System.currentTimeMillis()) // Synthetic sequencer value to support strict ordering downstream
        );

        AWSS3EventNotification.BucketInfo bucketInfo = 
        		new AWSS3EventNotification.BucketInfo( bucketName,
        											   new AWSS3EventNotification.OwnerIdentity("fabric-polling-reconciliation"),
        											   "arn:aws:s3:::" + bucketName
        );

        // 2. Composition of the middle-tier S3 block container
        AWSS3EventNotification.S3Entity s3Entity =
        	new AWSS3EventNotification.S3Entity("1.0",
        									    "fabric-polling-configuration",
									            bucketInfo,
									            objectInfo
        );

        // 3. Assembling the overarching structural Record envelope !
        return new AWSS3EventNotification.S3EventRecord("2.1",
											            "fabric:s3", 
											            "us-east-1",
											            eventTimeStr,
											            "ObjectCreated:Put", // Simulating standard mutation operational verb !
											            new AWSS3EventNotification.UserIdentity("fabric:polling-service"),
											            new AWSS3EventNotification.RequestParameters("127.0.0.1"),
											            new AWSS3EventNotification.ResponseElements("SYNTHETIC-REQ-ID", "SYNTHETIC-ID-2"),
											            s3Entity
        );
    }
}