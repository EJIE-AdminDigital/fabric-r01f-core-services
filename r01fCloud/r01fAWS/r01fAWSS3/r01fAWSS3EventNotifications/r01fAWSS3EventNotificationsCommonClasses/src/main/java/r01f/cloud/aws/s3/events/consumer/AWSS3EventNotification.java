package r01f.cloud.aws.s3.events.consumer;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import r01f.cloud.aws.s3.model.AWSS3BucketID;
import r01f.cloud.aws.s3.model.AWSS3ObjectKey;
import r01f.cloud.aws.s3.model.AWSS3Operation;

import java.util.List;
import java.util.Optional;

/**
 * Production-ready S3 Event Notification model.[ Todo : Use fabric  annotations ]
 * Fully compatible with AWS S3, MinIO, and Scality event payloads.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AWSS3EventNotification(  @JsonProperty("Records") List<S3EventRecord> records)
		implements AWSS3Event {

    /**
     * Helper method to safely retrieve the first record without polluting 
     * the business logic layer with loops or list management.
     *
     * @return An Optional containing the first S3EventRecord if present.
     */
    public Optional<S3EventRecord> firstRecord() {
        return (records != null && !records.isEmpty()) 
                ? Optional.of(records.get(0)) 
                : Optional.empty();
    }
//////////////////////////////////////////////////////////////////
/// IMPLEMENTATION OF S3 EVENT [AWSS3Event]
/////////////////////////////////////////////////////////////////
    @Override
    public AWSS3BucketID getBucketName() {
        return firstRecord()
                .map(S3EventRecord::s3)
                .map(S3Entity::bucket)
                .map(BucketInfo::name)
                .map(AWSS3BucketID::new)
                .orElse(null);
    }

    @Override
    public AWSS3ObjectKey getObjectKey() {
        return firstRecord()
                .map(S3EventRecord::s3)
                .map(S3Entity::object)
                .map(ObjectInfo::key)
                .map(AWSS3ObjectKey::new)
                .orElse(null);
    }

    @Override
    public Long getObjectSize() {
        return firstRecord()
                .map(S3EventRecord::s3)
                .map(S3Entity::object)
                .map(ObjectInfo::size)
                .orElse(null);
    }

    @Override
    public AWSS3Operation getOperation() {
        String eventName = firstRecord()
                .map(S3EventRecord::eventName)
                .orElse(null);
        return AWSS3Operation.fromEventString(eventName);
    }
///////////////////////////////////////////////////////
/// RECORDS
//////////////////////////////////////////////////////
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static record S3EventRecord( @JsonProperty("eventVersion") String eventVersion,
								        @JsonProperty("eventSource") String eventSource,     // "aws:s3" or "minio:s3"
								        @JsonProperty("awsRegion") String awsRegion,
								        @JsonProperty("eventTime") String eventTime,         // ISO-8601 Timestamp
								        @JsonProperty("eventName") String eventName,         // e.g., "ObjectCreated:Put"
								        @JsonProperty("userIdentity") UserIdentity userIdentity,
								        @JsonProperty("requestParameters") RequestParameters requestParameters,
								        @JsonProperty("responseElements") ResponseElements responseElements,
								        @JsonProperty("s3") S3Entity s3
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static record UserIdentity( @JsonProperty("principalId") String principalId
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static record RequestParameters( @JsonProperty("sourceIPAddress") String sourceIPAddress
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static record ResponseElements(  @JsonProperty("x-amz-request-id") String xAmzRequestId,
    										@JsonProperty("x-amz-id-2") String xAmzId2
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static record S3Entity( @JsonProperty("s3SchemaVersion") String s3SchemaVersion,
								   @JsonProperty("configurationId") String configurationId,
								   @JsonProperty("bucket") BucketInfo bucket,
								   @JsonProperty("object") ObjectInfo object
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static record BucketInfo( @JsonProperty("name") String name,
							         @JsonProperty("ownerIdentity") OwnerIdentity ownerIdentity,
							         @JsonProperty("arn") String arn
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static record OwnerIdentity( @JsonProperty("principalId") String principalId
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static record ObjectInfo(    @JsonProperty("key") String key,               // Object Storage Key (URL Encoded)
								        @JsonProperty("size") Long size,               // File size in bytes (can be null on Deletes)
								        @JsonProperty("eTag") String eTag,             // MD5 checksum
								        @JsonProperty("versionId") String versionId,   // Object version ID if enabled
								        @JsonProperty("sequencer") String sequencer    // Hexadecimal string to determine event ordering
    ) {
        public ObjectInfo {
            // Key Handling!
            if (key != null) {
                key = java.net.URLDecoder.decode(key, java.nio.charset.StandardCharsets.UTF_8);
            }
        }
    }
}
