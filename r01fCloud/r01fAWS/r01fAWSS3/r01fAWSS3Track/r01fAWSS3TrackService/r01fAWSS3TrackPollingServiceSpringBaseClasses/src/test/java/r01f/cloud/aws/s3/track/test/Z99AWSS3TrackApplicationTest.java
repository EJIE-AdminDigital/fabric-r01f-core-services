package r01f.cloud.aws.s3.track.test;



import java.time.Instant;
import java.time.Year;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

import lombok.extern.slf4j.Slf4j;
import r01f.cloud.aws.s3.model.AWSS3BucketID;
import r01f.cloud.aws.s3.model.AWSS3FolderPath;
import r01f.cloud.aws.s3.track.client.api.AWSS3ClientAPIForTrack;
import r01f.cloud.aws.s3.track.model.AWSS3ObjectTrackSummary;
import r01f.cloud.aws.s3.track.spring.service.polling.AWSS3MetadataTrackPollingConfig;
import r01f.types.Path;
import r01f.types.datetime.DayOfMonth;
import r01f.types.datetime.MonthOfYear;

@Slf4j
@EnableScheduling //
@SpringBootApplication // 
public class Z99AWSS3TrackApplicationTest {

	 // UTC Formatter for date-based folder segregation: e.g., 2026/07/05/
   /* private static final DateTimeFormatter DATE_PATH_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd/")
                                                                                    .withZone(ZoneOffset.UTC);*/
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public static void main(final String[] args) throws InterruptedException {       
	    ApplicationContext springContext = SpringApplication.run(Z99AWSS3TrackApplicationTest.class,
	    														 args);
	    // ...just a sample of crud ops.
	    Z99AWSS3TrackApplicationTest.processUsing(springContext);  
	    
	     Thread.currentThread().join();
	}
	public static void processUsing(final ApplicationContext context) {    
	    
	  
	    //trackMetadataChange(trackApi,config);
	    
	 //
	    /*ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
	    
	    log.info("=== Loop trackMetadataChange cada 30 segundos ===");
	    
	    // 3. Programamos la ejecución periódica (0 segundos de espera inicial, 30 segundos de periodo)
	    executor.scheduleAtFixedRate(() -> {
	        try {
	            Z99AWSS3TrackApplicationTest.trackMetadataChange(trackApi, config);
	        } catch (Exception e) {
	            log.error("Error ejecutando el barrido de tracking", e);
	        }
	    }, 0, 30, TimeUnit.SECONDS);

	 
	    try {
	       
	        Thread.sleep(TimeUnit.MINUTES.toMillis(10)); 
	    } catch (InterruptedException e) {
	        Thread.currentThread().interrupt();
	    } finally {
	        
	        executor.shutdown();
	    }*/
	}
	
	
	
	 public static void trackMetadataChange(AWSS3ClientAPIForTrack trackApi,AWSS3MetadataTrackPollingConfig config ) {
        try {
            // 1. Build the Bucket domain object from dynamic configuration
        	AWSS3BucketID bucket = config.getBucket();
            
            // 2. Define the lookback time window (e.g., last 3 minutes)
            Instant windowThreshold = Instant.now().minus(5, ChronoUnit.HOURS);
          

            log.info("=== [AWSS3MetadataTrackPollingService] Starting configured sweep on bucket: '{}' ===", 
            		config.getBucket());

            List<AWSS3FolderPath> pathsToScan = config.getDataPaths();
            if (pathsToScan == null || pathsToScan.isEmpty()) {
                log.warn("[AWSS3MetadataTrackPollingService] No data-paths have been configured in properties.");
                return;
            }

            // 3. Dynamically iterate over each configured path prefix
            for (final AWSS3FolderPath dataType : pathsToScan) {
                
                // Compose the hierarchical layout: /data_type/2026/07/05/
            
                Path  targetPath = Path.from(dataType.asString()).joinedWith(Year.now())
                                               .joinedWith(MonthOfYear.nowAt(ZoneId.of("Europe/Madrid")).asStringPaddedWithZero())
                                               .joinedWith(DayOfMonth.nowAt(ZoneId.of("Europe/Madrid")).asStringPaddedWithZero());
                
                AWSS3FolderPath folderPath = AWSS3FolderPath.fromString(targetPath.asRelativeString());
                
                log.warn("****[AWSS3MetadataTrackPollingService] Scanning for changes in {} since {}", targetPath.asAbsoluteString(), windowThreshold   );

                // 4. Invoke the optimized Filer/Client API tracking method
                Collection<AWSS3ObjectTrackSummary> changedObjects = 
                		trackApi.getForTrack().trackModifiedSince(bucket, 
		                    		                              folderPath, 
		                    		                               windowThreshold);

                // 5. If orphan items are discovered via metadata evaluation, process them for reconciliation
                if (changedObjects != null && !changedObjects.isEmpty()) {
                    log.warn("[AWSS3MetadataTrackPollingService] Detected {} modified or new objects in path '{}' requiring reconciliation!", 
                             changedObjects.size(), dataType);
                    
                  
                }
            }
            
            log.info("=== [AWSS3MetadataTrackPollingService] Metadata sweep completed successfully ===");

        } catch (final Throwable e) {
            log.error("Critical error executing metadata polling sweep via Filer/ClientAPI", e);
        }
    }

}
