package r01f.cloud.aws.s3.events.consumer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import r01f.cloud.aws.s3.model.AWSS3BucketID;
import r01f.cloud.aws.s3.model.AWSS3ObjectKey;
import r01f.cloud.aws.s3.model.AWSS3Operation;

import java.util.List;
import java.util.Optional;

/**
 * Production-ready AWS EventBridge S3 Event model.
 * Handles both full native EventBridge payloads and transformed/filtered variants.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AWSS3EventBridgeNotification(	@JsonProperty("version") String version,
										    @JsonProperty("id") String id,
										    @JsonProperty("detail-type") String detailType,
										    @JsonProperty("source") String source,
										    @JsonProperty("account") String account,
										    @JsonProperty("time") String time,
										    @JsonProperty("region") String region,
										    @JsonProperty("resources") List<String> resources,
										    @JsonProperty("detail") EventDetail detail
) implements AWSS3Event {
//////////////////////////////////////////////////////////////////
/// IMPLEMENTATION OF S3 EVENT [AWSS3Event]
/////////////////////////////////////////////////////////////////
    @Override
    public AWSS3BucketID getBucketName() {
        return Optional.ofNullable(detail)
		                .map(EventDetail::bucket)
		                .map(BucketInfo::name)
		                .map(AWSS3BucketID::new)
		               .orElse(null);
    }

    @Override
    public AWSS3ObjectKey getObjectKey() {
        return Optional.ofNullable(detail)
			                .map(EventDetail::object)
			                .map(ObjectInfo::key)
			                .map(AWSS3ObjectKey::new)
			               .orElse(null);
    }

    @Override
    public Long getObjectSize() {
        return Optional.ofNullable(detail)
			                .map(EventDetail::object)
			                .map(ObjectInfo::size)
                .orElse(null);
    }

    @Override
    public AWSS3Operation getOperation() {
        // Uses 'reason' if present(ej. PutObject); else use 'detail-type' (ej. Object Created)
        String rawOperation = Optional.ofNullable(detail)
							                .map(EventDetail::reason)
							                .filter(r -> !r.isBlank())
						                .orElse(detailType);

        return AWSS3Operation.fromEventString(rawOperation);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static record EventDetail(   @JsonProperty("version") String version,
								        @JsonProperty("bucket") BucketInfo bucket,
								        @JsonProperty("object") ObjectInfo object,
								        @JsonProperty("request-id") String requestId,
								        @JsonProperty("requester") String requester,
								        @JsonProperty("source-ip-address") String sourceIpAddress,
								        @JsonProperty("reason") String reason,
								        @JsonProperty("deletion-type") String deletionType
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static record BucketInfo(  @JsonProperty("name") String name
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static record ObjectInfo(@JsonProperty("key") String key,               // Object Storage Key (URL Encoded)
							        @JsonProperty("size") Long size,               // File size in bytes (can be null on Deletes)
							        @JsonProperty("etag") String eTag,             // MD5 checksum
							        @JsonProperty("version-id") String versionId,   // Object version ID if enabled
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