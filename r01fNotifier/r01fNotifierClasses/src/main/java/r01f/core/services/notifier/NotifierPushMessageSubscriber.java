package r01f.core.services.notifier;

import java.io.Serializable;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.core.services.notifier.NotifierOIDs.NotifierPushTopic;
import r01f.securitycontext.SecurityIDS.SecurityToken;

/***
 * The receptor (or receptors of) a Push Message are identified by a Topic or a token, or both.. *
 */
@Accessors(prefix="_")
@RequiredArgsConstructor
public class NotifierPushMessageSubscriber   
  implements Serializable {

	private static final long serialVersionUID = 300942327630733205L;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////	
	@Getter private NotifierPushTopic  _topic;
	@Getter private SecurityToken  _token;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
	public NotifierPushMessageSubscriber forToken(final SecurityToken token) {
	    _token = token;
		return this;
	}
	public NotifierPushMessageSubscriber forTopic(final NotifierPushTopic topic) {
	    _topic = topic;
		return this;
	}
}