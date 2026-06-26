package r01f.cloud.nexmo.api.interfaces;

import com.nexmo.client.voice.CallEvent;

import r01f.types.contact.Phone;
import r01f.types.url.Url;

public interface NexmoServicesForVoice {
	
	public CallEvent makeCall(final Phone toPhone,
		                      final Url nexmoUrl); 

}
