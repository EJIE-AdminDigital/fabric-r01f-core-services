package r01f.cloud.nexmo.model.outbound;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.cloud.nexmo.model.Message;
import r01f.cloud.nexmo.model.NexmoIDS.MessageUUID;
import r01f.cloud.nexmo.model.Peer;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallType;


/*
 * See https://developer.nexmo.com/messages/code-snippets/whatsapp/send-text 
 * 
 * {
    "from": { "type": "whatsapp", "number": "'$WHATSAPP_NUMBER'" },
    "to": { "type": "whatsapp", "number": "'$TO_NUMBER'" },
    "message": {
      "content": {
        "type": "text",
        "text": "This is a WhatsApp Message sent from the Messages API"
      }
    }
  }'
 *
 */

@MarshallType(as="outboudMessage")
@ConvertToDirtyStateTrackable			// changes in state are tracked
@Accessors(prefix="_")
@NoArgsConstructor
public class NexmoOutboundMessage {
	
	@MarshallField(as="uuid")
	@Getter @Setter private MessageUUID _uuid;
	
	@MarshallField(as="from")
	@Getter @Setter private Peer _from;
	
	@MarshallField(as="to")
	@Getter @Setter private Peer _to;
	
	@MarshallField(as="message")
	@Getter @Setter private Message  _message;
	
}
