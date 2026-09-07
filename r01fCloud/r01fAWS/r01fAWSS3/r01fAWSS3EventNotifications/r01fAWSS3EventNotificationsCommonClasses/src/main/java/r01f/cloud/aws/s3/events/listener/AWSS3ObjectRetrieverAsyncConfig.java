package r01f.cloud.aws.s3.events.listener;



import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Configuration blueprint mapped directly from application properties/yml.
 * Allows runtime tuning of the dedicated S3 async worker thread pool.
 */

@Accessors(prefix = "_")
public class AWSS3ObjectRetrieverAsyncConfig {
/////////////////////////////////////////////////////////////////////////////////////////
//CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////	
	public static final String PROPERTIES_PREFIX = "s3.events.retriever";
/////////////////////////////////////////////////////////////////////////////////////////
//CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////	
    /** Minimum number of active threads always alive in the pool */
    @Setter @Getter private int _corePoolSize = 10;

    /** Maximum allowed threads under heavy spike loads */
    @Setter @Getter private int _maxPoolSize = 50;

    /** Capacity of the waiting queue before spawning new max threads */
    @Setter @Getter private int _queueCapacity = 100;

    /** Naming prefix applied to worker threads for easy debugging/thread-dumps */
    @Setter  @Getter private String _threadNamePrefix = "s3-async-worker-retriever-";
}