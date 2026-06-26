package r01f.cloud.nexmo.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.cloud.nexmo.NexmoAPI.MessagingService;
import r01f.cloud.nexmo.model.NexmoIDS.PeerID;
import r01f.model.ModelObject;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallFrom;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.contact.Phone;

@MarshallType(as="peer")
@ConvertToDirtyStateTrackable			// changes in state are tracked
@Accessors(prefix="_")
@NoArgsConstructor
public class Peer
     implements ModelObject {

	private static final long serialVersionUID = 3848509510590725933L;

/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="type")
	@Setter @Getter private    MessagingService _type;

	@MarshallField(as="number")
	@Setter @Getter private   Phone _number;
	
	@MarshallField(as="id")
	@Setter @Getter private   PeerID _id;
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	public Peer(final MessagingService type, final Phone number ) {
		this(type,number,null);
	}
	public Peer(@MarshallFrom("type") final MessagingService type,
			    @MarshallFrom("number") final Phone phone,
			    @MarshallFrom("id") final PeerID id) {
			                               
		_number = phone;	
		_type = type;
		_id = id;
		
	}

}
