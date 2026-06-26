package r01f.cloud.nexmo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.objectstreamer.annotations.MarshallType;

@MarshallType(as="content")
@ConvertToDirtyStateTrackable			// changes in state are tracked
@Accessors(prefix="_")
@NoArgsConstructor
public abstract class MessageContentBase 
	implements MessageContent {
/////////////////////////////////////////////////////////////////////////////////////////
//FIELDS
/////////////////////////////////////////////////////////////////////////////////////////	
	@Getter @Setter @JsonIgnore private   MessageContentType _type;
	
	public MessageContentBase(final  MessageContentType type) {
		_type = type;
	}
}
