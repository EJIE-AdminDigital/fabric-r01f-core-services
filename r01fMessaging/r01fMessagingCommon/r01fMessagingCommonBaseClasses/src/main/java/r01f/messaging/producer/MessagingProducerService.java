package r01f.messaging.producer;

import r01f.messaging.MessagingService;
import r01f.messaging.model.MessageSubcriber;
import r01f.messaging.model.MessagingFactory;

/**
 * Interface for Distributed Messaging Producer
 * @param <O>
 * @param <M>
 */
public interface MessagingProducerService<V,S extends MessageSubcriber>// S-> ( TO or Subscriber)
	     extends MessagingService {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	public abstract ProducerServiceResponse<S> send(final S to,
		                                            final MessagingFactory<V> messageToBeDeliveredFactory);


}
