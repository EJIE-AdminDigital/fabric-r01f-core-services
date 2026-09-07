package r01f.cloud.aws.s3.track.client.api.delegates;

import java.time.Instant;
import java.util.Collection;

import r01f.cloud.aws.s3.client.api.AWSS3ClientAPI;

import r01f.cloud.aws.s3.model.AWSS3BucketID;
import r01f.cloud.aws.s3.model.AWSS3FolderPath;
import r01f.cloud.aws.s3.track.api.interfaces.AWSS3ServicesForTrack;
import r01f.cloud.aws.s3.track.api.interfaces.impl.AWSS3ServicesForTrackImpl;
import r01f.cloud.aws.s3.track.model.AWSS3ObjectTrackSummary;

public class AWSS3ClientAPIDelegateForTrack
  implements AWSS3ServicesForTrack {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////	
	protected final AWSS3ServicesForTrackImpl _trackImpl;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AWSS3ClientAPIDelegateForTrack(final AWSS3ClientAPI s3Client) {
		_trackImpl = new  AWSS3ServicesForTrackImpl(s3Client);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EXISTENCE
/////////////////////////////////////////////////////////////////////////////////////////	

	@Override
	public Collection<AWSS3ObjectTrackSummary> trackModifiedSince(final AWSS3BucketID bucket, 
			                                                      final AWSS3FolderPath folderPath,
			                                                      final Instant modifiedSince) {
		return _trackImpl.trackModifiedSince(bucket,
											folderPath,
											 modifiedSince);
	}	
}
