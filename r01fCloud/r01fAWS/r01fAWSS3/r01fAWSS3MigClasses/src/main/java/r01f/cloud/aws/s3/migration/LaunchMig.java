package r01f.cloud.aws.s3.migration;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.extern.slf4j.Slf4j;
import r01f.cloud.aws.s3.migration.db.entities.R01TDBEntityForMigS3;
import r01f.cloud.aws.s3.migration.model.MigrationComplexModel;
import r01f.cloud.aws.s3.migration.utils.FileUtils;
import r01f.cloud.aws.s3.migration.utils.S3PurgeUtil;

/**
 * <p>
 * Command line application that migrates content from HDFS (or other configured source) to Amazon S3 in two stages.
 * </p>
 * <p>
 * The process is:
 * <ol>
 *   <li>Download a directory tree from the configured remote source (e.g., HDFS) to a local root directory.</li>
 *   <li>Upload the downloaded local directory structure to an S3 bucket.</li>
 * </ol>
 * Both stages record detailed results (success, error and warning entries) into CSV files / DB.
 * </p>
 * <p>
 * Usage:
 * <pre>
 *   java -jar s3-migrator.jar;
 * </pre>
 * </p>
 */
@Slf4j
public class LaunchMig extends LaunchMigBase {
	/**
     * Executes the two-stage migration process: from remote source to local, and then from local to S3.
     * This method overrides the abstract {@code execute} method from {@link LaunchMigBase}.
     * <ol>
     *     <li>Deletes the local destination folder to ensure a clean migration.</li>
     *     <li>Initiates the migration from the remote source (e.g., HDFS) to the local file system.</li>
     *     <li>Purges any existing content at the S3 destination path before uploading.</li>
     *     <li>Initiates the migration from the local file system to Amazon S3.</li>
     * </ol>
     *
     * @param migrationFromModel The model containing details about the migration source (remote to local).
     * @param migrationToModel The model containing details about the migration destination (local to S3).
     * @throws URISyntaxException If a URI syntax error occurs during path construction.
     * @throws IOException If an I/O error occurs during file operations (delete, read, write).
     */
	@Override
	protected void execute(List<MigrationComplexModel> migrationModelList) throws URISyntaxException, IOException {
		Flowable<MigrationComplexModel> items = Flowable.fromIterable(migrationModelList);
		log.info("############################ Migration number of threads: " + _migrationConfigModel.getThreadPool());
		ExecutorService executorService = Executors.newFixedThreadPool(_migrationConfigModel.getThreadPool());
		try {
			items.parallel()
					 .runOn(Schedulers.from(executorService))
					 .doOnNext(migrationModel -> {
						 	 R01TDBEntityForMigS3 migrationEntity = new R01TDBEntityForMigS3();
					         migrationEntity.setMigrationOid(migrationModel.getMigrationFromModel().getMigrationOid().asString());
					         migrationEntity.setRemotePath(r01f.types.Path.from(migrationModel.getMigrationFromModel().getSrcPath()).asAbsoluteString());
					         migrationEntity.setRemoteType(this._migrationConfigModel.getOriginFileStoreType().getCode().toUpperCase());
					         migrationEntity.setStartDate(LocalDateTime.now());
					         migrationEntity = this._migService.save(migrationEntity);
						 	 //FROM HDFS
							 log.info("############################ Migration to local from remote start ############################");
							 FileUtils.delete(migrationModel.getMigrationFromModel().getDstPath());
							 this._migrationFromService.migrateFrom(migrationModel.getMigrationFromModel());
							 log.info("############################ Migration to local from remote finished ############################");
							 //TO S3
							 log.info("############################ Migration to S3 from local start ############################");
							 S3PurgeUtil.purge(migrationModel.getMigrationToModel().getDstPath());
							 this._migrationToService.migrateTo(_migrationConfigModel.getWorkingDirectory(),migrationModel.getMigrationToModel());
							 log.info("############################ Migration to S3 from local finished ############################");
							 migrationEntity.setEndDate(LocalDateTime.now());
							 this._migService.save(migrationEntity);
		                 }
					  )
					 .doOnError(th -> log.error("\t... error during item processing: {}",
							   				    th.getMessage(),th))
					 .doOnComplete(() -> log.info("End item processing."))
					 .sequential()
					 .blockingSubscribe();
		} finally {
			log.info("Shutting down executor service...");
			executorService.shutdown();
            log.info("Executor service shut down.");
        }
	}
	/**
     * Application entry point for the S3 migration tool.
     * This method initializes the {@link LaunchMig} class and calls its {@link #launchMig(String[])} method
     * to start the migration process.
     *
     * @param args Command line arguments.
     * @throws IOException If an unrecoverable I/O error occurs during the migration process.
     * @throws URISyntaxException If a URI syntax error occurs during path construction.
     */
	public static void main(String[] args) throws IOException, URISyntaxException {
		LaunchMig launcher = new LaunchMig();
		launcher.launchMig();
    }
}