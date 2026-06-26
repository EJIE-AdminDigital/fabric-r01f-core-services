package r01f.cloud.aws.s3.migration.service.interfaces.impl;

import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.cloud.aws.s3.migration.db.entities.R01TDBEntityForMigS3Obj;
import r01f.cloud.aws.s3.migration.model.AuditModel;
import r01f.cloud.aws.s3.migration.service.interfaces.MigS3ObjService;
import r01f.cloud.aws.s3.migration.service.interfaces.MigrationAuditService;

/**
 * Service class responsible for logging S3 migration results (success, error, warning)
 * to a database.
 *
 * <p>
 * This implementation uses JPA's {@link EntityManager} to interact with the database
 * through a {@link R01TDBEntityForMigS3ObjObjDAO} instance. Each audit operation
 * (success, error, warning) is performed within its own transaction.
 * </p>
 *
 * <p>
 * The class is marked as {@code @Accessors(prefix="_")} to work with Lombok's
 * getter/setter generation for fields prefixed with an underscore.
 * </p>
 *
 * @see MigrationAuditService
 * @see R01TDBEntityForMigS3ObjObjDAO
 * @see R01TDBEntityForMigS3Obj
 * @see AuditModel
 */
@Accessors(prefix="_")
public class MigrationDBAuditServiceImpl implements MigrationAuditService {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter @Setter private MigS3ObjService _migS3ObjService;
	@Getter @Setter private EntityManagerFactory _entityManagerFactory;	
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
    /**
     * Constructs a new {@code MigrationDBAuditServiceImpl} with the provided {@link EntityManagerFactory}.
     * This constructor initializes the internal {@link MigS3ObjService} using the given
     * {@link EntityManagerFactory}.
     *
     * @param entityManagerFactory The {@link EntityManagerFactory} to be used for database operations. Must not be {@code null}.
     */
    public MigrationDBAuditServiceImpl(EntityManagerFactory entityManagerFactory) {
    	_entityManagerFactory = entityManagerFactory;
    	_migS3ObjService = new MigS3ObjServiceImpl(entityManagerFactory);
    }
    
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////   
    @Override
	public void auditSuccess(AuditModel audit) {
        this._migS3ObjService.save(this._transform(audit));
    }
    @Override
	public void auditError(AuditModel audit) {
        this._migS3ObjService.save(this._transform(audit));
    }
    @Override
	public void auditWarning(AuditModel audit) {
     	this._migS3ObjService.save(this._transform(audit));
    }
	/**
     * Transforms an {@link AuditModel} object into an {@link R01TDBEntityForMigS3Obj} entity.
     * This private helper method maps the audit details to the corresponding database entity fields.
     *
     * @param audit The {@link AuditModel} to transform. Must not be {@code null}.
     * @return A new {@link R01TDBEntityForMigS3Obj} entity populated with data from the audit model.
     */	
	private R01TDBEntityForMigS3Obj _transform(AuditModel audit){
		R01TDBEntityForMigS3Obj newEntity = new R01TDBEntityForMigS3Obj();
		Optional<R01TDBEntityForMigS3Obj> existingEntity = _migS3ObjService.findByLocalPath(
            audit.getMigrationOid(),
            audit.getLocalPath()
        );
		if (existingEntity.isPresent()) {
            newEntity.setOid(existingEntity.get().getOid());
            newEntity.setSourcePath(existingEntity.get().getSourcePath());
        } else {
        	newEntity.setSourcePath(audit.getSrcPath());
        }
        newEntity.setMigrationOid(audit.getMigrationOid());
        newEntity.setLocalPath(audit.getLocalPath());
        newEntity.setDestinationPath(audit.getDstPath());
        newEntity.setWarning(audit.getWarning());
        newEntity.setError(audit.getError());
        return newEntity;
	}
}