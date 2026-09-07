package r01f.cloud.aws.s3.events.spring.http.delegate.listener;

import java.util.concurrent.CompletableFuture;

import jakarta.inject.Provider;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.cloud.aws.s3.events.consumer.AWSS3Event;
import r01f.cloud.aws.s3.events.listener.AWSS3ObjectRetrieverAsync;
import r01f.cloud.aws.s3.model.AWSS3BucketID;
import r01f.cloud.aws.s3.model.AWSS3ObjectGetResult;
import r01f.cloud.aws.s3.model.AWSS3ObjectKey;
import r01f.cloud.aws.s3.model.AWSS3Operation;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;

@Slf4j
@RequiredArgsConstructor
@Accessors(prefix="_")
public class AWSS3EventWebhookWithAsycnRetieverListenerDelegateBase {

/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
    @Getter private final   Provider<SecurityContext> _securityContextProvider;                            
    @Getter protected final Marshaller _marshaller;        
    @Getter protected final AWSS3ObjectRetrieverAsync _objectRetrieverAsync;	
/////////////////////////////////////////////////////////////////////////////////////////
//  DELEGATE PROCESSING WORKFLOW
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Unwraps any S3 event payload (Native S3/MinIO or AWS EventBridge) under the current 
     * security context execution boundaries and triggers the non-blocking background retrieval pipelines.
     *
     * @param event The unified AWSS3Event model abstraction.
     */
    public void process(final AWSS3Event event) {
        if (event == null) {
            log.warn("[] Aborting process: Incoming AWSS3Event model is null.");
            return;
        }

        AWSS3BucketID bucket = event.getBucketName();
        AWSS3ObjectKey key = event.getObjectKey();
        AWSS3Operation operation = event.getOperation();

        if (bucket == null || key == null) {
            log.warn("[] Aborting process: Could not extract bucket or key coordinates from event. EventType: {}", 
                     event.getClass().getSimpleName());
            return;
        }

        // Optional filtering: Skip non-creations (e.g. DELETE operations)
        if (operation == AWSS3Operation.DELETE) {
            log.info("[] Skipping object retrieval for DELETE operation on bucket '{}' and key '{}'", 
                     bucket.asString(), key.asString());
            return;
        }

        this.executeAsyncObjectRetrieval(bucket,key);
    } 
    
    
    /**
     * Override this method with the retrieved object
     * @param s3ObjectGetResult
     */
	@SuppressWarnings("static-method")
	protected void dispatchToBusinessEngine(final AWSS3ObjectGetResult s3ObjectGetResult) {
		 log.warn("[] Override this method ,with the retieved object : {}", 
				  s3ObjectGetResult.getKey());
	
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  PRIVATE HELPER PIPELINES
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Internal plumbing helper to route record coordinates into the asynchronous cloud infrastructure.
     */
    private CompletableFuture<Void> executeAsyncObjectRetrieval(final AWSS3BucketID bucket, final AWSS3ObjectKey key) {
        if (_objectRetrieverAsync == null) {
            log.error("[] Missing non-blocking asynchronous retriever infrastructure bean dependency.");
            return CompletableFuture.completedFuture(null);
        }

        log.warn("[] Offloading download interaction asynchronously for tenant bucket '{}' and storage key '{}'", 
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
				                                        if (s3ObjectGetResult != null) {
				                                            log.warn("\n\n [] Async stream download completed successfully for: {}", 
				                                                     s3ObjectGetResult.getKey().asString());
				                                            
				                                            // Inject downstream business engine here:
				                                            this.dispatchToBusinessEngine(s3ObjectGetResult);
                                        }
                                    })
                                    .exceptionally(ex -> {
                                        log.error("[] Uncaught runtime failure during worker thread parsing sequence for key '{}': {}", 
                                                  key.asString(), ex.getMessage(), ex);
                                        return null;
                                    });
    }
}