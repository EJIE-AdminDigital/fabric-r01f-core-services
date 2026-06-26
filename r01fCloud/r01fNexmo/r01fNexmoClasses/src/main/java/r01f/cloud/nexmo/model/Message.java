package r01f.cloud.nexmo.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallField;
@Accessors(prefix="_")
public class Message {
	
	@MarshallField(as="content")
	@Getter @Setter private MessageContent  _content;
	
	public Message() {		
	}
	public Message( final MessageContent  content) {	
		_content = content;
	}
}
