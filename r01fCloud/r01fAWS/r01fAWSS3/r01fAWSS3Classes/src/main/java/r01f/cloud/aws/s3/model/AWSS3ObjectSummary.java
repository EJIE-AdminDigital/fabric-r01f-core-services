package r01f.cloud.aws.s3.model;

import java.time.Instant;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.debug.Debuggable;

@NoArgsConstructor
@Accessors(prefix="_")
public class AWSS3ObjectSummary
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
 	@Override
	public CharSequence debugInfo() {
	        return "S3ObjectSummary {" +
	                	"bucket='" + _bucket + '\'' + ',' +
	                	"key='" + _key + '\'' + ',' +
	                	"isFolder='" + _isFolder + '\'' +
	                '}';
	    }
}
