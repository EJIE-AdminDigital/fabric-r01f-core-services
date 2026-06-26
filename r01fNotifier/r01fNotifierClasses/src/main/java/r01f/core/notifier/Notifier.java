package r01f.core.notifier;

public interface Notifier<M> { // Message & Subscriber(To)
	/**
	 * @return true if the notifier is enabled, false otherwise
	 */
	public boolean isEnabled();
	/**
	 * Send the notification
	 * @param <T>
	 * @param message
	 */
	public NotifierResponse sendNotification(final M message);

}
