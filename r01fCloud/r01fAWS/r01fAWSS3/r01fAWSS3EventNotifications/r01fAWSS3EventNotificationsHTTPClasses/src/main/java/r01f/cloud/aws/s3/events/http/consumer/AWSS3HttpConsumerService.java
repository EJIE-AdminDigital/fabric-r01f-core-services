package r01f.cloud.aws.s3.events.http.consumer;



/**
 * Base contract for HTTP-based consumer services (Webhooks)
 * that process events originally originating from Kafka.
 *
 * @param <K> Event key type (e.g., OID)
 * @param <V> Event value/payload type (e.g., ModelObject)
 */
public interface AWSS3HttpConsumerService<K, V> {

	/**
	 * Returns the webhook configuration associated with this consumer.
	 */
	AWSS3HttpConsumerServiceConfig getWebhookConfig();

	/**
	 * Starts or activates the HTTP consumer lifecycle.
	 */
	void start();

	/**
	 * Gracefully stops the HTTP consumer.
	 */
	void stop();

	/**
	 * Checks whether the webhook endpoint is active and listening for requests.
	 */
	boolean isRunning();
}