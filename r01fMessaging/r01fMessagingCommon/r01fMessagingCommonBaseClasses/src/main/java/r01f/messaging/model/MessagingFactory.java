package r01f.messaging.model;

public interface MessagingFactory<V> {
	/**
	 * @return an instance of type T
	 */
	public V getValue();
}
