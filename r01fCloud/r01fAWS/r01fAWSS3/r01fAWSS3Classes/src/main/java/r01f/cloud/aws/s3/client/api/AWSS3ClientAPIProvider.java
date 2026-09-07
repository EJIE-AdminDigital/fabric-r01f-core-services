package r01f.cloud.aws.s3.client.api;

/**
 * User for multiple clients apis for buckets with diferente credentials, severs, etc...
 */
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.experimental.Accessors;
import r01f.cloud.aws.s3.model.AWSS3BucketID;


@Accessors(prefix="_")
public class AWSS3ClientAPIProvider {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final AWSS3ClientConfigSet _clientConfigSet;
	private final Map<AWSS3BucketID, AWSS3ClientAPI> _clientApis = new ConcurrentHashMap<>();
/////////////////////////////////////////////////////////////////////////////////////////
// CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AWSS3ClientAPIProvider(final AWSS3ClientConfigSet clientConfigSet) {
		if (clientConfigSet == null) {
			throw new IllegalArgumentException("A S3 bootstrap configuration provider must be provided");
		}
		_clientConfigSet = clientConfigSet;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  THE PROVIDER METHOD
/////////////////////////////////////////////////////////////////////////////////////////
	public AWSS3ClientAPI forBucket(final AWSS3BucketID bucket) {		
		return _clientApis.computeIfAbsent(bucket, b -> {
															AWSS3ClientConfig configByBucket = _clientConfigSet.getConfigFor(bucket);
															if (configByBucket == null) {
																throw new IllegalStateException("No S3 bootstrap configuration found for bucket: " + b.asString());
															}									
															return new AWSS3ClientAPI(configByBucket);
		});
	}
}