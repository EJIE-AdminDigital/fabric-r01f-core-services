package r01f.messaging.consumer;

import r01f.messaging.MessagingService;
import r01f.messaging.model.MessageSubcriber;

/**
 * Interface for Distributed Messaging Consumer Service
 * @param <O>
 * @param <M>
 */
public interface MessagingConsumerService<V,S extends MessageSubcriber>// T-> TO
		extends MessagingService {
  //
}
