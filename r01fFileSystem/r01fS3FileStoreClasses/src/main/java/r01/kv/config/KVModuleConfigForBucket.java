
package r01.kv.config;

import java.util.Properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01.kv.transform.namestrategy.KVNameStrategies;
import r01f.cloud.aws.s3.model.AWSS3Bucket;
import r01f.config.ContainsConfigData;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallType;
@RequiredArgsConstructor
@MarshallType(as="moduleConfigForBucket")
@Accessors(prefix="_")
public class KVModuleConfigForBucket
  implements ContainsConfigData {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
    @MarshallField(as="defaultBucket")
    @Getter  private final  KVBucketName  _defaultBucket;

    @MarshallField(as="nameStrategy")
    @Getter  private final  KVNameStrategies  _nameStrategy;

 	@MarshallField(as="properties")
    @Getter  private  final Properties _properties;
/////////////////////////////////////////////////////////////////////////////////////////
//	
///////////////////////////////////////////////////////////////////////////////////////// 	
 	public static class KVBucketName
	 		     extends AWSS3Bucket {

		private static final long serialVersionUID = 7124477844851489365L;
		
		public KVBucketName(final String id) {
			super(id);
		}
		public static KVBucketName forId(final String idAsString) {
			return new KVBucketName(idAsString);
		}
		public AWSS3Bucket asS3() {
			  return AWSS3Bucket.forId(this.asString());
		}
  	}
}


