package r01f.cloud.aws.s3.track.client.api;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.cloud.aws.s3.client.api.AWSS3ClientAPI;
import r01f.cloud.aws.s3.track.client.api.delegates.AWSS3ClientAPIDelegateForTrack;
import r01f.exceptions.Throwables;


/**
 * Base type for every API implementation of S3API Track API
 */

@Accessors(prefix="_")
public class AWSS3ClientAPIForTrack {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	@SuppressWarnings("unused")
	private final AWSS3ClientAPI _s3Client;

	/**
	 * S3 FOLDER  TRACK
	 */
	@Getter private final AWSS3ClientAPIDelegateForTrack  _forTrack;
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public AWSS3ClientAPIForTrack(final AWSS3ClientAPI s3Client) {
		// Checks if client  is not null AmazonS3Client
		if (s3Client == null) {
			Throwables.throwUnchecked(new IllegalArgumentException("In order to create instance of S3api, a client config, must be provided"));
		}
		_forTrack = new AWSS3ClientAPIDelegateForTrack(s3Client);
		_s3Client  = s3Client;
	 }
/////////////////////////////////////////////////////////////////////////////////////////
//  SUB-APIs
/////////////////////////////////////////////////////////////////////////////////////////
	public AWSS3ClientAPIDelegateForTrack forTrack() {
		return _forTrack;
	}
}
