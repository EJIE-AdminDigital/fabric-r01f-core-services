package r01f.cloud.aws.s3.events.listener;



import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.extern.slf4j.Slf4j;
import r01f.cloud.aws.s3.client.api.AWSS3ClientAPIProvider;
import r01f.cloud.aws.s3.model.AWSS3BucketID;
import r01f.cloud.aws.s3.model.AWSS3ObjectGetResult;
import r01f.cloud.aws.s3.model.AWSS3ObjectKey;

/**
 * Production implementation of the asynchronous object retriever.
 * It offloads the blocking network I/O stream reading to the provided Executor pool,
 * preventing Event Listeners (e.g., Kafka/SQS workers) from freezing.
 */
@Slf4j

public class AWSS3ObjectRetrieverAsyncImpl 
  implements AWSS3ObjectRetrieverAsync {

   
    private final AWSS3ClientAPIProvider _s3ClientAPIProvider;
    private final Executor _s3AsyncExecutor;
//////////////////////////////////////////////////////////////
 /// CONSTRUCTORS
 /////////////////////////////////////////////////////////////
    public AWSS3ObjectRetrieverAsyncImpl(final AWSS3ClientAPIProvider s3ClientAPIProvider,
    		                             final AWSS3ObjectRetrieverAsyncConfig config) {    
    	
    	this(s3ClientAPIProvider,
    		 _createExecutorFrom(config));    	
    }
    
    public AWSS3ObjectRetrieverAsyncImpl(final AWSS3ClientAPIProvider s3ClientAPIProvider,
                                         final Executor s3AsyncExecutor) {
    	_s3ClientAPIProvider  = s3ClientAPIProvider;
    	_s3AsyncExecutor  = s3AsyncExecutor;
    }
 //////////////////////////////////////////////////////////////
 /// METHODS
 /////////////////////////////////////////////////////////////
    @Override
    public CompletableFuture<AWSS3ObjectGetResult> retrieveObject(final AWSS3BucketID bucket, final AWSS3ObjectKey key) {
        if (_s3ClientAPIProvider == null 
        		|| bucket == null || key == null 
        		|| _s3AsyncExecutor == null) {
            log.error("[AWSS3ClientAPIObjectRetrieverAsync] Cannot retrieve object async: Missing required infrastructure or parameters.");
            return CompletableFuture.completedFuture(null);
        }

        // Dispatch the blocking 'get()' operation onto the background executor thread pool
        return CompletableFuture.supplyAsync(() -> {
											            log.warn("[AWSS3ObjectRetrieverAsync] Background worker thread started downloading key '{}'", key.asString());
											            return _s3ClientAPIProvider.forBucket(bucket)
											            										.forObjects()
											                                       .getObject(bucket, key);
											                                       
											        }, _s3AsyncExecutor)
        											.exceptionally(ex -> {
																            log.error("[AWSS3ClientAPIObjectRetrieverAsync] Critical failure during background download of '{}': {}", 
																                      key.asString(), ex.getMessage(), ex);
																            return null;
											        });
    }
    
    

 /////////////////////////////////////////////////////////////////////////////////////////
 // PRIVATED METHODS
 /////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Creates and initializes a ThreadPoolTaskExecutor tailored by the provided 
     * configuration blueprint metrics.
     * * @param config The structural async retriever properties context.
     * @return A fully initialized, active and thread-safe Executor instance.
     */
     static Executor _createExecutorFrom(final AWSS3ObjectRetrieverAsyncConfig config) {
        if (config == null) {
            log.error("[ExecutorFactory] Cannot build executor. Configuration context blueprint is null.");
            throw new IllegalArgumentException("Configuration context cannot be null");
        }

        log.info("[ExecutorFactory] Assembling dedicated S3 async pool. CoreSize: {}, MaxSize: {}, QueueCapacity: {}, Prefix: '{}'",
		                 config.getCorePoolSize(), 
		                 config.getMaxPoolSize(), 
		                 config.getQueueCapacity(), 
		                 config.getThreadNamePrefix());

        // 1. Default java / executo based on java. It's posiblle to use Spring imp usig  the other constructor.
        ThreadFactory threadFactory = new ThreadFactory() {
												            private final AtomicInteger _threadNumber = new AtomicInteger(1);
												            
												            @Override
												            public Thread newThread(final Runnable r) {
												                Thread t = new Thread(r, config.getThreadNamePrefix() + _threadNumber.getAndIncrement());
												                // Ensure threads don't block application shutdown if main thread dies
												                if (t.isDaemon()) t.setDaemon(false);
												                if (t.getPriority() != Thread.NORM_PRIORITY) t.setPriority(Thread.NORM_PRIORITY);
												                return t;
								            }
        };
        
        ThreadPoolExecutor executor = new ThreadPoolExecutor(config.getCorePoolSize(),
											                 config.getMaxPoolSize(),
											                 60L, TimeUnit.SECONDS,
											                 new LinkedBlockingQueue<>(config.getQueueCapacity()),
											                 threadFactory,
											                 new ThreadPoolExecutor.CallerRunsPolicy() // Safe fallback if the queue fills up completely
        );

        // Pre-start core threads to have the pool warm and ready for incoming S3 events
        executor.prestartAllCoreThreads();        
        return executor;
    }

}