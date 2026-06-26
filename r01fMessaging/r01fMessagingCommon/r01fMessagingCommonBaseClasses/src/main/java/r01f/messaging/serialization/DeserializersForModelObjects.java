package r01f.messaging.serialization;



import lombok.experimental.Accessors;
import r01f.guids.OID;
import r01f.guids.OIDs;
import r01f.messaging.serialization.DeserializersForBasicTypes.MarshalledObjectDeserializerBase;
import r01f.mime.MimeType;
import r01f.mime.MimeTypes;
import r01f.model.ModelObject;
import r01f.objectstreamer.Marshaller;

public class DeserializersForModelObjects {
/////////////////////////////////////////////////////////////////////////////////////////
//	Deserializer For Model Object
////////////////////////////////////////////////////////////////////////////////////////
	@Accessors(prefix="_")
	public static  class ModelObjectDeserializerBase<M extends ModelObject>
		        extends MarshalledObjectDeserializerBase<M> {

		public ModelObjectDeserializerBase( final Class<M> mappedType,
				                            final Marshaller marshaller) {
			super(mappedType,
				  MimeTypes.APPLICATION_JSON,
				  marshaller );
		}
		public ModelObjectDeserializerBase(final Class<M> mappedType,
				                           final Marshaller marshaller,
									       final MimeType mimeType) {
			super(mappedType,
				  mimeType, marshaller);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
// Deserializer For OID
////////////////////////////////////////////////////////////////////////////////////////
	@Accessors(prefix="_")
	public static  class OIDDeserializer<O extends OID>
			implements Deserializer<O> {

		private final Class<O>  _oidType;

		public OIDDeserializer( final Class<O> oidType) {
			_oidType = oidType;
		}

		@Override
		public O deserialize(final byte[] data) {
			return OIDs.createOIDFromString(_oidType, new String(data));
		}
	}
}
