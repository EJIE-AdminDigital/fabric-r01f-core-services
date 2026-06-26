package r01f.cloud.nexmo.api.interfaces;

import com.nexmo.client.sms.SmsSubmissionResponse;

import r01f.types.contact.Phone;

public interface NexmoServicesForSMS {
	
	public SmsSubmissionResponse sendSMS(final Phone toPhone,
                                         final String text); 

}
