package r01f.messaging.rabbitmq;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.securitycontext.SecurityIDS.LoginID;
import r01f.securitycontext.SecurityIDS.Password;
import r01f.types.url.Host;



@Accessors(prefix="_")
public abstract class RabbitMQMessagingServiceConfigBase
	implements RabbitMQMessagingServiceConfig {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter	@Setter	protected Host _host;
	@Getter	@Setter	protected int  _port;
	@Getter	@Setter	protected LoginID   _loginId;
	@Getter	@Setter	protected Password _password;
/////////////////////////////////////////////////////////////////////////////////////////
// METHODS
/////////////////////////////////////////////////////////////////////////////////////////

}
