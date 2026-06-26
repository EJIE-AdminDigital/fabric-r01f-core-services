package r01f.cloud.nexmo.api.interfaces;

import r01f.cloud.nexmo.model.Message;
import r01f.cloud.nexmo.model.Peer;
import r01f.cloud.nexmo.model.outbound.NexmoOutboundMessage;

public interface NexmoServicesForMessagingApplication {
	
	public NexmoOutboundMessage send(final Peer to, final Message message);
}
