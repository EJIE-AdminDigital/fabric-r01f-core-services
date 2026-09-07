package r01f.cloud.aws.s3.events.kafka.serialization;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import jakarta.inject.Inject;
import lombok.experimental.Accessors;
import r01f.cloud.aws.s3.model.AWSS3ObjectKey;
import r01f.messaging.serialization.DeserializersForModelObjects.OIDDeserializer;

@Accessors(prefix="_")
public class AWSS3KafkaDeserializerForS3ObjectKey
	 extends OIDDeserializer<AWSS3ObjectKey>
  implements AWS3KafkaDeserializer<AWSS3ObjectKey> {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
	@Inject
	public AWSS3KafkaDeserializerForS3ObjectKey() {
		super(AWSS3ObjectKey.class);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	DESERIALIZE
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public AWSS3ObjectKey deserialize(final String topic, final byte[] data) {		
	    AWSS3ObjectKey rawObjectKey = super.deserialize(data);	    
	    if (rawObjectKey == null || rawObjectKey.asString() == null) {
	        return rawObjectKey;
	    }	    
	  
	    String encodedKey = rawObjectKey.asString();
	    String decodedKey = URLDecoder.decode(encodedKey, StandardCharsets.UTF_8);
	  
	    return AWSS3ObjectKey.forId(decodedKey);
	}
}
