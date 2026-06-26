package r01f.cloud.aws.s3.migration.service.interfaces.impl;

import java.util.List;
import java.util.Optional;

import com.google.inject.Inject;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.cloud.aws.s3.migration.db.dao.interfaces.R01TDBEntityForMigS3ObjDAO;
import r01f.cloud.aws.s3.migration.db.dao.interfaces.impl.R01TDBEntityForMigS3ObjDAOImpl;
import r01f.cloud.aws.s3.migration.db.entities.R01TDBEntityForMigS3Obj;
import r01f.cloud.aws.s3.migration.service.interfaces.MigS3ObjService;

/**
 * Service class for R01TDBEntityForMigS3ObjDAO
 *
 * <p>
 * This implementation uses JPA's {@link EntityManager} to interact with the database
 * through a {@link R01TDBEntityForMigS3ObjDAO} instance.
 * </p>
 *
 * <p>
 * The class is marked as {@code @Accessors(prefix="_")} to work with Lombok's
 * getter/setter generation for fields prefixed with an underscore.
 * </p>
 *
 * @see MigS3ObjService
 * @see R01TDBEntityForMigS3ObjDAO
 * @see R01TDBEntityForMigS3Obj
 */
@Accessors(prefix="_")
@Slf4j
public class MigS3ObjServiceImpl implements MigS3ObjService {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter @Setter public EntityManagerFactory _entityManagerFactory;
	@Inject @Getter @Setter private R01TDBEntityForMigS3ObjDAO _dbEntityForMigS3ObjDAO;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
    /**
     * Constructs a new {@code MigS3ObjServiceImpl} with the provided {@link EntityManagerFactory}.
     * This constructor initializes the internal {@link R01TDBEntityForMigS3ObjDAO} using the given
     * {@link EntityManagerFactory}.
     *
     * @param entityManagerFactory The {@link EntityManagerFactory} to be used for database operations. Must not be {@code null}.
     */
    public MigS3ObjServiceImpl(EntityManagerFactory entityManagerFactory) {
    	this._entityManagerFactory = entityManagerFactory;
    	_dbEntityForMigS3ObjDAO = new R01TDBEntityForMigS3ObjDAOImpl();
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public R01TDBEntityForMigS3Obj save(R01TDBEntityForMigS3Obj entity) {
		EntityManager entityManager = null;
		R01TDBEntityForMigS3Obj entityForMigS3Obj = null;
		try {
        	entityManager = this._entityManagerFactory.createEntityManager();
        	entityManager.getTransaction().begin();
        	entityForMigS3Obj = this._dbEntityForMigS3ObjDAO.save(entityManager,entity);
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
		return entityForMigS3Obj;
	}
	@Override
	public List<R01TDBEntityForMigS3Obj> findAllWithError(String migrationOid) {
		EntityManager entityManager = null;
		List<R01TDBEntityForMigS3Obj> entities = null;
		try {
        	entityManager = this._entityManagerFactory.createEntityManager();
        	entities = this._dbEntityForMigS3ObjDAO.findAllWithError(entityManager,migrationOid);
        } catch (Exception e) {
        	log.error("Failed to find entities", e);
        } finally{
        	if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
		return entities;
	}
	@Override
	public Optional<R01TDBEntityForMigS3Obj> findByLocalPath(String migrationOid, String localPath) {
		EntityManager entityManager = null;
		Optional<R01TDBEntityForMigS3Obj> entityForMigS3Obj = null;
		try {
        	entityManager = this._entityManagerFactory.createEntityManager();
        	entityForMigS3Obj = this._dbEntityForMigS3ObjDAO.findByLocalPath(entityManager,migrationOid,localPath);
        } catch (Exception e) {
        	log.error("Failed to find entity", e);
        } finally{
        	if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
		return entityForMigS3Obj;
	}
}