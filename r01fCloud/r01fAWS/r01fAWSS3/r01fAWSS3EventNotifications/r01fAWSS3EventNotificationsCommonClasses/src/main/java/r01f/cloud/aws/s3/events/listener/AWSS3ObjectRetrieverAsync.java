package r01f.cloud.aws.s3.events.listener;



import java.util.concurrent.CompletableFuture;


import r01f.cloud.aws.s3.model.AWSS3BucketID;
import r01f.cloud.aws.s3.model.AWSS3ObjectGetResult;
import r01f.cloud.aws.s3.model.AWSS3ObjectKey;

/**
 * Asynchronous contract for non-blocking object retrieval.
 * Designed to offload network operations to worker thread pools.
 */
public interface AWSS3ObjectRetrieverAsync {

    public CompletableFuture<AWSS3ObjectGetResult> retrieveObject(final AWSS3BucketID bucket,  final AWSS3ObjectKey key);
}