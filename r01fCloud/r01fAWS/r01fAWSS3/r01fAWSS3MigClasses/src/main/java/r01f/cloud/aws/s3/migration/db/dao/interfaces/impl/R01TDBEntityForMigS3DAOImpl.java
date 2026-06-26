package r01f.cloud.aws.s3.migration.db.dao.interfaces.impl;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import r01f.cloud.aws.s3.migration.db.dao.interfaces.R01TDBEntityForMigS3DAO;
import r01f.cloud.aws.s3.migration.db.entities.R01TDBEntityForMigS3;
import r01f.cloud.aws.s3.migration.model.MigrationCommonOIDs.MigrationOID;

/**
 * Concrete implementation of the {@link R01TDBEntityForMigS3DAO} interface.
 * This class provides the persistence logic for {@link R01TDBEntityForMigS3} entities
 * using JPA (Java Persistence API) and an {@link EntityManager}.
 *
 * <p>
 * It handles both the creation (persisting new entities) and updating (merging existing entities)
 * of S3 migration database entities. New entities are assigned a unique {@link MigrationOID}
 * before persistence.
 * </p>
 *
 * @see R01TDBEntityForMigS3DAO
 * @see R01TDBEntityForMigS3
 * @see EntityManager
 */
@Accessors(prefix="_")
@NoArgsConstructor
public class R01TDBEntityForMigS3DAOImpl implements R01TDBEntityForMigS3DAO {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public R01TDBEntityForMigS3 save(final EntityManager entityManager, final R01TDBEntityForMigS3 entity) {
    	if (entity == null) {
            throw new IllegalArgumentException("Entity to save cannot be null.");
        }
        if (entity.getOid() == null) {
            entity.setOid(MigrationOID.supplyId());
            entityManager.persist(entity);
            return entity;
        } else {
            return entityManager.merge(entity);
        }
    }
    @Override
    public Optional<R01TDBEntityForMigS3> findByMigrationOid(final EntityManager entityManager, final String migrationOid) {
        if (migrationOid == null || migrationOid.trim().isEmpty()) {
            throw new IllegalArgumentException("MigrationOID cannot be null or empty when searching for an entity.");
        }
        String jpql = "SELECT e FROM R01TDBEntityForMigS3 e WHERE e._migrationOid = :migrationOid ORDER BY e._migrationOid ASC";
        TypedQuery<R01TDBEntityForMigS3> query = entityManager.createQuery(jpql, R01TDBEntityForMigS3.class);
        query.setParameter("migrationOid", migrationOid);
        List<R01TDBEntityForMigS3> results = query.getResultList();
        if (results.isEmpty()) {
            return Optional.empty();
        } else if (results.size() > 1) {
            throw new IllegalStateException("Multiple entities found for migration OID '" + migrationOid + "'. Expected a single result.");
        } else {
            return Optional.of(results.get(0));
        }
    }
    @Override
    public Optional<R01TDBEntityForMigS3> findByRemotePath(final EntityManager entityManager, final String remotePath) {
        if (remotePath == null || remotePath.trim().isEmpty()) {
            throw new IllegalArgumentException("RemotePath cannot be null or empty when searching for an entity.");
        }
        String jpql = "SELECT e FROM R01TDBEntityForMigS3 e WHERE e._remotePath = :remotePath ORDER BY e._migrationOid ASC";
        TypedQuery<R01TDBEntityForMigS3> query = entityManager.createQuery(jpql, R01TDBEntityForMigS3.class);
        query.setParameter("remotePath", remotePath);
        List<R01TDBEntityForMigS3> results = query.getResultList();
        if (results.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(results.get(0));
        }
    }
}