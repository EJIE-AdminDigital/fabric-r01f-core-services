package r01f.cloud.nexmo.api.interfaces.impl;

import com.google.common.base.Preconditions;
import com.nexmo.client.NexmoClient;
import com.nexmo.client.sms.SmsSubmissionResponse;
import com.nexmo.client.sms.messages.TextMessage;

import r01f.cloud.nexmo.NexmoAPI.NexmoAPIData;
import r01f.cloud.nexmo.api.interfaces.NexmoServicesForSMS;
import r01f.objectstreamer.Marshaller;
import r01f.types.contact.Phone;

public class NexmoServicesForSMSImpl 
	 extends NexmoServicesBaseImpl 
  implements NexmoServicesForSMS {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
	public NexmoServicesForSMSImpl(final NexmoAPIData apiData,
			                       final NexmoClient nexmoClient,
			                       final Marshaller marshaller) {
		super(apiData,
			  nexmoClient,
			  marshaller);	
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Sends a Nexmo SMS
	 * @param toPhone
	 * @param text
	 * @return	
	 */
	@Override
	public SmsSubmissionResponse sendSMS(final Phone toPhone,
						                 final String text)  {
		Preconditions.checkArgument(toPhone != null,"The destination phone must NOT be null!");
		Preconditions.checkArgument(text != null,"A text is needed for the sms message!");
		
	    SmsSubmissionResponse response = _nexmoClient.getSmsClient()
	    											 .submitMessage(new TextMessage(_apiData.getMessagingPhone().asString(),
									                		                   		toPhone.asString(),
									                		                   		text));
		return response;
	}
}
