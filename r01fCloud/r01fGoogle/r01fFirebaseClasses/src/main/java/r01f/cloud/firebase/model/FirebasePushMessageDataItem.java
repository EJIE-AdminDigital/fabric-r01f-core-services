package r01f.cloud.firebase.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.cloud.firebase.model.FirebaseIds.FirebasePushMessageDataItemID;

@Accessors(prefix="_")
@RequiredArgsConstructor
public class FirebasePushMessageDataItem {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final FirebasePushMessageDataItemID _id;
    @Getter private final String _value;
}
