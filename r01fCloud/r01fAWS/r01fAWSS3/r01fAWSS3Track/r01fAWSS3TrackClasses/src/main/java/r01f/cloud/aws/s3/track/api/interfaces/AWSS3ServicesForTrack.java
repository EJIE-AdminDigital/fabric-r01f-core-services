package r01f.cloud.aws.s3.track.api.interfaces;

import java.time.Instant;
import java.util.Collection;

import r01f.cloud.aws.s3.model.AWSS3BucketID;
import r01f.cloud.aws.s3.model.AWSS3FolderPath;
import r01f.cloud.aws.s3.track.model.AWSS3ObjectTrackSummary;


public interface AWSS3ServicesForTrack {

/////////////////////////////////////////////////////////////////////////////////////////
//EXIST BUCKETS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Track objects modified since last time.
	 * @param bucket
	 * @param folderPath
	 * @param modifiedSince
	 * @return
	 */
	public Collection<AWSS3ObjectTrackSummary> trackModifiedSince(final AWSS3BucketID bucket,
																  final AWSS3FolderPath folderPath,
																  final Instant modifiedSince);
}