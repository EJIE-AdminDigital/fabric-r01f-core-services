package r01f.cloud.aws.s3.migration.service.interfaces;

import java.io.IOException;
import java.nio.file.Path;

import r01f.cloud.aws.s3.migration.model.MigrationToModel;

public interface MigrationToService extends MigrationService {

	/**
	 * Upload a complete directory into destination.
	 * <p>
	 * The method creates the destination directory if it does not exist and starts
	 * a recursive traversal of the tree.
	 *
	 * @param migrationModel migration model object.
	 */
	public void migrateTo(Path workingDirectory, MigrationToModel migrationModel) throws IOException;
	/**
	 * Upload a folder/file into destination.
	 * <p>
	 * The method creates the destination directory/file
	 * 
	 * @param workingDirectory migration working directory.
	 * @param migrationModel migration model object.
	 */
	public void migrateOneTo(Path workingDirectory, MigrationToModel migrationModel) throws IOException;
	
}