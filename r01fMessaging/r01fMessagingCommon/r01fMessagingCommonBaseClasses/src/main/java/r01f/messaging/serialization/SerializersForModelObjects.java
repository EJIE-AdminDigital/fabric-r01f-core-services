package r01f.messaging.serialization;

import lombok.experimental.Accessors;
import r01f.guids.OID;
import r01f.messaging.serialization.SerializersForBasicTypes.MarshalledObjectSerializerBase;
import r01f.mime.MimeType;
import r01f.mime.MimeTypes;
import r01f.model.ModelObject;
import r01f.objectstreamer.Marshaller;
import r01f.util.types.Strings;

public class SerializersForModelObjects {
/////////////////////////////////////////////////////////////////////////////////////////
//	Serializer For Model Object
////////////////////////////////////////////////////////////////////////////////////////
	@Accessors(prefix="_")
	public static class ModelObjectSerializerBase<M extends ModelObject>
		        extends MarshalledObjectSerializerBase<M> {

		public ModelObjectSerializerBase(final Class<M> classType,
				                         final Marshaller marshaller) {
			super(classType,
				  MimeTypes.APPLICATION_JSON,
				  marshaller );
		}
		public ModelObjectSerializerBase(final Class<M> classType,
				                         final Marshaller marshaller,
									     final MimeType mimeType) {
			super(classType,
				  mimeType, marshaller);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
// Serializer For OID
////////////////////////////////////////////////////////////////////////////////////////
	@Accessors(prefix="_")
	public static class OIDSerializerBase<O extends OID>
			implements Serializer<O> {

		@Override
		public byte[] serialize(final  OID data) {
			String oid = data.asString();
			// write
			if (Strings.isNOTNullOrEmpty(oid))
				return oid.getBytes();
			throw new IllegalArgumentException("OID cannot be null");
		}
	}
}
