package r01f.messaging.producer;



/**
 * Models a messaging producer service response
 * @param <A>
 */
public interface ProducerServiceResponse<T> {

	public T getTo();

	public boolean wasSuccessful();

	public ProducerServiceResponseError<T> asResponseError();

	public ProducerServiceResponseOK<T> asResponseOK();
}
