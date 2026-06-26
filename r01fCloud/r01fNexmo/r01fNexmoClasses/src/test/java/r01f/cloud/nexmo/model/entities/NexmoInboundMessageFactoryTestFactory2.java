package r01f.cloud.nexmo.model.entities;

import r01f.cloud.nexmo.NexmoAPI.MessagingService;
import r01f.cloud.nexmo.model.Message;
import r01f.cloud.nexmo.model.MessageContents.TextMessageContent;
import r01f.cloud.nexmo.model.NexmoIDS.PeerID;
import r01f.cloud.nexmo.model.Peer;
import r01f.cloud.nexmo.model.inbound.NexmoInboundMessage;
import r01f.internal.R01FAppCodes;
import r01f.objectstreamer.MarshallerBuilder;
import r01f.patterns.Factory;
import r01f.types.contact.Phone;

public class NexmoInboundMessageFactoryTestFactory2
  implements Factory<NexmoInboundMessage> {
	
	@Override
	public NexmoInboundMessage create() {
		return _buildOutboundMessage();
	}
	
	
	private static NexmoInboundMessage _buildOutboundMessage() {
		NexmoInboundMessage  outboundMessage  = new NexmoInboundMessage();
		Peer from = new Peer(MessagingService.whatsapp, Phone.create("14157386170"), PeerID.valueOf("66565665"));
		outboundMessage.setFrom(from);
		Peer to =  new Peer(MessagingService.whatsapp, Phone.create("34616178858"),PeerID.valueOf("66565665"));
		outboundMessage.setTo(to);
		Message message = new Message();
		TextMessageContent content = new TextMessageContent("Hola Caracola");
		message.setContent(content);
		outboundMessage.setMessage(message);
	
		return outboundMessage;		
	}
	
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unused")
	public static void main(final String[] argv) {
	    String json = MarshallerBuilder.findTypesToMarshallAt(R01FAppCodes.APP_CODE)
                                     	.build()
                                     .forWriting()
								     .toJson(new NexmoInboundMessageFactoryTestFactory2().create());
	    
	    System.out.println(json);
	    NexmoInboundMessage  re = MarshallerBuilder.findTypesToMarshallAt(R01FAppCodes.APP_CODE)
										             	.build()
										             .forReading()
										             .fromJson(json, NexmoInboundMessage.class);
	    
		   
		
	}
}
