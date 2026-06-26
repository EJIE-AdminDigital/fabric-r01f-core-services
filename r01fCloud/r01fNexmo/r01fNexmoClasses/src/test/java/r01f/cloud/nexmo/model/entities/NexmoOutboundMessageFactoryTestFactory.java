package r01f.cloud.nexmo.model.entities;

import r01f.cloud.nexmo.NexmoAPI.MessagingService;
import r01f.cloud.nexmo.model.Message;
import r01f.cloud.nexmo.model.MessageContents.TextMessageContent;
import r01f.cloud.nexmo.model.Peer;
import r01f.cloud.nexmo.model.outbound.NexmoOutboundMessage;
import r01f.internal.R01FAppCodes;
import r01f.objectstreamer.MarshallerBuilder;
import r01f.patterns.Factory;
import r01f.types.contact.Phone;

public class NexmoOutboundMessageFactoryTestFactory
  implements Factory<NexmoOutboundMessage> {
	
	@Override
	public NexmoOutboundMessage create() {
		return _buildOutboundMessage();
	}
	
	private static NexmoOutboundMessage _buildOutboundMessage() {
		NexmoOutboundMessage  outboundMessage  = new NexmoOutboundMessage();
		Peer from = new Peer(MessagingService.whatsapp, Phone.create("14157386170"));
		outboundMessage.setFrom(from);
		Peer to =  new Peer(MessagingService.whatsapp, Phone.create("34616178858"));
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
	public static void main(final String[] argv) {
	    String json = MarshallerBuilder.findTypesToMarshallAt(R01FAppCodes.APP_CODE)
                                     	.build()
                                     .forWriting()
								     .toJson(new NexmoOutboundMessageFactoryTestFactory().create());
		System.out.println(json);
	}
}
