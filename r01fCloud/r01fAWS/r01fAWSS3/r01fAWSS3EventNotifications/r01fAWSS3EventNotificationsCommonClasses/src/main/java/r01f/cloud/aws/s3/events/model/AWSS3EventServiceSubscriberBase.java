package r01f.cloud.aws.s3.events.model;

public abstract class AWSS3EventServiceSubscriberBase
 		   implements AWSS3EventServiceSubscriber {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override @SuppressWarnings("unchecked")
	public <S extends AWSS3EventServiceSubscriber> S as(final Class<S> type) {
		return (S) this;
	}
}
