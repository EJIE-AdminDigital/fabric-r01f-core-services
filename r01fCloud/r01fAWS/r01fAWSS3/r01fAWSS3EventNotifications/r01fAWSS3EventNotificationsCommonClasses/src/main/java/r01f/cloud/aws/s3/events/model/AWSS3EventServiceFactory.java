package r01f.cloud.aws.s3.events.model;

public interface AWSS3EventServiceFactory<V> {
	/**
	 * @return an instance of type T
	 */
	public V getValue();
}
