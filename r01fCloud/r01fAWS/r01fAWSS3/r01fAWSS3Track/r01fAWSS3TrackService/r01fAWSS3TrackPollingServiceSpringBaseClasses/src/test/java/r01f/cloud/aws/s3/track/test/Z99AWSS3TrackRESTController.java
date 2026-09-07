package r01f.cloud.aws.s3.track.test;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.inject.Inject;


@RestController
@RequestMapping("/api/s3-metadata-applicant-sync")
public class Z99AWSS3TrackRESTController {

    private final Z99AWSS3MetadataTrackPollingService _pollingService;

    @Inject
    public Z99AWSS3TrackRESTController(Z99AWSS3MetadataTrackPollingService pollingService) {
        _pollingService = pollingService;
    }

    @GetMapping("/trigger")
    public ResponseEntity<String> forceSync() {
        // Lanza el barrido de metadatos bajo demanda vía HTTP POST
        _pollingService.trackMetadata();
        return ResponseEntity.ok("Metadata sweep triggered successfully");
    }
}