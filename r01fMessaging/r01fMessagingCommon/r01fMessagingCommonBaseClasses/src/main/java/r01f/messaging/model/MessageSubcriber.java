package r01f.messaging.model;

public interface MessageSubcriber {

	<S extends MessageSubcriber > S as(final Class<S> type);
}
