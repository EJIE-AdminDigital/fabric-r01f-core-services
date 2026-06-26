package r01f.cloud.nexmo.api.interfaces.impl;

import com.google.common.base.Preconditions;
import com.nexmo.client.NexmoClient;
import com.nexmo.client.voice.Call;
import com.nexmo.client.voice.CallEvent;

import r01f.cloud.nexmo.NexmoAPI.NexmoAPIData;
import r01f.cloud.nexmo.api.interfaces.NexmoServicesForVoice;
import r01f.objectstreamer.Marshaller;
import r01f.types.contact.Phone;
import r01f.types.url.Url;

public class NexmoServicesForVoiceImpl 
			extends NexmoServicesBaseImpl 
	implements NexmoServicesForVoice {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
	public NexmoServicesForVoiceImpl(final NexmoAPIData apiData,
									 final NexmoClient nexmoClient,
									 final Marshaller marshaller) {
		super(apiData,
			  nexmoClient,
			  marshaller);	
	}
	public NexmoServicesForVoiceImpl(final NexmoAPIData apiData,
									final NexmoClient nexmoClient) {
		super(apiData,
			  nexmoClient,
			  null);	
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Makes a nexmo outgoing phone call
	 * @param toPhone the target phone number
	 * @param twmlUrl the twml to execute (ie ""https://nexmo-community.github.io/ncco-examples/first_call_talk.json"")
	 * @return
	 */
	@Override
	public CallEvent makeCall(final Phone toPhone,
							  final Url nexmoUrl)  {
		Preconditions.checkArgument(toPhone != null,"The destination phone must NOT be null!");
		Preconditions.checkArgument(nexmoUrl != null,"A nexmo URL is needed!");
		Call call = new Call(toPhone.asString(), _apiData.getVoicePhone().asString(),nexmoUrl.asString());
		CallEvent event = _nexmoClient.getVoiceClient().createCall(call);
		return event;
	}
}
