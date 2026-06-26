package r01f.cloud.nexmo.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.model.ModelObject;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallType;

@MarshallType(as="error")
@ConvertToDirtyStateTrackable			// changes in state are tracked
@Accessors(prefix="_")
@NoArgsConstructor
public class MessageError 
  implements ModelObject {

	private static final long serialVersionUID = 6598175323810427650L;

	@MarshallField(as="code")
	@Getter @Setter
	private int _code;
	
	@MarshallField(as="reason")
	@Getter @Setter
	private String _reason;
}
