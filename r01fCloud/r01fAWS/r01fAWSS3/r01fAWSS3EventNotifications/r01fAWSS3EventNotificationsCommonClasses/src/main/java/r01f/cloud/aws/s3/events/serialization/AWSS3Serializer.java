package r01f.cloud.aws.s3.events.serialization;

public interface AWSS3Serializer<T> {

	byte[] serialize(final T obj);

}
