package r01f.messaging.http.test.events.bridge.spring.app;



import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import lombok.extern.slf4j.Slf4j;
import r01f.cloud.aws.s3.bootstrap.spring.AWSS3SpringBootstrapConfiguration;

@Slf4j
@SpringBootApplication    // http://localhost:8090/api/v1/webhooks/s3/bridge/hi
public class Z99AWSS3EventBridgeWebhookControllerListenerTestApplication {

    public static void main(final String[] args) throws InterruptedException {      
        SpringApplication app = new SpringApplication(Z99AWSS3EventBridgeWebhookControllerListenerTestApplication.class);
              
        app.setDefaultProperties(java.util.Collections.singletonMap("server.port", "8090"));

        ApplicationContext springContext = app.run(args);

        // Simulate an event...
        Z99AWSS3EventBridgeWebhookControllerListenerTestApplication.processUsing(springContext);  
        
        Thread.currentThread().join();
    }

    public static void processUsing(final ApplicationContext context) { 
        AWSS3SpringBootstrapConfiguration config = context.getBean(AWSS3SpringBootstrapConfiguration.class);
        log.warn(config.debugInfo().toString());

        new Thread(() -> {
	            try {
	                
	                Thread.sleep(1500);
	                
	                log.warn(">>> SIMULATING EVENTBRIDGE EVENT POST TO LOCAL WEBHOOK <<<");
	                
	                // Read sample event brige event  classpath
	                String eventBridgePayload = readResourceAsString("eventbridge/eventbridge-s3-event.json");
	
	                HttpClient client = HttpClient.newBuilder()
	                								.connectTimeout(Duration.ofSeconds(5))
	                							  .build();
	
	                HttpRequest request = HttpRequest.newBuilder()
								                        .uri(URI.create("http://localhost:8090/api/v1/webhooks/s3/bridge"))
								                        .header("Content-Type", "application/json")
								                        .POST(HttpRequest.BodyPublishers.ofString(eventBridgePayload))
							                     .build();
	
	                HttpResponse<String> response = client.send(request, 
	                											HttpResponse.BodyHandlers.ofString());
	
	                log.info(">>> SIMULATION RESPONSE STATUS: {} - BODY: {} <<<",
			                		response.statusCode(),
			                		response.body());

            } catch (final Throwable  ex) {   
            	ex.printStackTrace();
                log.error("Error sending simulated EventBridge event from external JSON file", ex);
            }
        }).start();
    }

    /**
     * Helper method to load classpath files as UTF-8 Strings.
     */
    private static String readResourceAsString(final String resourcePath) throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("Resource file not found on classpath: " + resourcePath);
            }
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}

