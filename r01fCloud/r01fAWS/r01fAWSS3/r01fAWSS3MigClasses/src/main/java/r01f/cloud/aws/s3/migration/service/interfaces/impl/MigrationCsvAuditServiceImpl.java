package r01f.cloud.aws.s3.migration.service.interfaces.impl;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import lombok.extern.slf4j.Slf4j;
import r01f.cloud.aws.s3.migration.model.AuditModel;
import r01f.cloud.aws.s3.migration.service.interfaces.MigrationAuditService;

/**
 * Utility class that logs migration results to CSV files.
 * <p>
 * One CSV contains successful uploads and the other contains errors.
 * Each instance owns its writers and should be closed when no longer needed.
 */
@Slf4j
public class MigrationCsvAuditServiceImpl implements MigrationAuditService, Closeable  {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private static final byte[] UTF8_BOM = new byte[] {
            (byte) 0xEF, (byte) 0xBB, (byte) 0xBF
    };
    private final BufferedWriter successWriter;
    private final BufferedWriter errorWriter;
    private final BufferedWriter warningWriter;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
    /**
     * Creates a new {@link CsvResultLogger} with the given CSV paths.
     * Existing files will be truncated.
     *
     * @param successCsvPath path to the CSV file where successful results are stored
     * @param errorCsvPath   path to the CSV file where error results are stored
     * @param warningCsvPath path to the CSV file where warning results are stored
     * @throws IOException if an I/O error occurs while opening or creating the files
     */
    public MigrationCsvAuditServiceImpl(Path successCsvPath, Path errorCsvPath, Path warningCsvPath) throws IOException {
        this.successWriter = _newUtf8CsvWriterWithBom(successCsvPath);
        this.errorWriter = _newUtf8CsvWriterWithBom(errorCsvPath);
        this.warningWriter = _newUtf8CsvWriterWithBom(warningCsvPath);
        writeHeaders();
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
    @Override
	public synchronized void auditSuccess(AuditModel audit) {
        try {
        	String line = "";
        	if(audit.getSrcPath() != null) {
        		line = _csvEscape(audit.getMigrationOid()) + "," + _csvEscape(audit.getSrcPath()) + "," + _csvEscape(audit.getLocalPath());
        	} else {
        		line = _csvEscape(audit.getMigrationOid()) + "," + _csvEscape(audit.getLocalPath()) + "," + _csvEscape(audit.getDstPath());
        	}
            successWriter.write(line);
            successWriter.newLine();
            successWriter.flush();
        } catch (IOException e) {
            log.error("Failed to write success entry to CSV: " + e.getMessage());
        }
    }
    @Override
	public synchronized void auditError(AuditModel audit) {
        try {
        	String line = "";
        	if(audit.getSrcPath() != null) {
        		line = _csvEscape(audit.getMigrationOid()) + "," + _csvEscape(audit.getSrcPath()) + "," + _csvEscape(audit.getLocalPath());
        	} else {
        		line = _csvEscape(audit.getMigrationOid()) + "," + _csvEscape(audit.getLocalPath()) + "," + _csvEscape(audit.getDstPath());
        	}
            line += "," + _csvEscape(audit.getError());
            errorWriter.write(line);
            errorWriter.newLine();
            errorWriter.flush();
        } catch (IOException e) {
            log.error("Failed to write error entry to CSV: " + e.getMessage());
        }
    }
    @Override
	public synchronized void auditWarning(AuditModel audit) {
        try {
        	String line = "";
        	if(audit.getSrcPath() != null) {
        		line = _csvEscape(audit.getMigrationOid()) + "," + _csvEscape(audit.getSrcPath()) + "," + _csvEscape(audit.getLocalPath());
        	} else {
        		line = _csvEscape(audit.getMigrationOid()) + "," + _csvEscape(audit.getLocalPath()) + "," + _csvEscape(audit.getDstPath());
        	}
            line += "," + _csvEscape(audit.getWarning());
            warningWriter.write(line);
            warningWriter.newLine();
            warningWriter.flush();
        } catch (IOException e) {
            log.error("Failed to write warning entry to CSV: " + e.getMessage());
        }
    }
    /**
     * Closes both CSV writers.
     *
     * @throws IOException if an I/O error occurs while closing the writers
     */
    @Override
    public void close() throws IOException {
        IOException exception = null;

        try {
            successWriter.close();
        } catch (IOException e) {
            exception = e;
        }

        try {
            errorWriter.close();
        } catch (IOException e) {
            if (exception == null) {
                exception = e;
            } else {
                exception.addSuppressed(e);
            }
        }
        
        try {
            warningWriter.close();
        } catch (IOException e) {
            if (exception == null) {
                exception = e;
            } else {
                exception.addSuppressed(e);
            }
        }

        if (exception != null) {
            throw exception;
        }
    }
    /**
     * Escapes a value for CSV format.
     * <p>
     * - Wraps value in double quotes if it contains comma, quote or newline.
     * - Escapes double quotes by doubling them.
     *
     * @param value raw value
     * @return CSV-escaped value
     */
    private String _csvEscape(String value) {
        if (value == null) {
            return "";
        }
        boolean containsSpecial = value.contains(",") ||
                                  value.contains("\"") ||
                                  value.contains("\n") ||
                                  value.contains("\r");
        String escaped = value.replace("\"", "\"\"");
        if (containsSpecial) {
            return "\"" + escaped + "\"";
        }
        return escaped;
    }
    /**
     * Creates a UTF-8 BufferedWriter for the given path and writes a UTF-8 BOM
     * at the beginning of the file so that tools like Excel detect the encoding correctly.
     *
     * @param path CSV file path
     * @return buffered writer ready to write CSV content in UTF-8
     * @throws IOException if an I/O error occurs
     */
    private static BufferedWriter _newUtf8CsvWriterWithBom(Path path) throws IOException {
        OutputStream out = Files.newOutputStream(
                path,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE
        );
        // Write UTF-8 BOM
        out.write(UTF8_BOM);
        // Wrap with a UTF-8 writer
        return new BufferedWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));
    }
    /**
     * Writes CSV headers to both success and error CSV files.
     *
     * @throws IOException if an I/O error occurs
     */
    private void writeHeaders() throws IOException {
        successWriter.write("oid,source_path,destination_path");
        successWriter.newLine();
        successWriter.flush();

        errorWriter.write("oid,source_path,destination_path,error");
        errorWriter.newLine();
        errorWriter.flush();
        
        warningWriter.write("oid,source_path,destination_path,warning");
        warningWriter.newLine();
        warningWriter.flush();
    }
}