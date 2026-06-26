package r01f.cloud.aws.s3.migration.service.interfaces.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Objects;

import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.cloud.aws.s3.migration.service.interfaces.MigrationAuditService;
import r01f.filestore.api.FileStoreAPI;
import r01f.filestore.api.FileStoreFilerAPI;

@Slf4j
@Accessors(prefix="_")
abstract class MigrationServiceImpl {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
    @SuppressWarnings("unused")
	protected final FileStoreAPI _fileStoreAPI;
    @SuppressWarnings("unused")
	protected final FileStoreFilerAPI _fileStoreFilerAPI;
    @SuppressWarnings("unused")
	protected final MigrationAuditService _migrationAuditService;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
    public MigrationServiceImpl(FileStoreAPI fileStoreAPI, FileStoreFilerAPI fileStoreFilerAPI, MigrationAuditService migrationAuditService) {
        this._fileStoreAPI = Objects.requireNonNull(fileStoreAPI, "fileStoreAPI must not be null");
        this._fileStoreFilerAPI = Objects.requireNonNull(fileStoreFilerAPI, "fileStoreFilerAPI must not be null");
        this._migrationAuditService = Objects.requireNonNull(migrationAuditService, "resultLogger must not be null");
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////    
    /**
     * Converts a stack trace to a single-line String, replacing line breaks.
     *
     * @param throwable exception to stringify
     * @return single-line representation of the stack trace
     */
    protected String _getStackTraceAsSingleLine(Throwable throwable) {
        StringWriter sw = new StringWriter();
        try (PrintWriter pw = new PrintWriter(sw)) {
            throwable.printStackTrace(pw);
        }
        String raw = sw.toString();
        return raw.replace("\r\n", " | ")
                  .replace("\n", " | ")
                  .replace("\r", " | ");
    }
}