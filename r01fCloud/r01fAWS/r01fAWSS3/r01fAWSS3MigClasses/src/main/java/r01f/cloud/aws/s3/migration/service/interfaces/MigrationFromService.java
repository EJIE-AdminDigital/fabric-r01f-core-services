package r01f.cloud.aws.s3.migration.service.interfaces;

import java.io.IOException;

import r01f.cloud.aws.s3.migration.model.MigrationFromModel;

public interface MigrationFromService extends MigrationService {

	/**
	 * Downloads a complete directory into fylesystem.
	 * <p>
	 * The method creates the destination directory if it does not exist and starts
	 * a recursive traversal of the tree.
	 *
	 * @param migrationModel migration model object.
	 */
	public void migrateFrom(MigrationFromModel migrationModel) throws IOException;
	/**
	 * Downloads a directory/file into fylesystem.
	 * <p>
	 * The method creates the destination directory/file.
	 *
	 * @param migrationModel migration model object.
	 */
	public void migrateOneFrom(MigrationFromModel migrationModel) throws IOException;
	
}