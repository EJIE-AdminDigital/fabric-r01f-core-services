package r01f.cloud.aws.s3.migration.utils;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import r01f.cloud.aws.s3.client.api.AWSS3BucketConfig;
import r01f.cloud.aws.s3.client.api.AWSS3BucketConfigBuilder;
import r01f.cloud.aws.s3.client.api.AWSS3ClientConfig;
import r01f.cloud.aws.s3.client.api.AWSS3ClientConfigBuilder;
import r01f.guids.CommonOIDs.AppCode;
import r01f.xmlproperties.XMLProperties;
import r01f.xmlproperties.XMLPropertiesBuilder;
import r01f.xmlproperties.XMLPropertiesForApp;
import r01f.xmlproperties.XMLPropertiesForAppComponent;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.AbortMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteMarkerEntry;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListMultipartUploadsRequest;
import software.amazon.awssdk.services.s3.model.ListMultipartUploadsResponse;
import software.amazon.awssdk.services.s3.model.ListObjectVersionsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectVersionsResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.MultipartUpload;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.ObjectVersion;

/**
 * Utility class for purging (force deleting) objects and their versions,
 * as well as aborting multipart uploads, within a specified folder prefix in an S3 bucket.
 * This is particularly useful for cleaning up S3 buckets that have versioning enabled
 * or contain incomplete multipart uploads.
 */
public class S3PurgeUtil {

    /**
     * Initiates a complete purge operation for a given folder prefix in the S3 bucket.
     * This includes aborting all incomplete multipart uploads, deleting all current
     * versions of objects, and deleting all historical versions and delete markers.
     *
     * @param folderPrefix The prefix (folder path) within the S3 bucket to purge.
     *                     e.g., "myfolder/", "data/temp/".
     * @throws URISyntaxException 
     */
    public static void purge(Path folderPrefix) throws URISyntaxException {
        // Build S3 client configuration from XML properties
        AWSS3ClientConfig clientConfig = _buildS3Config();
        // Build S3 bucket configuration from XML properties
        AWSS3BucketConfig bucketConfig = _buildS3BucketConfig();

        // Initialize the S3 client with credentials and endpoint override
        S3Client s3 = S3Client.builder()
                .endpointOverride(clientConfig.getEndPoint().asUri()) // Custom endpoint for S3-compatible storage
                .region(Region.US_EAST_1) // Specify the region (can be arbitrary for custom endpoints)
                .credentialsProvider(StaticCredentialsProvider.create( // Provide AWS credentials
                        AwsBasicCredentials.create(clientConfig.getAccessKey().getId(),
                                clientConfig.getAccessSecret().getId())))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true) // Enable path-style access for custom endpoints
                        .build())
                .build();

        // Abort any incomplete multipart uploads under the specified prefix
        abortMultipartUploads(s3, bucketConfig.getDefaultBucket(), folderPrefix.toString());
        // Delete all current versions of objects under the specified prefix
        purgeCurrentObjects(s3, bucketConfig.getDefaultBucket(), folderPrefix.toString());
        // Delete all historical versions and delete markers under the specified prefix
        purgeAllVersions(s3, bucketConfig.getDefaultBucket(), folderPrefix.toString());
    }

    /**
     * Normalizes a given prefix string to ensure it's in a consistent S3-compatible format.
     * This involves replacing backslashes with forward slashes, removing leading slashes,
     * collapsing multiple slashes, and ensuring the prefix ends with a single forward slash.
     *
     * @param p The raw prefix string.
     * @return The normalized S3 prefix.
     */
    private static String normalizePrefix(String p) {
        if (p == null) return "";
        String k = p.replace('\\', '/').replaceFirst("^/+", "").replaceAll("/{2,}", "/");
        return k.endsWith("/") ? k : k + "/";
    }

    /**
     * Purges (deletes) all current versions of objects under a specified folder prefix.
     * This method iterates through objects in batches and performs a bulk delete.
     *
     * @param s3           The initialized S3Client instance.
     * @param bucket       The name of the S3 bucket.
     * @param folderPrefix The prefix (folder path) to purge objects from.
     */
    private static void purgeCurrentObjects(S3Client s3, String bucket, String folderPrefix) {
        final String prefix = normalizePrefix(folderPrefix);
        String token = null; // Continuation token for paginated listing
        do {
            // List objects in batches of 1000
            ListObjectsV2Request req = ListObjectsV2Request.builder()
                    .bucket(bucket)
                    .prefix(prefix)
                    .continuationToken(token)
                    .maxKeys(1000)
                    .build();
            ListObjectsV2Response lo = s3.listObjectsV2(req);

            if (!lo.contents().isEmpty()) {
                // Map listed objects to ObjectIdentifier for bulk deletion
                List<ObjectIdentifier> batch = lo.contents().stream()
                        .map(o -> ObjectIdentifier.builder().key(o.key()).build())
                        .collect(Collectors.toList());
                // Create a bulk delete request
                DeleteObjectsRequest del = DeleteObjectsRequest.builder()
                        .bucket(bucket)
                        .delete(Delete.builder().objects(batch).build())
                        .build();
                s3.deleteObjects(del); // Execute bulk delete
            }
            token = lo.nextContinuationToken(); // Get next token for pagination
        } while (token != null); // Continue until no more objects are listed
    }

    /**
     * Aborts all incomplete multipart uploads under a specified folder prefix.
     * This prevents orphaned parts from consuming storage.
     *
     * @param s3           The initialized S3Client instance.
     * @param bucket       The name of the S3 bucket.
     * @param folderPrefix The prefix (folder path) to abort multipart uploads from.
     */
    private static void abortMultipartUploads(S3Client s3, String bucket, String folderPrefix) {
        final String prefix = normalizePrefix(folderPrefix);
        String keyMarker = null, uploadIdMarker = null; // Markers for paginated listing
        while (true) {
            // List multipart uploads in batches of 1000
            ListMultipartUploadsRequest req = ListMultipartUploadsRequest.builder()
                    .bucket(bucket)
                    .prefix(prefix)
                    .keyMarker(keyMarker)
                    .uploadIdMarker(uploadIdMarker)
                    .maxUploads(1000)
                    .build();
            ListMultipartUploadsResponse lmu = s3.listMultipartUploads(req);

            // Abort each listed multipart upload
            for (MultipartUpload u : lmu.uploads()) {
                AbortMultipartUploadRequest abortReq = AbortMultipartUploadRequest.builder()
                        .bucket(bucket)
                        .key(u.key())
                        .uploadId(u.uploadId())
                        .build();
                s3.abortMultipartUpload(abortReq);
            }

            // Check if there are more uploads to list
            if (Boolean.TRUE.equals(lmu.isTruncated())) {
                keyMarker = lmu.nextKeyMarker();
                uploadIdMarker = lmu.nextUploadIdMarker();
            } else {
                break; // No more uploads, exit loop
            }
        }
    }

    /**
     * Purges (deletes) all versions of objects, including delete markers,
     * under a specified folder prefix. This is crucial for buckets with versioning enabled.
     *
     * @param s3           The initialized S3Client instance.
     * @param bucket       The name of the S3 bucket.
     * @param folderPrefix The prefix (folder path) to purge all versions from.
     */
    private static void purgeAllVersions(S3Client s3, String bucket, String folderPrefix) {
        final String prefix = normalizePrefix(folderPrefix);
        String keyMarker = null, versionIdMarker = null; // Markers for paginated listing
        while (true) {
            // List object versions (including delete markers) in batches of 1000
            ListObjectVersionsRequest req = ListObjectVersionsRequest.builder()
                    .bucket(bucket)
                    .prefix(prefix)
                    .keyMarker(keyMarker)
                    .versionIdMarker(versionIdMarker)
                    .maxKeys(1000)
                    .build();
            ListObjectVersionsResponse v = s3.listObjectVersions(req);

            // Collect all object versions and delete markers into a single list for deletion
            List<ObjectIdentifier> toDel = new ArrayList<>(v.versions().size() + v.deleteMarkers().size());
            for (ObjectVersion ov : v.versions()) {
                toDel.add(ObjectIdentifier.builder().key(ov.key()).versionId(ov.versionId()).build());
            }
            for (DeleteMarkerEntry dm : v.deleteMarkers()) {
                toDel.add(ObjectIdentifier.builder().key(dm.key()).versionId(dm.versionId()).build());
            }

            // Perform bulk deletion in batches of up to 1000 objects
            for (int i = 0; i < toDel.size(); i += 1000) {
                int end = Math.min(i + 1000, toDel.size());
                List<ObjectIdentifier> slice = toDel.subList(i, end);
                if (!slice.isEmpty()) {
                    DeleteObjectsRequest del = DeleteObjectsRequest.builder()
                            .bucket(bucket)
                            .delete(Delete.builder().objects(slice).build())
                            .build();
                    s3.deleteObjects(del); // Execute bulk delete
                }
            }

            // Check if there are more versions to list
            if (Boolean.TRUE.equals(v.isTruncated())) {
                keyMarker = v.nextKeyMarker();
                versionIdMarker = v.nextVersionIdMarker();
            } else {
                break; // No more versions, exit loop
            }
        }
    }

    /**
     * Builds the {@link AWSS3ClientConfig} for the given environment using XML properties.
     * <p>
     * The configuration is read from the {@code r01f} application properties under the
     * {@code s3} component, using the XML path:
     * <pre>
     *   /moduleConfigForS3/s3ClientConfig
     * </pre>
     *
     * @return an {@link AWSS3ClientConfig} instance containing client level S3 settings.
     */
    private static AWSS3ClientConfig _buildS3Config() {
		// create properties
		XMLProperties xmlProps = XMLPropertiesBuilder.create()
													 .notUsingCache();
		XMLPropertiesForApp testProps = xmlProps.forApp(AppCode.forId("r01f"));
		XMLPropertiesForAppComponent props = testProps.forComponent("s3");
		
		AWSS3ClientConfig s3Config = AWSS3ClientConfigBuilder.fromXMLProperties(props, "/moduleConfigForS3/s3ClientConfig");

		return s3Config;
	}
    
    /**
     * Builds the {@link AWSS3BucketConfig} for the given environment using XML properties.
     * <p>
     * The configuration is read from the {@code r01f} application properties under the
     * {@code s3} component, using the XML path:
     * <pre>
     *   /moduleConfigForS3/moduleConfigForBucket
     * </pre>
     *
     * @return an {@link AWSS3BucketConfig} instance describing the target S3 bucket layout and options.
     */
	private static AWSS3BucketConfig _buildS3BucketConfig() {
		// create properties
		XMLProperties xmlProps = XMLPropertiesBuilder.create()
													 .notUsingCache();
		XMLPropertiesForApp testProps = xmlProps.forApp(AppCode.forId("r01f"));
		XMLPropertiesForAppComponent props = testProps.forComponent("s3");
		
		AWSS3BucketConfig s3BucketConfig = AWSS3BucketConfigBuilder.fromXMLProperties(props, "/moduleConfigForS3/moduleConfigForBucket");

		return s3BucketConfig;
	}
}