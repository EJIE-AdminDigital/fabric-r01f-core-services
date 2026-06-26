package r01f.cloud.aws.s3.migration.service.interfaces.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.inject.Inject;

import lombok.extern.slf4j.Slf4j;
import r01.filestore.api.s3.S3FileStoreAPI;
import r01.filestore.api.s3.S3FileStoreFilerAPI;
import r01f.cloud.aws.s3.client.api.AWSS3BucketConfig;
import r01f.cloud.aws.s3.client.api.AWSS3BucketConfigBuilder;
import r01f.cloud.aws.s3.client.api.AWSS3ClientConfig;
import r01f.cloud.aws.s3.client.api.AWSS3ClientConfigBuilder;
import r01f.cloud.aws.s3.migration.model.AuditModel;
import r01f.cloud.aws.s3.migration.model.MigrationToModel;
import r01f.cloud.aws.s3.migration.service.interfaces.MigrationAuditService;
import r01f.cloud.aws.s3.migration.service.interfaces.MigrationToService;
import r01f.cloud.aws.s3.migration.utils.LocaleUtils;
import r01f.filestore.api.FileStoreAPI;
import r01f.filestore.api.FileStoreFilerAPI;
import r01f.guids.CommonOIDs.AppCode;
import r01f.xmlproperties.XMLProperties;
import r01f.xmlproperties.XMLPropertiesBuilder;
import r01f.xmlproperties.XMLPropertiesForApp;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

/**
 * Service responsible for downloading directory structures from the local filesystem to S3.
 * <p>
 * The service relies on two abstractions:
 * <ul>
 *   <li>{@link FileStoreAPI} for file-level operations such as reading file streams.</li>
 *   <li>{@link FileStoreFilerAPI} for directory-related operations such as listing folders.</li>
 * </ul>
 * Each downloaded item is recorded through a {@link CsvResultService}, which stores
 * success entries, errors and warnings in separate CSV files.
 * <p>
 * This component does not perform any S3 interaction. Its only responsibility is
 * traversing an local filesystem tree and materializing it on the S3.
 */
@Slf4j
public class MigrationToS3ServiceImpl extends MigrationServiceImpl implements MigrationToService {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
     * Creates a new {@link MigrationToS3ServiceImpl}.
     *
     * @param migrationAuditService audit used to record success and error results
     */
	@Inject
	public MigrationToS3ServiceImpl(MigrationAuditService migrationAuditService) {
		super(new S3FileStoreAPI(_buildS3Config(),
					  			 _buildS3BucketConfig()), 
			  new S3FileStoreFilerAPI(_buildS3Config(),
        							  _buildS3BucketConfig()), 
			  migrationAuditService);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void migrateTo(Path workingDirectory, MigrationToModel migrationToModel) throws IOException {
		try (var paths = Files.walk(migrationToModel.getSrcPath())) {
	        paths.forEach(path -> {
                if (!path.equals(migrationToModel.getSrcPath()) && Files.isDirectory(path)) {
                    this._createFolder(migrationToModel.getMigrationOid().getId(),
                    				   workingDirectory,
                    				   path,
                    				   migrationToModel.getIsSanitize());
                } else if (Files.isRegularFile(path)) {
                    this._createFile(migrationToModel.getMigrationOid().getId(),
                    				 workingDirectory,
                    				 path,
                    				 migrationToModel.getIsSanitize());
                }
	        });
	    }
	}
	@Override
	public void migrateOneTo(Path workingDirectory, MigrationToModel migrationToModel) throws IOException {
		r01f.types.Path element = r01f.types.Path.from(migrationToModel.getSrcPath());
		if(!element.isFolderPath()) {
			this._createFile(migrationToModel.getMigrationOid().getId(),
                    		 workingDirectory,
                    		 migrationToModel.getSrcPath(),
                    		 migrationToModel.getIsSanitize());
		} else if(element.isFolderPath()) {
			this._createFolder(migrationToModel.getMigrationOid().getId(),
                    		   workingDirectory,
                    		   migrationToModel.getSrcPath(),
                    		   migrationToModel.getIsSanitize());
		}
	}
	/**
	 * Creates a remote folder for the given local directory using the file store API.
	 *
	 * @param oid migration identifier.
	 * @param rootDir root directory used to calculate the relative key
	 * @param path     local directory for which the remote folder should be created
	 * @param isSanitize true: the path folder will be sanitized
	 */
	private void _createFolder(final String oid,Path rootDir, Path path, boolean isSanitize) {
	    Path relative = rootDir.relativize(path);
	    String key = _sanitizeKeyS3(relative.toString(),isSanitize);
	    log.debug("+++++++ Create folder: " + path.toAbsolutePath() + " -> key: " + key);
	    try {
	        this._fileStoreFilerAPI.createFolder(r01f.types.Path.forId(key));
	        if(!_matchesLocalPath(key,relative)) {
	        	this._migrationAuditService.auditWarning(new AuditModel(oid,null,r01f.types.Path.from(path).asAbsoluteString(),r01f.types.Path.forId(key).asAbsoluteString(),"Different path name",null));
	        } else {
	        	this._migrationAuditService.auditSuccess(new AuditModel(oid,null,r01f.types.Path.from(path).asAbsoluteString(),r01f.types.Path.forId(key).asAbsoluteString(),null,null));
	        }
	    } catch (Exception ex) {
	    	log.error("!!!!!! Create folder: " + path.toAbsolutePath() + " -> key: " + key, ex);
	        this._migrationAuditService.auditError(new AuditModel(oid,null,r01f.types.Path.from(path).asAbsoluteString(),null,null,this._getStackTraceAsSingleLine(ex)));
	    }
	}
    /**
     * Uploads a single file and logs the outcome.
     *
     * @param oid migration identifier.
     * @param rootDir root directory used to calculate relative key
     * @param path    file to upload
     * @param isSanitize true: the path folder will be sanitized
     */
    private void _createFile(final String oid,Path rootDir, Path path, boolean isSanitize) {
        Path relative = rootDir.relativize(path);
	    String key = _sanitizeKeyS3(relative.toString(),isSanitize);
	    log.debug("+++++++ Create file: " + path.toAbsolutePath() + " -> key: " + key);
	    try (InputStream ios = Files.newInputStream(path)) {
	        this._fileStoreAPI.writeToFile(ios, r01f.types.Path.forId(key), true);
	        if(!_matchesLocalPath(key,relative)) {
	        	this._migrationAuditService.auditWarning(new AuditModel(oid,null,r01f.types.Path.from(path).asAbsoluteString(),r01f.types.Path.forId(key).asAbsoluteString(),"Different path name",null));
	        } else {
	        	this._migrationAuditService.auditSuccess(new AuditModel(oid,null,r01f.types.Path.from(path).asAbsoluteString(),r01f.types.Path.forId(key).asAbsoluteString(),null,null));
	        }
	    } catch (Exception ex) {
	    	log.error("!!!!!! Create file: " + path.toAbsolutePath() + " -> key: " + key, ex);
	        this._migrationAuditService.auditError(new AuditModel(oid,null,r01f.types.Path.from(path).asAbsoluteString(),null,null,this._getStackTraceAsSingleLine(ex)));
	    }
    }
    /**
     * Builds the {@link AWSS3ClientConfig} for the given environment using XML properties.
     * <p>
     * The configuration is read from the {@code r01f} application properties under the
     * {@code s3} component, using the XML path:
     * <pre>
     *   /moduleConfigForS3/s3ClientConfig
     * </pre>
     *
     * @return an {@link AWSS3ClientConfig} instance containing client level S3 settings.
     */
    private static AWSS3ClientConfig _buildS3Config() {
		// create properties
		XMLProperties xmlProps = XMLPropertiesBuilder.create()
													 .notUsingCache();
		XMLPropertiesForApp testProps = xmlProps.forApp(AppCode.forId("r01f"));
		XMLPropertiesForAppComponent props = testProps.forComponent("s3");
		
		AWSS3ClientConfig s3Config = AWSS3ClientConfigBuilder.fromXMLProperties(props, "/moduleConfigForS3/s3ClientConfig");

		return s3Config;
	}
    /**
     * Builds the {@link AWSS3BucketConfig} for the given environment using XML properties.
     * <p>
     * The configuration is read from the {@code r01f} application properties under the
     * {@code s3} component, using the XML path:
     * <pre>
     *   /moduleConfigForS3/moduleConfigForBucket
     * </pre>
     *
     * @return an {@link AWSS3BucketConfig} instance describing the target S3 bucket layout and options.
     */
	private static AWSS3BucketConfig _buildS3BucketConfig() {
		// create properties
		XMLProperties xmlProps = XMLPropertiesBuilder.create()
													 .notUsingCache();
		XMLPropertiesForApp testProps = xmlProps.forApp(AppCode.forId("r01f"));
		XMLPropertiesForAppComponent props = testProps.forComponent("s3");
		
		AWSS3BucketConfig s3BucketConfig = AWSS3BucketConfigBuilder.fromXMLProperties(props, "/moduleConfigForS3/moduleConfigForBucket");

		return s3BucketConfig;
	}
	/**
	 * Sanitizes a raw path string so it can be safely used as an S3 key.
	 * <p>
	 * - Converts Windows-style backslashes to forward slashes.
	 * - Delegates name normalization to LocaleUtils.customNames(), which is expected
	 *   to handle removal or substitution of invalid characters based on the API rules.
	 *
	 * @param keyS3 raw path string to sanitize
	 * @param isSanitize true: the path folder will be sanitized
	 * @return sanitized path suitable for use as an S3 key
	 */
    private String _sanitizeKeyS3(String keyS3, boolean isSanitize) {
    	String key = keyS3.toString()
	    				  .replace('\\', '/');
    	if(isSanitize) {
    		r01f.types.Path pathKeyS3 = r01f.types.Path.forId(keyS3);
    		if(pathKeyS3.isFilePath()) {
    			String folderPathStr = pathKeyS3.asJavaNIOPath().getParent().toString()
    																		.replace('\\', '/');
    			String sanitizedFolderPathStr = LocaleUtils.customNames(folderPathStr);
    			Path sanitizedPathKeyS3 = r01f.types.Path.from(sanitizedFolderPathStr).asJavaNIOPath()
    																				  .resolve(pathKeyS3.asJavaNIOPath()
    																						  			.getFileName());  		
    			return r01f.types.Path.from(sanitizedPathKeyS3).asAbsoluteString();
    		} else {
    			return LocaleUtils.customNames(pathKeyS3.asAbsoluteString());
    		}
    	} else {
    		return r01f.types.Path.from(key).asAbsoluteString();
    	}
    }
    /**
	 * Checks whether a given S3 key matches a local relative filesystem path.
	 * <p>
	 * Both values are normalized by converting backslashes to forward slashes before comparison.
	 * The comparison is case-insensitive to avoid mismatches in environments where case differences
	 * should not be considered meaningful (e.g., Windows file systems or APIs treating keys as case-insensitive).
	 *
	 * @param keyS3 S3 key already sanitized and stored
	 * @param localRelativePath local relative path obtained from the file system
	 * @return true if both represent the same logical path, ignoring case; false otherwise
	 */
    private boolean _matchesLocalPath(String keyS3, Path localRelativePath) {
    	String localRelativePathStr = r01f.types.Path.from(localRelativePath).asAbsoluteString();
        return r01f.types.Path.from(keyS3).asAbsoluteString().equalsIgnoreCase(localRelativePathStr);
    }
}