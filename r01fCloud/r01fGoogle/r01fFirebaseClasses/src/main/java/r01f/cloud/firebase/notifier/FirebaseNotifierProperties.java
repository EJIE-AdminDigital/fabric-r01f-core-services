package r01f.cloud.firebase.notifier;

import java.util.Properties;

import r01f.cloud.firebase.service.FirebaseConfig;
import r01f.core.services.notifier.properties.NotifierImpPropertiesForPush;
import r01f.securitycontext.SecurityIDS.SecurityToken;
import r01f.types.Path;

public record FirebaseNotifierProperties( Path firebaseCredentialsPath, 
		                                  SecurityToken secToken) 
		implements NotifierImpPropertiesForPush {
////////////////////////////////////////////////////
/// CREATE
/////////////////////////////////////////////////////
	public static FirebaseNotifierProperties create(final Properties props) {
		
		return new FirebaseNotifierProperties (Path.from((String)props.getOrDefault("firebaseCredentialsPath", "<unknown-credentials-path>")),
				                               props.get("securityToken") == null? null : SecurityToken.from((String)props.get("securityToken"))
				                               );
	}
	
	public static FirebaseConfig serviceAPIDataFrom(final FirebaseNotifierProperties props) {		
		Path firebaseCredendtiaslPath = props.firebaseCredentialsPath();
		FirebaseConfig outCfg  =  FirebaseConfig.createFrom(firebaseCredendtiaslPath, props.secToken());
		return outCfg;
	}
}