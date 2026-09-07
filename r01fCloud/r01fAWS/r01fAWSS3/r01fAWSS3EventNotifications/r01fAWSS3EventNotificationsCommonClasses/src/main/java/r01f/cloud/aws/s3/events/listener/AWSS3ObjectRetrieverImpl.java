package r01f.cloud.aws.s3.events.listener;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import r01f.cloud.aws.s3.client.api.AWSS3ClientAPI;
import r01f.cloud.aws.s3.client.api.AWSS3ClientAPIProvider;

import r01f.cloud.aws.s3.model.AWSS3BucketID;
import r01f.cloud.aws.s3.model.AWSS3ObjectGetResult;
import r01f.cloud.aws.s3.model.AWSS3ObjectKey;

/**
 * Standard production implementation of the synchronous object retriever.
 * It resolves the concrete, isolated AWSS3ClientAPI instance per bucket 
 * context using the underlying multi-tenant provider.
 */
@Slf4j
@RequiredArgsConstructor
public class AWSS3ObjectRetrieverImpl 
  implements AWSS3ObjectRetriever {
 
    private final AWSS3ClientAPIProvider _s3ClientAPIProvider;

    @Override
    public AWSS3ObjectGetResult retrieveObject(final AWSS3BucketID bucket, final AWSS3ObjectKey key) {
        if (_s3ClientAPIProvider == null 
        				|| bucket == null || key == null) {
            log.error("[AWSS3ClientAPIObjectRetriever] Cannot retrieve object: Missing required infrastructure or identifiers.");
            return null;
        }

        log.debug("[AWSS3ClientAPIObjectRetriever] Routing synchronous download for bucket '{}' and key '{}'", 
                  bucket.asString(), key.asString());

        try {
            // 1. Thread-safely get or create the tenant-specific client API facade
            AWSS3ClientAPI concreteClient = _s3ClientAPIProvider.forBucket(bucket);
            
            // 2. Execute the synchronous blocking read stream via the native sub-api delegation
            return concreteClient.forObjects()
                                 .getObject(bucket, key);
                                 
        } catch (final Throwable ex) {
            log.error("[AWSS3ClientAPIObjectRetriever] Failed to retrieve object from S3 backend: {}", ex.getMessage(), ex);
            throw ex; 
        }
    }
}