package r01f.messaging.http.test.events.spring.app;



import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import lombok.extern.slf4j.Slf4j;
import r01f.cloud.aws.s3.bootstrap.spring.AWSS3SpringBootstrapConfiguration;



@Slf4j
@SpringBootApplication // 
public class Z99AWSS3EventWebhookControllerListenerTestApplication {

/////////////////////////////////////////////////////////////////////////////////////////
// REQUIRED BEAN FOR TEST
/////////////////////////////////////////////////////////////////////////////////////////
	public static void main(final String[] args) throws InterruptedException {      
	    // [Start at : http://localhost:8089/api/v1/webhooks/s3 ]
	    //  Set at minio as a webhook.
	    // [ Is it on line ? : http://localhost:8089/api/v1/webhooks/s3/hi ] 
	    // Important!!  !!
	    // No subscribirse al evento GET (sólo al put !) ...pq entramos en un bucle melancólico.... :_)

	    SpringApplication app = new SpringApplication(Z99AWSS3EventWebhookControllerListenerTestApplication.class);
	    
	    // Forzado del puerto server.port a 8089 programáticamente
	    app.setDefaultProperties(java.util.Collections.singletonMap("server.port", "8089"));

	    ApplicationContext springContext = app.run(args);

	    // ...just a sample of crud ops.
	    Z99AWSS3EventWebhookControllerListenerTestApplication.processUsing(springContext);  
	    
	    Thread.currentThread().join();
	}

	
	
	public static void processUsing(final ApplicationContext context) { 
		AWSS3SpringBootstrapConfiguration config = context.getBean(AWSS3SpringBootstrapConfiguration.class);
		log.warn(config.debugInfo().toString());
	}
	   
}


