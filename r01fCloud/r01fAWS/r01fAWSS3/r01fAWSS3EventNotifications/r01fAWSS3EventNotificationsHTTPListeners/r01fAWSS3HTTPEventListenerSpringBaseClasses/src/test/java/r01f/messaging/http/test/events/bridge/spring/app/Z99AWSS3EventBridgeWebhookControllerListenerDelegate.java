package r01f.messaging.http.test.events.bridge.spring.app;

import jakarta.inject.Provider;
import lombok.experimental.Accessors;
import r01f.cloud.aws.s3.events.listener.AWSS3ObjectRetrieverAsync;
import r01f.cloud.aws.s3.events.spring.http.delegate.listener.AWSS3EventWebhookWithAsycnRetieverListenerDelegateBase;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;


@Accessors(prefix="_")
public class Z99AWSS3EventBridgeWebhookControllerListenerDelegate 
		extends AWSS3EventWebhookWithAsycnRetieverListenerDelegateBase {
/////////////////////////////////////////////////////////////////////////////////////////
// 	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public Z99AWSS3EventBridgeWebhookControllerListenerDelegate(final Provider<SecurityContext> securityContextProvider,
																final Marshaller marshaller,
																final AWSS3ObjectRetrieverAsync objectRetrieverAsync) {
		super(securityContextProvider,
			  marshaller,
			  objectRetrieverAsync);
	}
}