package r01f.cloud.aws.s3.events.serialization;

public interface AWSS3Deserializer<T> {

	T deserialize(final byte[] data);
}
