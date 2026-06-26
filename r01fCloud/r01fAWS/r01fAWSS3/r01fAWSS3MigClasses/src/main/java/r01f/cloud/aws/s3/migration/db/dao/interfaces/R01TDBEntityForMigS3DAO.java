package r01f.cloud.aws.s3.migration.db.dao.interfaces;

import java.util.Optional;

import jakarta.persistence.EntityManager;
import r01f.cloud.aws.s3.migration.db.entities.R01TDBEntityForMigS3;

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
public interface R01TDBEntityForMigS3DAO {

    /**
     * Persists or updates an {@link R01TDBEntityForMigS3} entity in the database.
     * If the entity is new, it will be saved; otherwise, its existing record will be updated.
     *
     * @param entityManager {@link EntityManager} 
     * @param entity The {@link R01TDBEntityForMigS3} entity to be saved or updated. Must not be {@code null}.
     * @return The persisted or updated {@link R01TDBEntityForMigS3} entity, potentially with generated identifiers or updated fields.
     */
    public R01TDBEntityForMigS3 save(final EntityManager entityManager, final R01TDBEntityForMigS3 entity);
    /**
     * Finds a {@link R01TDBEntityForMigS3} entity by its unique identifier (OID).
     *
     * @param entityManager {@link EntityManager} 
     * @param oid The OID of the entity to find. Must not be {@code null} or empty.
     * @return An {@link Optional} containing the found {@link R01TDBEntityForMigS3} entity,
     *         or an empty {@link Optional} if no entity with the given OID exists.
     * @throws IllegalArgumentException if the provided {@code oid} is {@code null} or empty.
     */
    public Optional<R01TDBEntityForMigS3> findByMigrationOid(final EntityManager entityManager, final String oid);
    /**
     * Finds a {@link R01TDBEntityForMigS3} entity by its remote path. Find the first launch for this remote path.
     *
     * @param entityManager {@link EntityManager} 
     * @param remotePath The remote path to find.
     * @return An {@link Optional} containing the found {@link R01TDBEntityForMigS3} entity,
     *         or an empty {@link Optional} if no entity with the remote path exists.
     * @throws IllegalArgumentException if the provided {@code oid} is {@code null} or empty.
     */
    public Optional<R01TDBEntityForMigS3> findByRemotePath(final EntityManager entityManager, final String remotePath);
}