
package r01.kv.config;

import java.util.Properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01.kv.transform.namestrategy.KVNameStrategies;
import r01f.config.ContainsConfigData;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallType;

@MarshallType(as="moduleConfigForBucket")
@Accessors(prefix="_")
@RequiredArgsConstructor
public class KVModuleConfigForBucket
  implements ContainsConfigData {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
    @MarshallField(as="defaultBucket")
    @Getter  private final KVBucketName  _defaultBucket;

    @MarshallField(as="nameStrategy")
    @Getter  private final KVNameStrategies  _nameStrategy;

 	@MarshallField(as="properties")
    @Getter  private  final Properties _properties;
}


