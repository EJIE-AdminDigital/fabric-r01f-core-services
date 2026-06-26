package r01f.cloud.aws.s3.migration.db.dao.interfaces.impl;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import r01f.cloud.aws.s3.migration.db.dao.interfaces.R01TDBEntityForMigS3ObjDAO;
import r01f.cloud.aws.s3.migration.db.entities.R01TDBEntityForMigS3Obj;
import r01f.cloud.aws.s3.migration.model.MigrationCommonOIDs.MigrationOID;

/**
 * Concrete implementation of the {@link R01TDBEntityForMigS3ObjDAO} interface.
 * This class provides the persistence logic for {@link R01TDBEntityForMigS3Obj} entities
 * using JPA (Java Persistence API) and an {@link EntityManager}.
 *
 * <p>
 * It handles both the creation (persisting new entities) and updating (merging existing entities)
 * of S3 migration database entities. New entities are assigned a unique {@link MigrationOID}
 * before persistence.
 * </p>
 *
 * @see R01TDBEntityForMigS3ObjDAO
 * @see R01TDBEntityForMigS3Obj
 * @see EntityManager
 */
@Accessors(prefix="_")
@NoArgsConstructor
public class R01TDBEntityForMigS3ObjDAOImpl implements R01TDBEntityForMigS3ObjDAO {
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
    public R01TDBEntityForMigS3Obj save(final EntityManager entityManager, final R01TDBEntityForMigS3Obj entity) {
        if (entity.getOid() == null) {
        	entity.setOid(MigrationOID.supplyId());
            entityManager.persist(entity);
            return entity;
        } else {
            return entityManager.merge(entity);
        }
    }
    @Override
    public List<R01TDBEntityForMigS3Obj> findAllWithError(final EntityManager entityManager, final String migrationOid) {
        if (migrationOid == null || migrationOid.trim().isEmpty()) {
            throw new IllegalArgumentException("Migration OID cannot be null or empty when searching for error entities.");
        }
        String jpql = "SELECT e FROM R01TDBEntityForMigS3Obj e WHERE e._error IS NOT NULL AND e._warning IS NULL AND e._migrationOid = :migrationOid ORDER BY e._sourcePath ASC";
        TypedQuery<R01TDBEntityForMigS3Obj> query = entityManager.createQuery(jpql, R01TDBEntityForMigS3Obj.class);
        query.setParameter("migrationOid", migrationOid);
        return query.getResultList();
    }
    @Override
    public Optional<R01TDBEntityForMigS3Obj> findByLocalPath(final EntityManager entityManager, final String migrationOid, final String localPath) {
        if (migrationOid == null || migrationOid.trim().isEmpty()) {
            throw new IllegalArgumentException("Migration OID cannot be null or empty when searching for entities.");
        }
        if (localPath == null || localPath.trim().isEmpty()) {
            throw new IllegalArgumentException("Local path cannot be null or empty when searching for entities.");
        }
        String jpql = "SELECT e FROM R01TDBEntityForMigS3Obj e WHERE e._migrationOid = :migrationOid AND e._localPath = :localPath ORDER BY e._destinationPath ASC";
        TypedQuery<R01TDBEntityForMigS3Obj> query = entityManager.createQuery(jpql, R01TDBEntityForMigS3Obj.class);
        query.setParameter("migrationOid", migrationOid);
        query.setParameter("localPath", localPath);
        List<R01TDBEntityForMigS3Obj> results = query.getResultList();
        if (results.isEmpty()) {
            return Optional.empty();
        } else if (results.size() > 1) {
            throw new IllegalStateException("Multiple entities found for migration OID '" + migrationOid + "' and local path '" + localPath + "'. Expected a single result.");
        } else {
            return Optional.of(results.get(0));
        }
    }
}