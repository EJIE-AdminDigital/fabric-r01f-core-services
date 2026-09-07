package r01f.cloud.aws.s3.track.api.interfaces.impl;

import java.time.Instant;
import java.util.Collection;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import r01f.cloud.aws.s3.client.api.AWSS3ClientAPI;

import r01f.cloud.aws.s3.model.AWSS3BucketID;
import r01f.cloud.aws.s3.model.AWSS3FolderPath;
import r01f.cloud.aws.s3.model.AWSS3ObjectSummary;
import r01f.cloud.aws.s3.track.api.interfaces.AWSS3ServicesForTrack;
import r01f.cloud.aws.s3.track.model.AWSS3ObjectTrackSummary;
import r01f.util.types.collections.CollectionUtils;

@Slf4j
public class AWSS3ServicesForTrackImpl	
  implements AWSS3ServicesForTrack {
	
	final AWSS3ClientAPI  _client;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AWSS3ServicesForTrackImpl(final AWSS3ClientAPI s3Client)  {
		_client  = s3Client;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  TRACK
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Collection<AWSS3ObjectTrackSummary> trackModifiedSince(final AWSS3BucketID bucket, 
															      final AWSS3FolderPath folderPath,
															      final Instant modifiedSince) {
		log.warn("########################################################################");
		log.warn(" ..track last modified at '{}' ,"
				+ "			 since '{}'", folderPath,
										  modifiedSince);
		log.warn("########################################################################");
		Collection<AWSS3ObjectSummary> results = _client.getForFiler()
				 										.listModifiedFolderContents(bucket, 
				 																	folderPath,
				 																	modifiedSince);
		if (CollectionUtils.isNullOrEmpty(results)) {
			return null;
		}
	    return results.stream()
	    		       .map( r->  AWSS3ObjectTrackSummary.from(r))
	    		       .collect(Collectors.toList());
	}
		
}
