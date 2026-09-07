package r01f.cloud.aws.s3.events.model;

public interface AWSS3EventServiceSubscriber {

	<S extends AWSS3EventServiceSubscriber > S as(final Class<S> type);
}
