package r01f.messaging.rabbitmq.test.model;

import r01f.messaging.rabbitmq.consumer.RabbitMQConsumerServiceConfig;
import r01f.securitycontext.SecurityIDS.LoginID;
import r01f.securitycontext.SecurityIDS.Password;
import r01f.types.url.Host;

public class RabbitMQConsumerConfigSample
   	implements RabbitMQConsumerServiceConfig  {

		@Override
		public Host getHost() {
			return Host.LOCALHOST_USING_IP;
		}
		@Override
		public int getPort() {
			return 5674;
		}
		@Override
		public Password getPassword(){
			return Password.forId("guest");
		}
		@Override
		public LoginID getLoginId() {
			return LoginID.forId("guest");
		}
}
