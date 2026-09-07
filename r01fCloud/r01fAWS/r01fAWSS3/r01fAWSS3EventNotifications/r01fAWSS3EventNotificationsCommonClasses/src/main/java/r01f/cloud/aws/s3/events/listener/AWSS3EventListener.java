package r01f.cloud.aws.s3.events.listener;


import r01f.cloud.aws.s3.events.consumer.AWSS3Event;

public interface AWSS3EventListener< E extends AWSS3Event> {

	 public abstract void  handleEvent(final E e);
	
}
