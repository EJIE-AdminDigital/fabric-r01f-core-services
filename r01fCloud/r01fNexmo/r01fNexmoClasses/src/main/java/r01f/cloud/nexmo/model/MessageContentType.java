package r01f.cloud.nexmo.model;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Accessors(prefix="_")
@RequiredArgsConstructor
public enum MessageContentType {	
	text,  // lowcase must be
	image,
	location,
	audio
;
/////////////////////////////////////////////////////////////////////////////////////////
//	FROM OID & ID TYPES
/////////////////////////////////////////////////////////////////////////////////////////

}
