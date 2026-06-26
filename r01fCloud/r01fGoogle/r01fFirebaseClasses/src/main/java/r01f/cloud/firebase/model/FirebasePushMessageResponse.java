package r01f.cloud.firebase.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;


@Accessors(prefix="_")
@RequiredArgsConstructor
public class FirebasePushMessageResponse {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final String _message;
}
