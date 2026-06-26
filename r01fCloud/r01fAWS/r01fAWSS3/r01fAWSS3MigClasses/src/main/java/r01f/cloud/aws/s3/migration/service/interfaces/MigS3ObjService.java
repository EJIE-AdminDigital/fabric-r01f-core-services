package r01f.cloud.aws.s3.migration.service.interfaces;

import java.util.List;
import java.util.Optional;

import jakarta.validation.ValidationException;
import r01f.cloud.aws.s3.migration.db.entities.R01TDBEntityForMigS3;
import r01f.cloud.aws.s3.migration.db.entities.R01TDBEntityForMigS3Obj;

/**
 * Data Access Object (DAO) interface for managing {@link R01TDBEntityForMigS3} entities.
 * This interface defines the contract for persistence operations related to S3 migration database entities.
 *
 * <p>
 * This component is part of the S3 migration database layer, providing an abstraction
 * over the underlying data storage mechanism for {@code R01TDBEntityForMigS3} objects.
 * </p>
 *
 * @see R01TDBEntityForMigS3
 */
public interface MigS3ObjService {

    /**
     * Persists or updates an {@link R01TDBEntityForMigS3Obj} entity in the database.
     * If the entity is new, it will be saved; otherwise, its existing record will be updated.
     * 
     * @param entity The {@link R01TDBEntityForMigS3Obj} entity to be saved or updated. Must not be {@code null}.
     * @return The persisted or updated {@link R01TDBEntityForMigS3Obj} entity, potentially with generated identifiers or updated fields.
     */
    public R01TDBEntityForMigS3Obj save(final R01TDBEntityForMigS3Obj entity);
    
    /**
     * Retrieves a list of {@link R01TDBEntityForMigS3Obj} entities that have recorded errors
     * for a specific migration operation. This method filters for entities where the 'error'
     * field is not null, the 'warning' field is null, and the 'migrationOid' matches the provided value.
     *
     * @param migrationOid The OID of the migration operation to filter by. Must not be {@code null} or empty.
     * @return A {@link List} of {@link R01TDBEntityForMigS3Obj} entities that contain errors
     *         for the specified migration. Returns an empty list if no such entities are found.
     */
    public List<R01TDBEntityForMigS3Obj> findAllWithError(final String migrationOid);
    /**
     * Finds a single {@link R01TDBEntityForMigS3Obj} entity based on its migration OID and local path.
     * This method expects to find exactly one entity matching the provided criteria.
     *
     * @param migrationOid The unique identifier (OID) of the overall migration operation.
     *                     Cannot be {@code null} or empty.
     * @param localPath    The local path associated with the S3 migration entity.
     *                     Cannot be {@code null} or empty.
     * @return An {@link Optional} containing the single {@link R01TDBEntityForMigS3Obj} entity if found,
     *         or an empty {@link Optional} if no entity matches the criteria.
     * @throws IllegalArgumentException If {@code migrationOid} or {@code localPath} is {@code null} or empty.
     * @throws ValidationException      If no entity is found for the given criteria, or if more than one entity
     *                                  is found, indicating a data integrity issue or an unexpected state.
     *                                  The exception message will provide details about the specific failure.
     */
    public Optional<R01TDBEntityForMigS3Obj> findByLocalPath(final String migrationOid, final String localPath);
}