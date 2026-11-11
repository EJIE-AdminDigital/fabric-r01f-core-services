package r01f.cloud.aws.s3.track.spring.service.polling;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Year;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import r01f.cloud.aws.s3.model.AWSS3BucketID;
import r01f.cloud.aws.s3.model.AWSS3FolderPath;
import r01f.cloud.aws.s3.model.AWSS3ObjectKey;
import r01f.cloud.aws.s3.track.client.api.AWSS3ClientAPIProviderForTrack;
import r01f.cloud.aws.s3.track.model.AWSS3ObjectTrackSummary;
import r01f.types.Path;
import r01f.types.datetime.DayOfMonth;
import r01f.types.datetime.MonthOfYear;

@Slf4j
@Service
public abstract class AWSS3MetadataTrackPollingServiceBase {

 //////////////////////////////////////////////////////
 /// FIELDS
    //////////////////////////////////////////////////////
    protected final AWSS3ClientAPIProviderForTrack _apiProviderForTrack; 
    protected final AWSS3MetadataTrackPollingConfig _config;
 //////////////////////////////////////////////////////
 /// CONSTRUCTORS
  //////////////////////////////////////////////////////
    public AWSS3MetadataTrackPollingServiceBase(final AWSS3MetadataTrackPollingConfig config,
                                                final AWSS3ClientAPIProviderForTrack apiProviderForTrack) {
        _config = config;
        _apiProviderForTrack = apiProviderForTrack;
    }
 //////////////////////////////////////////////////////
 /// METHODS
 ////////////////////////////////////////////////////// 
 
    /**
     * Periodically sweeps configured S3 paths to detect new or updated objects 
     * within a rolling time window.
     */
    @Scheduled(fixedDelayString = "#{@s3MetadataTrackPollingConfig.getPollingFixedDelayMs()}")
    public void trackMetadata() {
        try {
            AWSS3BucketID bucket = _config.getBucket();
            
            // 1. Define time window (15-minute sliding window)
            ZoneId madridZone = ZoneId.of("Europe/Madrid");
            ZonedDateTime now = ZonedDateTime.now(madridZone);
            
           
            ZonedDateTime windowStart = now.minus(_config.getLastModifiedLookbackMinutes(), 
            		                              ChronoUnit.MINUTES);
            Instant windowThreshold = windowStart.toInstant();
            
            log.warn("=== [AWSS3MetadataTrackPollingService] Starting configured sweep on bucket: '{}' ===", 
                     _config.getBucket());

            List<AWSS3FolderPath> pathsToScan = _config.getDataPaths();
            if (pathsToScan == null || pathsToScan.isEmpty()) {
                log.warn("[AWSS3MetadataTrackPollingService] No data-paths have been configured in properties.");
                return;
            }

            // 2. Iterate over each configured data type path
            for (final AWSS3FolderPath dataType : pathsToScan) {
                
                // 3. Resolve unique date-partitioned paths covered by the time window (handles midnight boundary)
                Set<AWSS3FolderPath> folderPathsToScan = _resolveDatePathsForWindow(dataType, 
                																	windowStart,
                																	now);

                for (AWSS3FolderPath folderPath : folderPathsToScan) {
                    log.warn("[AWSS3MetadataTrackPollingService] Scanning for changes in {} since {}", 
                             folderPath.asString(), windowThreshold);

                    // 4. Query S3/MinIO for modified objects in the target path
                    Collection<AWSS3ObjectTrackSummary> changedObjects = 
                            _apiProviderForTrack.forBucket(bucket)
                                                .getForTrack()
                                                .trackModifiedSince(bucket,
                                                                    folderPath,
                                                                    windowThreshold);

                    // 5. Process detected objects
                    if (changedObjects != null && !changedObjects.isEmpty()) {
                        log.warn("[AWSS3MetadataTrackPollingService] Detected {} modified or new objects in path '{}' !", 
                                 changedObjects.size(), folderPath.asString());                    
                        this.processDiscoveredObjects(changedObjects);
                    }
                }
            }            
            log.warn("=== [AWSS3MetadataTrackPollingService] Metadata sweep completed successfully ==");
        } catch (final Throwable e) {
            log.error("Critical error executing metadata polling sweep via Filer/ClientAPI", e);
        }
    }
    

    /**
     * Processes discovered S3 objects, verifying idempotency against S3 execution markers (.done).
     *
     * @param objects Collection of discovered object summaries from S3
     */
    protected void processDiscoveredObjects(final Collection<AWSS3ObjectTrackSummary> objects) {
        AWSS3BucketID bucket = _config.getBucket();

        for (final AWSS3ObjectTrackSummary objTrackSummary : objects) {
            AWSS3ObjectKey objectKey = objTrackSummary.getKey();
            
            // Skip execution marker objects if returned by S3 sweep
            if (objectKey.asString().startsWith(".control/")) {
                continue;
            }

            // 1. Build a unique marker key incorporating ObjectKey + LastModified timestamp
            long lastModifiedMillis = objTrackSummary.getLastModified().toEpochMilli();
            AWSS3ObjectKey markerKey = _buildMarkerPath(objectKey, lastModifiedMillis);

            // 2. Check if the event marker already exists in S3
            boolean alreadyProcessed = _apiProviderForTrack.forBucket(bucket)
                                                           .getForTrack()
                                                           .existTrackMarker(bucket,
                                                        		             markerKey);

            if (alreadyProcessed) {
                log.warn("[AWSS3MetadataTrackPollingService] Key {} already marked as processed in S3. Skipping.", objectKey.asString());
                continue;
            }

            log.info("[AWSS3MetadataTrackPollingService] Reconciliation triggered for NEW event: {}", objectKey.asString());

            // 3. Dispatch reconciliation event to Kafka / Webhook / EventBus
            boolean success = dispatchProcessedEvents(objTrackSummary);

            // 4. Create completion marker in S3 only if event dispatch succeeded
            if (success) {
                _apiProviderForTrack.forBucket(bucket)
                                    .getForTrack()
                                    .createTrackMarker(bucket, markerKey);
                log.info("[AWSS3MetadataTrackPollingService] Created execution marker at: {}", markerKey.asString());
            }
        }
    }

    /**
     * Extension point to handle DISTPACH events (e.g., publishing to Kafka).
     *
     * @param objTrackSummary The summary of the modified object
     * @return true if the event was successfully dispatched, false otherwise
     */
    protected boolean dispatchProcessedEvents(final AWSS3ObjectTrackSummary... objTrackSummary) {
        // Concrete implementation provided by subclasses
        return true; 
    }
//////////////////////////////////////////////////////
/// PRIVATE METHODS
////////////////////////////////////////////////////// 
    
    /**
     * Constructs the unique control marker path in S3 for a given object key and timestamp.
     */
    private AWSS3ObjectKey _buildMarkerPath(final AWSS3ObjectKey originalKey, final long lastModifiedMillis) {
        String uniqueMarkerKey = String.format(".control/%s_%d.done", originalKey.asString(), lastModifiedMillis);
        return AWSS3ObjectKey.fromString(uniqueMarkerKey);
    }

    /**
     * Resolves all S3 date path partitions (YYYY/MM/DD) within the specified time window.
     * Iterates day-by-day from the start date to the end date to ensure no intermediate
     * days are skipped during the sweep.
     *
     * @param dataType base folder path
     * @param start start timestamp of the window
     * @param end end timestamp of the window
     * @return set of unique folder paths corresponding to each day in the range
     */
    private Set<AWSS3FolderPath> _resolveDatePathsForWindow(final AWSS3FolderPath dataType, 
                                                            final ZonedDateTime start,    final ZonedDateTime end) {
        Set<AWSS3FolderPath> paths = new LinkedHashSet<>();
        
        // Extract LocalDate components to iterate strictly over calendar days, ignoring time parts
        LocalDate startDate = start.toLocalDate();
        LocalDate endDate = end.toLocalDate();

        // Iterate day-by-day from start to end (inclusive)
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            // Reattach timezone context for path construction
            ZonedDateTime dateTimeForPath = currentDate.atStartOfDay(start.getZone());
            
            paths.add(_buildDatePath(dataType, dateTimeForPath));
            
            // Advance to the next calendar day
            currentDate = currentDate.plusDays(1);
        }
        
        return paths;
    }

    /**
     * Builds a full S3 folder path incorporating YYYY/MM/DD partitions.
     */
    private AWSS3FolderPath _buildDatePath(final AWSS3FolderPath dataType, final ZonedDateTime dateTime) {
        String year = String.valueOf(dateTime.getYear());
        String month = String.format("%02d", dateTime.getMonthValue());
        String day = String.format("%02d", dateTime.getDayOfMonth());

        Path targetPath = Path.from(dataType.asString())
                              .joinedWith(year)
                              .joinedWith(month)
                              .joinedWith(day);

        return AWSS3FolderPath.fromString(targetPath.asRelativeString());
    }

   
}