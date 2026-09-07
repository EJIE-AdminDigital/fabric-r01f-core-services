package r01f.cloud.nexmo.model.status;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.cloud.nexmo.model.MessageError;
import r01f.cloud.nexmo.model.MessageStatus;
import r01f.cloud.nexmo.model.MessageUsage;
import r01f.cloud.nexmo.model.NexmoIDS.NexmoMessageUUID;
import r01f.cloud.nexmo.model.Peer;
import r01f.model.ModelObject;
import r01f.objectstreamer.annotations.MarshallField;

//https://developer.nexmo.com/api/messages-olympus#message-status
/**
 * {
 *		  "message_uuid": "aaaaaaaa-bbbb-cccc-dddd-0123456789ab",
 *		  "to": {
 *		    "type": "sms",
 *		    "id": "0123456789012345",
 *		    "number": "447700900000"
 *		  },
 *		  "from": {
 *		    "type": "sms",
 *		    "id": "0123456789012345",
 *		    "number": "447700900000"
 *		  },
 *		  "timestamp": "2020-01-01T14:00:00.000Z",
 *		  "status": "delivered",
 *		  "error": {
 *		    "code": 1300,
 *		    "reason": "Not part of the provider network"
 *		  },
 *		  "usage": {
 *		    "currency": "EUR",
 *		    "price": "0.0333"
 *		  },
 *		  "client_ref": "my-personal-reference"
 *		}
 *
 */
@Accessors(prefix="_")
public class NexmoMessageStatus 
  implements ModelObject {

	private static final long serialVersionUID = -8263584933706907252L;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="message_uuid")
	@Getter @Setter private NexmoMessageUUID _uuid;
	
	@MarshallField(as="to")
	@Getter @Setter private Peer _to;
	
	@MarshallField(as="from")
	@Getter @Setter private Peer _from;
	
	@MarshallField(as="timestamp")
	@Getter @Setter private String _timeStamp;
	
	@MarshallField(as="status")
	@Getter @Setter private MessageStatus _status;
	
	@MarshallField(as="error")
	@Getter @Setter private MessageError _error;
	
	@MarshallField(as="usage")
	@Getter @Setter private MessageUsage _usage;
	
	@MarshallField(as="client_ref")
	@Getter @Setter private String _clientRef;
}
