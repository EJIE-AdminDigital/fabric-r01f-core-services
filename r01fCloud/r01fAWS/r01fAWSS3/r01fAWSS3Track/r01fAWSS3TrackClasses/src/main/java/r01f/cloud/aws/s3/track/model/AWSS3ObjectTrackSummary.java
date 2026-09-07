package r01f.cloud.aws.s3.track.model;

import java.time.Instant;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.cloud.aws.s3.model.AWSS3BucketID;
import r01f.cloud.aws.s3.model.AWSS3ObjectKey;
import r01f.cloud.aws.s3.model.AWSS3ObjectSummary;
import r01f.debug.Debuggable;

@NoArgsConstructor
@Accessors(prefix="_")
public class AWSS3ObjectTrackSummary
  implements Debuggable {
/////////////////////////////////////////////////////////////////////////////////////////
//	fields
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter @Setter AWSS3BucketID _bucket;
	@Getter @Setter AWSS3ObjectKey _key;
	@Getter @Setter Instant _lastModified;
	@Getter @Setter boolean _isFolder;	
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static AWSS3ObjectTrackSummary from(final AWSS3ObjectSummary summary) {
		AWSS3ObjectTrackSummary track = new AWSS3ObjectTrackSummary();
		track.setBucket(summary.getBucket());
		track.setKey(summary.getKey());
		track.setLastModified(summary.getLastModified());
		return track;
	}
	
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
 	@Override
	public CharSequence debugInfo() {
	        return "S3ObjectTrackSummary {" +
	                	"bucket='" + _bucket + '\'' + ',' +
	                	"key='" + _key + '\'' + ',' +
	                	"isFolder='" + _isFolder + '\'' +
	                '}';
	   }
}
