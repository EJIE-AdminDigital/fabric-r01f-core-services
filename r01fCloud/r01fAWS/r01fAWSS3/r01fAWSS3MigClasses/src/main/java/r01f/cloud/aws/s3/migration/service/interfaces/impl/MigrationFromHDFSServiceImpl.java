package r01f.cloud.aws.s3.migration.service.interfaces.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import org.apache.hadoop.conf.Configuration;

import com.google.inject.Inject;

import lombok.extern.slf4j.Slf4j;
import r01f.cloud.aws.s3.migration.model.AuditModel;
import r01f.cloud.aws.s3.migration.model.MigrationFromModel;
import r01f.cloud.aws.s3.migration.service.interfaces.MigrationAuditService;
import r01f.cloud.aws.s3.migration.service.interfaces.MigrationFromService;
import r01f.file.FileProperties;
import r01f.filestore.api.FileStoreAPI;
import r01f.filestore.api.FileStoreFilerAPI;
import r01f.filestore.api.hdfs.HDFSFileStoreAPI;
import r01f.filestore.api.hdfs.HDFSFileStoreFilerAPI;
import r01f.guids.CommonOIDs.Environment;
import r01f.internal.R01HomeLocation;

/**
 * Service responsible for downloading directory structures from HDFS to the local filesystem.
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
 * traversing an HDFS directory tree and materializing it on the local filesystem.
 */
@Slf4j
public class MigrationFromHDFSServiceImpl extends MigrationServiceImpl implements MigrationFromService {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Creates a new {@code MigrationFromHDFSServiceImpl} using the provided HDFS APIs and CSV logger.
	 *
	 * @param migrationAuditService audit used to record successful downloads, warnings and errors.
	 * @throws NullPointerException if any argument is {@code null}.
	 */
	@Inject
	public MigrationFromHDFSServiceImpl(MigrationAuditService migrationAuditService) {
        super(new HDFSFileStoreAPI(_buildHDFSConfig(Environment.guessFromSystemProps())), 
        	  new HDFSFileStoreFilerAPI(_buildHDFSConfig(Environment.guessFromSystemProps())), 
        	  migrationAuditService);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void migrateFrom(MigrationFromModel migrationFromModel) throws IOException {
		Files.createDirectories(migrationFromModel.getDstPath());
        _downloadFolderRecursive(migrationFromModel.getMigrationOid().getId(),migrationFromModel.getSrcPath(),migrationFromModel.getDstPath());
	}
	@Override
	public void migrateOneFrom(MigrationFromModel migrationFromModel) throws IOException {
		r01f.types.Path element = r01f.types.Path.from(migrationFromModel.getSrcPath());
		if(!element.isFolderPath()) {
			try {
                try (InputStream in = this._fileStoreAPI.getFileInputStream(element)) {
                    Files.copy(in, migrationFromModel.getDstPath());
                    this._migrationAuditService.auditSuccess(new AuditModel(migrationFromModel.getMigrationOid().toString(),
																			element.asAbsoluteString(), 
																			r01f.types.Path.from(migrationFromModel.getDstPath()).asAbsoluteString(),
																			null,null,null));
                }
            } catch (Exception e) {
            	this._migrationAuditService.auditError(new AuditModel(migrationFromModel.getMigrationOid().toString(),
            														  element.asAbsoluteString(), 
            														  r01f.types.Path.from(migrationFromModel.getDstPath()).asAbsoluteString(), 
            														  null,null,
            														  this._getStackTraceAsSingleLine(e)));
                log.error("Error processing path " + element + ": " + e.getMessage());
            }
		} else if(element.isFolderPath()) {
			Files.createDirectories(migrationFromModel.getDstPath());
			this._migrationAuditService.auditSuccess(new AuditModel(migrationFromModel.getMigrationOid().toString(),
																	element.asAbsoluteString(), 
																	r01f.types.Path.from(migrationFromModel.getDstPath()).asAbsoluteString(),
																	null,null,null));
		}
	}
	/**
	 * Recursively processes a directory: creates the corresponding local folder,
	 * lists its children using the {@link FileStoreFilerAPI},
	 * and downloads files or descends into subdirectories.
	 * <p>
	 * Symbolic links are ignored. Each successful or failed operation is reported
	 * to the {@link CsvResultService}.
	 *
	 * @param oid migration identifier.
	 * @param currentHdfsFolder absolute HDFS folder path represented as a {@code java.nio.file.Path}.
	 * @param currentLocalFolder matching local filesystem directory where content is written.
	 * @throws IOException if a folder cannot be listed or created.
	 */
    private void _downloadFolderRecursive(final String oid,
    									  final java.nio.file.Path currentHdfsFolder,
                                          final java.nio.file.Path currentLocalFolder) throws IOException {
        Files.createDirectories(currentLocalFolder);
        FileProperties[] children = this._fileStoreFilerAPI.listFolderContents(r01f.types.Path.from(currentHdfsFolder),null,false);
        if (children == null || children.length == 0) {
            return;
        }
        for (FileProperties child : children) {
            r01f.types.Path childHdfsPath = child.getPath();
            r01f.types.Path relative = childHdfsPath.remainingPathFrom(r01f.types.Path.from(currentHdfsFolder));
            String relativeStr = relative.asString();
            if (relativeStr.startsWith("/")) {
                relativeStr = relativeStr.substring(1);
            }
            java.nio.file.Path childLocalPath = currentLocalFolder.resolve(relativeStr);
            if (child.isFolder()) {
                _downloadFolderRecursive(oid,childHdfsPath.asJavaNIOPath(), childLocalPath);
                this._migrationAuditService.auditSuccess(new AuditModel(oid,childHdfsPath.asAbsoluteString(),r01f.types.Path.from(childLocalPath).asAbsoluteString(),null,null,null));
            } else if (child.isFile()) {
                try {
	                java.nio.file.Path parentDir = childLocalPath.getParent();
	                if (parentDir != null) {
	                    Files.createDirectories(parentDir);
	                }
	                try (InputStream in = this._fileStoreAPI.getFileInputStream(childHdfsPath)) {
	                    Files.copy(in, childLocalPath);
	                    this._migrationAuditService.auditSuccess(new AuditModel(oid,childHdfsPath.asAbsoluteString(),r01f.types.Path.from(childLocalPath).asAbsoluteString(),null,null,null));
	                }
                } catch (Exception e) {
                	this._migrationAuditService.auditError(new AuditModel(oid,childHdfsPath.asAbsoluteString(),r01f.types.Path.from(childLocalPath).asAbsoluteString(),null,null,this._getStackTraceAsSingleLine(e)));
	                log.error("Error processing path " + childHdfsPath + ": " + e.getMessage());
                }
            } else if (child.isSymLink()) {
                log.warn("**** Ignoring symlink " + childHdfsPath + " when downloading from HDFS ****");
                this._migrationAuditService.auditWarning(new AuditModel(oid,childHdfsPath.asAbsoluteString(),r01f.types.Path.from(childLocalPath).asAbsoluteString(),null,"symlink " + childHdfsPath + " when downloading from HDFS",null));
            }
        }
    }
    /**
     * Builds the Hadoop {@link Configuration} for the given environment.
     * <p>
     * This method sets the {@code hadoop.home.dir} system property and loads the
     * {@code core-site.xml} and {@code hdfs-site.xml} configuration files from
     * the classpath using the pattern:
     * <pre>
     *   hdfs/&lt;env&gt;/core-site.xml
     *   hdfs/&lt;env&gt;/hdfs-site.xml
     * </pre>
     *
     * @param env logical environment identifier (for example {@code loc}, {@code dev}, {@code prod}).
     * @return a configured Hadoop {@link Configuration} instance ready to be used by HDFS clients.
     */
	private static Configuration _buildHDFSConfig(final Environment env) {
		System.setProperty("hadoop.home.dir",
						   R01HomeLocation.HOME_PATH.joinedWith("hadoop/hadoop-2.7.3").asAbsoluteString());		// alternative to set HADOOP_HOME
		
		Configuration conf = new Configuration();
		conf.addResource(env + "/hadoop/core-site.xml");
		conf.addResource(env + "/hadoop/hdfs-site.xml");
		return conf;
	}
}