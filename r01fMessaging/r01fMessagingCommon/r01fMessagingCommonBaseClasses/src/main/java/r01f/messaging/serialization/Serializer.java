package r01f.messaging.serialization;

public interface Serializer<T> {

	byte[] serialize(final T obj);

}
