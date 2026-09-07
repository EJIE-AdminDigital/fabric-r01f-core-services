package r01f.cloud.aws.s3.track.spring.service.polling;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import r01f.cloud.aws.s3.model.AWSS3BucketID;
import r01f.cloud.aws.s3.model.AWSS3FolderPath;


@Accessors(prefix="_")
@NoArgsConstructor
public class AWSS3MetadataTrackPollingConfig {
	
/////////////////////////////////////////////////////////////////////////////////
//	CONSTANTS
////////////////////////////////////////////////////////////////////////////////	
	public static final String PROPERTIES_PREFIX = "s3-polling";
	
/////////////////////////////////////////////////////////////////////////////////
//CONSTANTS
////////////////////////////////////////////////////////////////////////////////
    @Setter @Getter private AWSS3BucketID 		_bucket;    
    @Setter @Getter private long       _fixedDelayMs;    
    @Setter @Getter  private List<AWSS3FolderPath> _dataPaths;
}