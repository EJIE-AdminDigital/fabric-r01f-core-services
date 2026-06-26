package r01f.cloud.nexmo.model.entities;

import r01f.cloud.nexmo.NexmoAPI.MessagingService;
import r01f.cloud.nexmo.model.Currency;
import r01f.cloud.nexmo.model.MessageError;
import r01f.cloud.nexmo.model.MessageStatus;
import r01f.cloud.nexmo.model.MessageUsage;
import r01f.cloud.nexmo.model.NexmoIDS.PeerID;
import r01f.cloud.nexmo.model.Peer;
import r01f.cloud.nexmo.model.status.NexmoMessageStatus;
import r01f.internal.R01FAppCodes;
import r01f.objectstreamer.MarshallerBuilder;
import r01f.patterns.Factory;
import r01f.types.contact.Phone;

public class NexmoMessageStatusFactoryTest 
  implements Factory<NexmoMessageStatus> {
	
	@Override
	public NexmoMessageStatus create() {
		return _buildMessageStatux();
	}
	
	
	private static NexmoMessageStatus _buildMessageStatux() {
		NexmoMessageStatus  messageStatus  = new NexmoMessageStatus();
		Peer from = new Peer(MessagingService.whatsapp, Phone.create("14157386170"), PeerID.valueOf("66565665"));
		messageStatus.setFrom(from);
		
		Peer to =  new Peer(MessagingService.whatsapp, Phone.create("34616178858"),PeerID.valueOf("66565665"));
		messageStatus.setTo(to);
		
		messageStatus.setTimeStamp("");
		
		messageStatus.setStatus(MessageStatus.rejected);
		
		MessageError error = new MessageError();
		error.setCode(110);
		error.setReason("Unknown recipient");
		messageStatus.setError(error);
		
		MessageUsage usage = new MessageUsage();
		usage.setCurrency(Currency.EUR);
		usage.setPrice("0.33");
		messageStatus.setUsage(usage);
		
		messageStatus.setClientRef("my-personal-reference");
	
		return messageStatus;		
	}
	
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unused")
	public static void main(final String[] argv) {
	    String json = MarshallerBuilder.findTypesToMarshallAt(R01FAppCodes.APP_CODE)
                                     	.build()
                                     .forWriting()
								     .toJson(new NexmoMessageStatusFactoryTest().create());
	    
	    System.out.println(json);
	    NexmoMessageStatus  ms = MarshallerBuilder.findTypesToMarshallAt(R01FAppCodes.APP_CODE)
										             	.build()
										             .forReading()
										             .fromJson(json, NexmoMessageStatus.class);
	    
		   
		
	}

}
