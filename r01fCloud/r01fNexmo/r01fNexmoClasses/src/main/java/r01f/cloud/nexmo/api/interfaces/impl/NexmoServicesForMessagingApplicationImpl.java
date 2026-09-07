package r01f.cloud.nexmo.api.interfaces.impl;

import java.io.IOException;

import com.nexmo.client.NexmoClient;

import lombok.extern.slf4j.Slf4j;
import r01f.cloud.nexmo.NexmoAPI.NexmoAPIData;
import r01f.cloud.nexmo.api.interfaces.NexmoServicesForMessagingApplication;
import r01f.cloud.nexmo.model.Message;
import r01f.cloud.nexmo.model.NexmoIDS.NexmoMessageUUID;
import r01f.cloud.nexmo.model.Peer;
import r01f.cloud.nexmo.model.outbound.NexmoOutboundMessage;
import r01f.httpclient.HttpHeader;
import r01f.httpclient.HttpResponseResult;
import r01f.io.util.StringPersistenceUtils;
import r01f.mime.MimeTypes;
import r01f.objectstreamer.Marshaller;
import r01f.services.client.servicesproxy.rest.DelegateForRawREST;
import r01f.types.contact.Phone;
import r01f.types.url.Url;
import r01f.util.types.Strings;

@Slf4j
public class NexmoServicesForMessagingApplicationImpl
	 extends NexmoServicesBaseImpl
  implements NexmoServicesForMessagingApplication {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public NexmoServicesForMessagingApplicationImpl(final NexmoAPIData apiData,
			                                        final NexmoClient nexmoClient,
			                                        final Marshaller marshaller) {
		super(apiData,
			  nexmoClient,
			  marshaller);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	SEND
/////////////////////////////////////////////////////////////////////////////////////////	
	public void send(final Phone toPhone,final String text) {
		/*NexmoOutboundMessage outboudMesagge = new NexmoOutboundMessage();
		Message message = new Message();
		TextMessageContent _content = new TextMessageContent(text);
		message.setContent(_content);
		send(outboudMesagge);*/
		throw new UnsupportedOperationException(" Not implemented. Use : public MessageUUID send( final NexmoOutboundMessage out ) ");

	}
	@Override
	public NexmoOutboundMessage send(final Peer to, final Message message) {
	   return _doSend(to, message);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	private  NexmoOutboundMessage _doSend(final Peer to,final Message message) {
		NexmoOutboundMessage out = new NexmoOutboundMessage();
		Peer from = new Peer(_apiData.getMessagingService(),_apiData.getMessagingPhone());
		out.setFrom(from);
		out.setTo(to);
		out.setMessage(message);
		// do the http call
		Url restResourceUrl = _apiData.getRestResouceURIForMessagingApplicationImpl();
		log.warn(" Create REST Resource Url {}",restResourceUrl);
	    // 0. Marshall.
		String entityAsStringJson = _marshaller.forWriting()
											   .toJson(out);
		// 1. Generare JWT based on Nexmo Client.
		String jwtAsString = _nexmoClient.generateJwt();
	    log.warn(" \n \n Generated JWT  {} \n", jwtAsString);
		// 2. POST.
		HttpResponseResult httpResponse = DelegateForRawREST.POST(restResourceUrl, 									                  // REST Resource URI
					                                              MimeTypes.APPLICATION_JSON,                                         // Mime Type JSON
											  					  new HttpHeader("Authorization","Bearer " + jwtAsString),     		  // JWT bearer
											  					  entityAsStringJson,                              					  // Posted JSON as String
											  					  new HttpHeader("accept",MimeTypes.APPLICATION_JSON.asString()));    // Accept Header, to obtain CRUDResult based response ( otherwise model object will be returned)


	    //Parse Result
		String response = null;
		if (httpResponse.isSuccess()) {
			// Parse 200 OK result JSON to MessageUUID //{"message_uuid":"e670a362-568a-4895-a93e-76ffb78c21f1"}
			 try {
				response = StringPersistenceUtils.load(httpResponse.loadAsStream());
			 } catch ( final IOException e) {
				e.printStackTrace();
			 }
			 log.warn( "json response {}",response);
			 NexmoMessageUUID uuid = _marshaller.forReading()
			                               .fromJson(response, NexmoMessageUUID.class);
			 log.warn(" post message uuid {}",uuid.asString());
			 out.setUuid(uuid);
			 return out;
		}
		throw new IllegalStateException( Strings.customized(" error posting  outbound message {}",response));
	}
}
