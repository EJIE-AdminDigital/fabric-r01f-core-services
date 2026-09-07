package r01f.messaging.kafka.test.spring;



import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import lombok.extern.slf4j.Slf4j;
import r01f.cloud.aws.s3.bootstrap.spring.AWSS3SpringBootstrapConfiguration;

@Slf4j
@SpringBootApplication
public class Z99AWSS3KafkaEventListenerTestApplication implements CommandLineRunner {

    public static void main(String[] args) throws InterruptedException {
       
        SpringApplication app = new SpringApplication(Z99AWSS3KafkaEventListenerTestApplication.class);
        app.setWebApplicationType(org.springframework.boot.WebApplicationType.NONE);
        ApplicationContext springContext =   app.run(args);
        
        AWSS3SpringBootstrapConfiguration config = springContext.getBean(AWSS3SpringBootstrapConfiguration.class);
		log.warn(config.debugInfo().toString());
		Thread.currentThread().join();
    }

    @Override
    public void run(String... args) throws Exception {
        log.warn("====================================================");
        log.warn("   Spring Kafka Listener Live Active");
        log.warn("====================================================");       
        // 
       
    }

/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////

}