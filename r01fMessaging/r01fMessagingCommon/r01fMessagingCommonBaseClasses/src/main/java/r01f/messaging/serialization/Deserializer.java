package r01f.messaging.serialization;

public interface Deserializer<T> {

	T deserialize(final byte[] data);
}
