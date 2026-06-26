package r01f.cloud.aws.s3.migration.guice;

import java.io.IOException;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import lombok.extern.slf4j.Slf4j;
import r01f.cloud.aws.s3.migration.db.dao.interfaces.R01TDBEntityForMigS3DAO;
import r01f.cloud.aws.s3.migration.db.dao.interfaces.R01TDBEntityForMigS3ObjDAO;
import r01f.cloud.aws.s3.migration.db.dao.interfaces.impl.R01TDBEntityForMigS3DAOImpl;
import r01f.cloud.aws.s3.migration.db.dao.interfaces.impl.R01TDBEntityForMigS3ObjDAOImpl;
import r01f.cloud.aws.s3.migration.model.MigrationConfigModel;
import r01f.cloud.aws.s3.migration.service.interfaces.MigS3ObjService;
import r01f.cloud.aws.s3.migration.service.interfaces.MigS3Service;
import r01f.cloud.aws.s3.migration.service.interfaces.MigrationAuditService;
import r01f.cloud.aws.s3.migration.service.interfaces.MigrationFromService;
import r01f.cloud.aws.s3.migration.service.interfaces.MigrationToService;
import r01f.cloud.aws.s3.migration.service.interfaces.impl.MigS3ObjServiceImpl;
import r01f.cloud.aws.s3.migration.service.interfaces.impl.MigS3ServiceImpl;
import r01f.cloud.aws.s3.migration.service.interfaces.impl.MigrationCsvAuditServiceImpl;
import r01f.cloud.aws.s3.migration.service.interfaces.impl.MigrationDBAuditServiceImpl;
import r01f.cloud.aws.s3.migration.service.interfaces.impl.MigrationFromHDFSServiceImpl;
import r01f.cloud.aws.s3.migration.service.interfaces.impl.MigrationFromLocalServiceImpl;
import r01f.cloud.aws.s3.migration.service.interfaces.impl.MigrationToS3ServiceImpl;
import r01f.filestore.api.FileStoreType;

/**
 * <p>Guice module for configuring the S3 migration services.</p>
 * <p>This module binds the different service interfaces to their concrete implementations
 * based on the provided {@link MigrationConfigModel}. It also provides the
 * {@link EntityManagerFactory} as a singleton for JPA-related services.</p>
 */
@Slf4j
public class S3GuiceModule extends AbstractModule {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
    private final MigrationConfigModel _migrationConfigModel;
    private final EntityManagerFactory _emf;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
    /**
     * Constructs an {@code S3GuiceModule} with the given migration configuration.
     * Initializes the {@link EntityManagerFactory} using the "migrationS3" persistence unit.
     *
     * @param migrationConfigModel The configuration model for the migration.
     */
    public S3GuiceModule(final MigrationConfigModel migrationConfigModel) {
        _migrationConfigModel = migrationConfigModel;
        _emf = Persistence.createEntityManagerFactory("migrationS3");
        log.info(">>>>> EntityManagerFactory is created");
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void configure() {
    	if(FileStoreType.HDFS.equals(_migrationConfigModel.getOriginFileStoreType())) {
    		bind(MigrationFromService.class).to(MigrationFromHDFSServiceImpl.class).in(Singleton.class);
    	} else {
    		bind(MigrationFromService.class).to(MigrationFromLocalServiceImpl.class).in(Singleton.class);
    	}
        bind(MigrationToService.class).to(MigrationToS3ServiceImpl.class).in(Singleton.class);
        bind(R01TDBEntityForMigS3DAO.class).to(R01TDBEntityForMigS3DAOImpl.class).in(Singleton.class);
        bind(R01TDBEntityForMigS3ObjDAO.class).to(R01TDBEntityForMigS3ObjDAOImpl.class).in(Singleton.class);
    }
    @Provides
    @Singleton
    public MigrationAuditService provideMigrationAuditService() throws IOException {
        if(_migrationConfigModel.getAuditImpl().equalsIgnoreCase("csv")) {
	    	return new MigrationCsvAuditServiceImpl(
	                _migrationConfigModel.getCsvDirectory().resolve("success.csv"),
	                _migrationConfigModel.getCsvDirectory().resolve("error.csv"),
	                _migrationConfigModel.getCsvDirectory().resolve("warning.csv")
	        );
        } else {
        	return new MigrationDBAuditServiceImpl(this._emf);
        }
    }
    @Provides
    @Singleton
    public MigS3Service provideMigS3Service() {
    	return new MigS3ServiceImpl(this._emf);
    }
    @Provides
    @Singleton
    public MigS3ObjService provideMigS3ObjService() {
    	return new MigS3ObjServiceImpl(this._emf);
    }
    /**
     * Closes the {@link EntityManagerFactory} when the application is shutting down.
     * This releases all resources held by the factory, such as database connections.
     * It's crucial to call this method to prevent resource leaks.
     */
    public void close() {
    	if(this._emf!=null) {
    		this._emf.close();
    		log.info(">>>>> EntityManagerFactory is closed");
    	}
    }
}