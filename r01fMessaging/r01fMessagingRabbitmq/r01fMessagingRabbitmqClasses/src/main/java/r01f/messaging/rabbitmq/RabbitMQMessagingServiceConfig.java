package r01f.messaging.rabbitmq;

import r01f.messaging.MessagingServiceConfig;
import r01f.oids.SecurityIDs.LoginID;
import r01f.oids.SecurityIDs.Password;
import r01f.types.url.Host;

public interface RabbitMQMessagingServiceConfig
		 extends MessagingServiceConfig {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	Host  getHost();
	int   getPort();
	Password getPassword();
	LoginID  getLoginId();
}
