package r01f.messaging.kafka.test.spring;

import java.util.concurrent.CompletableFuture;

import jakarta.inject.Provider;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.cloud.aws.s3.events.consumer.AWSS3EventNotification;
import r01f.cloud.aws.s3.events.consumer.AWSS3EventNotification.S3EventRecord;
import r01f.cloud.aws.s3.events.listener.AWSS3ObjectRetrieverAsync;
import r01f.cloud.aws.s3.model.AWSS3BucketID;
import r01f.cloud.aws.s3.model.AWSS3ObjectKey;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;


@Slf4j
@RequiredArgsConstructor
@Accessors(prefix="_")
public class Z99AWSS3KafkaEventListenerDelegate {

/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
    @Getter private final Provider<SecurityContext> _securityContextProvider;                            
    @Getter protected final Marshaller _marshaller;        
    @Getter protected final AWSS3ObjectRetrieverAsync _objectRetrieverAsync;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  DELEGATE PROCESSING WORKFLOW
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Unwraps the S3 notification payload under the current security context execution boundaries 
     * and triggers the non-blocking background retrieval pipelines.
     */
    public void process(final AWSS3EventNotification notification) {
        if (notification == null || notification.records() == null) {
            log.warn("[Z99WebhookDelegate] Aborting process: Incoming notification model or records matrix is null.");
            return;
        }
        notification.firstRecord()
        			.ifPresent(record -> {
        									this.executeAsyncObjectRetrieval(record);
        });
    } 

/////////////////////////////////////////////////////////////////////////////////////////
//  PRIVATE HELPER PIPELINES
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Internal plumbing helper to route record coordinates into the asynchronous cloud infrastructure.
     */
    private CompletableFuture<Void> executeAsyncObjectRetrieval(final S3EventRecord record) {
        if (_objectRetrieverAsync == null) {
            log.error("[Z99WebhookDelegate] Missing non-blocking asynchronous retriever infrastructure bean dependency.");
            return CompletableFuture.completedFuture(null);
        }

        AWSS3BucketID bucket = AWSS3BucketID.forId(record.s3().bucket().name());
        AWSS3ObjectKey key = AWSS3ObjectKey.forId(record.s3().object().key());

        log.info("[Z99WebhookDelegate] Offloading download interaction asynchronously for tenant bucket '{}' and storage key '{}'", 
                 bucket.asString(), key.asString());

        // Dispatch background operations securely
        // -----------------------------------------------------------------------------------------
        // S3 Async Execution Post-Processing Options:
        // The object retrieval itself is non-blocking and fully asynchronous. 
        // Once completed, you must chain the downstream business execution flow:
        // -----------------------------------------------------------------------------------------
        
        // [OPTION 1] .thenAccept(s3ObjectGetResult -> { ... })
        // - SYNCHRONOUS downstream execution relative to the worker thread.
        // - The background S3 worker thread ('z99-s3-async-x') will execute this block sequentially.
        // - Use ONLY for lightweight operations (e.g., quick validation, logging). 
        //   Warning: Long-running or blocking code here will hog and starve the S3 download pool.
        
        // [OPTION 2] .thenAcceptAsync(s3ObjectGetResult -> { ... })
        // - ASYNCHRONOUS downstream execution.
        // - The S3 worker thread hands off the result and is instantly freed to process the next download.
        // - The business logic block is dispatched to a separate thread pool (ForkJoinPool.commonPool() by default).
        // - RECOMMENDED for heavy, blocking, or CPU-intensive business engines (e.g., parsing, DB writes).
        return _objectRetrieverAsync.retrieveObject(bucket, key)
                                    // ..the retrieve is async , after you must define : thenAccept or theAcceptAsync 
        		                    //    [OP 1] retrieveObject(bucket, key)
        							//				.thenAccept(s3ObjectGetResult  --> 
				        		   //    [OP 2] retrieveObject(bucket, key)
								  //				.thenAcceptAsync(s3ObjectGetResult  --> 
        		                   
        							.thenAcceptAsync(s3ObjectGetResult -> {
								                                        if (s3ObjectGetResult != null ) {
								                                            log.warn("\n\n [Z99WebhookDelegate] Async stream download completed successfully for: {}", 
								                                                     s3ObjectGetResult.getKey().asString());
								                                            
								                                            // TODO  [ by sample app ] : Inject your background business rule core engines right here
								                                            //   ..call to api or call to rest...etc...
								                                            // this.dispatchToBusinessEngine(s3ObjectGetResult);
								                                        }
                                    })
                                    .exceptionally(ex -> {
						                                        log.error("[Z99WebhookDelegate] Uncaught runtime failure during worker thread parsing sequence for key '{}': {}", 
						                                                  key.asString(), ex.getMessage(), ex);
						                                        return null;
                                    });
    }
}