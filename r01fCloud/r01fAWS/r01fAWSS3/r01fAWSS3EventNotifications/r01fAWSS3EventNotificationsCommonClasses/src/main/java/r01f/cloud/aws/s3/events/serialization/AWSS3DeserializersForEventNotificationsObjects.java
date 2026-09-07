package r01f.cloud.aws.s3.events.serialization;



import lombok.experimental.Accessors;
import r01f.cloud.aws.s3.events.consumer.AWSS3EventBridgeNotification;
import r01f.cloud.aws.s3.events.consumer.AWSS3EventNotification;
import r01f.cloud.aws.s3.events.serialization.AWSS3DeserializersForBasicTypes.MarshalledObjectDeserializerBase;
import r01f.guids.OID;
import r01f.guids.OIDs;
import r01f.mime.MimeType;
import r01f.mime.MimeTypes;
import r01f.objectstreamer.Marshaller;

public class AWSS3DeserializersForEventNotificationsObjects {
/////////////////////////////////////////////////////////////////////////////////////////
//	Deserializer For Event Notification
////////////////////////////////////////////////////////////////////////////////////////
	@Accessors(prefix="_")
	public static  class AWSS3ModelObjectDeserializerForEventNotification
		        extends MarshalledObjectDeserializerBase<AWSS3EventNotification> {

		public AWSS3ModelObjectDeserializerForEventNotification(final Marshaller marshaller) {
			super(AWSS3EventNotification.class,
				  MimeTypes.APPLICATION_JSON,
				  marshaller );
		}
		public AWSS3ModelObjectDeserializerForEventNotification( final Marshaller marshaller,
									                             final MimeType mimeType) {
			super(AWSS3EventNotification.class,
				  mimeType, marshaller);
		}
	}
	
	@Accessors(prefix="_")
	public static  class AWSS3ModelObjectDeserializerForEventBridgeNotification
		        extends MarshalledObjectDeserializerBase<AWSS3EventBridgeNotification> {

		public AWSS3ModelObjectDeserializerForEventBridgeNotification(final Marshaller marshaller) {
			super(AWSS3EventBridgeNotification.class,
				  MimeTypes.APPLICATION_JSON,
				  marshaller );
		}
		public AWSS3ModelObjectDeserializerForEventBridgeNotification( final Marshaller marshaller,
									                                  final MimeType mimeType) {
			super(AWSS3EventBridgeNotification.class,
				  mimeType, marshaller);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
// Deserializer For OID
////////////////////////////////////////////////////////////////////////////////////////
	@Accessors(prefix="_")
	public static  class AWSS2OIDDeserializer<O extends OID>
			implements AWSS3Deserializer<O> {

		private final Class<O>  _oidType;

		public AWSS2OIDDeserializer(final Class<O> oidType) {
			_oidType = oidType;
		}

		@Override
		public O deserialize(final byte[] data) {
			return OIDs.createOIDFromString(_oidType, new String(data));
		}
	}
}
