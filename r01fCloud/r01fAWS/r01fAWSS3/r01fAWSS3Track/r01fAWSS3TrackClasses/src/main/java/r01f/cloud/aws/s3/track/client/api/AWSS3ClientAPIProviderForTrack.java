package r01f.cloud.aws.s3.track.client.api;



import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.cloud.aws.s3.client.api.AWSS3ClientAPI;
import r01f.cloud.aws.s3.client.api.AWSS3ClientAPIProvider;

import r01f.cloud.aws.s3.model.AWSS3BucketID;

@Slf4j
@Accessors(prefix="_")
public class AWSS3ClientAPIProviderForTrack {

/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * The base standard provider used to delegate and reuse core AWSS3ClientAPI instances.
	 */
	private final AWSS3ClientAPIProvider _s3ClientAPIProvider;
	
	/**
	 * Internal concurrent registry storing dedicated Tracking API clients indexed by bucket.
	 */
	private final Map<AWSS3BucketID, AWSS3ClientAPIForTrack> _trackApis = new ConcurrentHashMap<>();
/////////////////////////////////////////////////////////////////////////////////////////
// CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AWSS3ClientAPIProviderForTrack(final AWSS3ClientAPIProvider s3ClientAPIProvider) {
		if (s3ClientAPIProvider == null) {
			throw new IllegalArgumentException("The base AWSS3ClientAPIProvider must be provided");
		}
		_s3ClientAPIProvider = s3ClientAPIProvider;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  THE PROVIDER METHOD FOR TRACKING
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Provides a dedicated AWSS3ClientAPIForTrack instance for the specified bucket.
	 * It ensures that the tracking facade wraps the correct, isolated core client.
	 * * @param bucket The target S3 bucket identifier for tracking/audit operations.
	 * @return A fully initialized AWSS3ClientAPIForTrack wrapper.
	 */
	public AWSS3ClientAPIForTrack forBucket(final AWSS3BucketID bucket) {
		return _trackApis.computeIfAbsent(bucket, b -> {
			log.warn("[AWSS3ClientAPIProviderForTrack] Lazily creating dedicated Tracking API for bucket '{}'", b.asString());
			
			// 1. Thread-safely fetch or create the base API client from the standard provider
			AWSS3ClientAPI baseClient = _s3ClientAPIProvider.forBucket(b);
			
			// 2. Wrap it inside the tracking-specific API facade
			return new AWSS3ClientAPIForTrack(baseClient);
		});
	}
}