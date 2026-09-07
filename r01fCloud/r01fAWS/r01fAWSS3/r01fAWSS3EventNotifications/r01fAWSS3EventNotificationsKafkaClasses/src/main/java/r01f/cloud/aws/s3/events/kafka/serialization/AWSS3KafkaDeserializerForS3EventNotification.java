package r01f.cloud.aws.s3.events.kafka.serialization;

import lombok.experimental.Accessors;
import r01f.cloud.aws.s3.events.consumer.AWSS3EventNotification;
import r01f.cloud.aws.s3.events.serialization.AWSS3DeserializersForEventNotificationsObjects.AWSS3ModelObjectDeserializerForEventNotification;
import r01f.objectstreamer.Marshaller;

@Accessors(prefix="_")
public  class AWSS3KafkaDeserializerForS3EventNotification
	  extends AWSS3ModelObjectDeserializerForEventNotification 
   implements AWS3KafkaDeserializer<AWSS3EventNotification> {

/////////////////////////////////////////////////////////////////////////////////////////
// CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AWSS3KafkaDeserializerForS3EventNotification(final Marshaller marshaller) {
		super(marshaller);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	DESERIALIZE
/////////////////////////////////////////////////////////////////////////////////////////	
	/*@Override
	public AWSS3EventNotification  deserialize(final String topic, final byte[] data) {
		return super.deserialize(data);
	}*/
	
	@Override
	public AWSS3EventNotification deserialize(String topic, byte[] data) {
	    if (data == null || data.length == 0) return null;

	    try {	    
	        AWSS3EventNotification notification =super.deserialize(data);	       
	        return notification;
	    } catch (Throwable e) {
	        throw new IllegalArgumentException(e);
	    }
	}
}