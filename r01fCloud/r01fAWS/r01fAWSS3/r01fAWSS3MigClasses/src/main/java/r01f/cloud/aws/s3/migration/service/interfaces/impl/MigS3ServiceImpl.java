package r01f.cloud.aws.s3.migration.service.interfaces.impl;

import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.cloud.aws.s3.migration.db.dao.interfaces.R01TDBEntityForMigS3DAO;
import r01f.cloud.aws.s3.migration.db.dao.interfaces.impl.R01TDBEntityForMigS3DAOImpl;
import r01f.cloud.aws.s3.migration.db.entities.R01TDBEntityForMigS3;
import r01f.cloud.aws.s3.migration.service.interfaces.MigS3Service;

/**
 * Service class for R01TDBEntityForMigS3DAO
 *
 * <p>
 * This implementation uses JPA's {@link EntityManager} to interact with the database
 * through a {@link R01TDBEntityForMigS3DAO} instance.
 * </p>
 *
 * <p>
 * The class is marked as {@code @Accessors(prefix="_")} to work with Lombok's
 * getter/setter generation for fields prefixed with an underscore.
 * </p>
 *
 * @see MigS3Service
 * @see R01TDBEntityForMigS3DAO
 * @see R01TDBEntityForMigS3
 */
@Accessors(prefix="_")
@Slf4j
public class MigS3ServiceImpl implements MigS3Service {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter @Setter private EntityManagerFactory _entityManagerFactory;
	@Getter @Setter private R01TDBEntityForMigS3DAO _dbEntityForMigS3DAO;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
    /**
     * Constructs a new {@code MigrationDBAuditServiceImpl} with the provided {@link EntityManagerFactory}.
     * This constructor initializes the internal {@link R01TDBEntityForMigS3ObjObjDAO} using the given
     * {@link EntityManagerFactory}.
     *
     * @param entityManagerFactory The {@link EntityManagerFactory} to be used for database operations. Must not be {@code null}.
     */
    public MigS3ServiceImpl(EntityManagerFactory entityManagerFactory) {
    	this._entityManagerFactory = entityManagerFactory;
    	_dbEntityForMigS3DAO = new R01TDBEntityForMigS3DAOImpl();
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public R01TDBEntityForMigS3 save(R01TDBEntityForMigS3 entity) {
		EntityManager entityManager = null;
		R01TDBEntityForMigS3 entityForMigS3 = null;
		try {
        	entityManager = this._entityManagerFactory.createEntityManager();
        	entityManager.getTransaction().begin();
        	entityForMigS3 = this._dbEntityForMigS3DAO.save(entityManager,entity);
        	entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction() != null && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            log.error("Failed to save entity", e);
        } finally{
        	if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
		return entityForMigS3;
	}
	@Override
	public Optional<R01TDBEntityForMigS3> findByMigrationOid(String migrationOid) {
		EntityManager entityManager = null;
		Optional<R01TDBEntityForMigS3> entityForMigS3 = null;
		try {
        	entityManager = this._entityManagerFactory.createEntityManager();
        	entityForMigS3 = this._dbEntityForMigS3DAO.findByMigrationOid(entityManager,migrationOid);
        } catch (Exception e) {
        	log.error("Failed to find entity", e);
        } finally{
        	if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
		return entityForMigS3;
	}
	@Override
	public Optional<R01TDBEntityForMigS3> findByRemotePath(String remotePath) {
		EntityManager entityManager = null;
		Optional<R01TDBEntityForMigS3> entityForMigS3 = null;
		try {
        	entityManager = this._entityManagerFactory.createEntityManager();
        	entityForMigS3 = this._dbEntityForMigS3DAO.findByRemotePath(entityManager,remotePath);
        } catch (Exception e) {
        	log.error("Failed to find entity", e);
        } finally{
        	if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
		return entityForMigS3;
	}
}