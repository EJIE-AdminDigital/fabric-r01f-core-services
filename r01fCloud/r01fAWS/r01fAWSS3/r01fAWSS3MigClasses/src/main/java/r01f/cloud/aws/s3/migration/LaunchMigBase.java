package r01f.cloud.aws.s3.migration;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.google.inject.Guice;
import com.google.inject.Injector;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.cloud.aws.s3.migration.guice.S3GuiceModule;
import r01f.cloud.aws.s3.migration.model.MigrationCommonOIDs.MigrationOID;
import r01f.cloud.aws.s3.migration.model.MigrationComplexModel;
import r01f.cloud.aws.s3.migration.model.MigrationConfigModel;
import r01f.cloud.aws.s3.migration.model.MigrationConfigRemoteModel;
import r01f.cloud.aws.s3.migration.model.MigrationFromModel;
import r01f.cloud.aws.s3.migration.model.MigrationToModel;
import r01f.cloud.aws.s3.migration.service.interfaces.MigS3ObjService;
import r01f.cloud.aws.s3.migration.service.interfaces.MigS3Service;
import r01f.cloud.aws.s3.migration.service.interfaces.MigrationFromService;
import r01f.cloud.aws.s3.migration.service.interfaces.MigrationToService;
import r01f.filestore.api.FileStoreType;
import r01f.guids.CommonOIDs.AppCode;
import r01f.xmlproperties.XMLProperties;
import r01f.xmlproperties.XMLPropertiesBuilder;
import r01f.xmlproperties.XMLPropertiesForApp;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

/**
 * <p>
 * Abstract base class for launching data migration processes.
 * This class provides the common setup and execution flow for migrations,
 * including configuration loading, service injection using Guice, and
 * preparing the migration models. Subclasses must implement the
 * {@link #execute(MigrationFromModel, MigrationToModel)} method to define
 * the specific migration logic.
 * </p>
 */
@Slf4j
@Accessors(prefix="_")
public abstract class LaunchMigBase {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////	
	@Getter @Setter protected MigrationConfigModel _migrationConfigModel;
	
	@Getter @Setter protected MigrationFromService _migrationFromService;
	@Getter @Setter protected MigrationToService _migrationToService;
	@Getter @Setter protected MigS3Service _migService;
	@Getter @Setter protected MigS3ObjService _migObjService;
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////   
	
	public void launchMig() throws URISyntaxException, IOException {
		this.launchMig(null);
	}
	
	/**
     * Initiates the migration process.
     * This method performs the following steps:
     * <ol>
     *     <li>Loads the migration configuration from an XML properties file.</li>
     *     <li>Validates the working directory specified in the configuration.</li>
     *     <li>Prepares {@link MigrationFromModel} and {@link MigrationToModel} instances based on the configuration.</li>
     *     <li>Configures and initializes Guice injector to provide instances of {@link MigrationFromService} and {@link MigrationToService}.</li>
     *     <li>Calls the abstract {@link #execute(MigrationFromModel, MigrationToModel)} method, which must be implemented by subclasses
     *         to perform the actual migration logic.</li>
     * </ol>
     * If the configuration is invalid or the working directory does not exist, the application will exit.
     *
     * @param args Command-line arguments (currently not used directly within this method, but part of the standard main method signature).
     * @throws URISyntaxException If a URI syntax error occurs during path construction.
     * @throws IOException If an I/O error occurs, for example, when checking directory existence.
     */
	public void launchMig(String migrationOid) throws URISyntaxException, IOException {
		log.info("############################ BEGIN ############################");
		//VALIDATION
		_migrationConfigModel = _buildMigrationConfigModel();
		if (_migrationConfigModel == null) {
            log.error("Usage: java -jar s3-migrator.jar not configure config xml");
            System.exit(1);
        }
		
		//PREPARE MIGRATION ARGS
        if (!Files.exists(_migrationConfigModel.getWorkingDirectory()) || !Files.isDirectory(_migrationConfigModel.getWorkingDirectory())) {
            log.error("The provided source directory does not exist or is not a directory: " + _migrationConfigModel.getWorkingDirectory());
            System.exit(2);
        }
        List<MigrationComplexModel> migrationModelList = new ArrayList<MigrationComplexModel>();
        for(MigrationConfigRemoteModel remote: _migrationConfigModel.getRemotes()) {
        	Path remotePathDir = Paths.get(remote.getRemoteRootDirectory().toString(), 
        							   	   remote.getRemoteFolderDirectory().toString()); // Remote
	        Path localPathDir = Paths.get(_migrationConfigModel.getWorkingDirectory().toString(), 
	        							  remote.getRemoteFolderDirectory().toString()); // Local	
	        MigrationOID oid = null;
	        if(migrationOid == null) {
	        	oid = new MigrationOID(r01f.types.Path.from(remote.getRemoteFolderDirectory()).asAbsoluteString() 
	        					  	   + "-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
	        } else {
	        	oid = new MigrationOID(migrationOid);
	        }
	        migrationModelList.add(new MigrationComplexModel(new MigrationFromModel(oid,
	        															  			remotePathDir,
	        															  			localPathDir), 
			        									   	 new MigrationToModel(oid,
			        															  localPathDir,
			        															  remote.getRemoteFolderDirectory(),
			        															  _migrationConfigModel.isSanitize())));
        } 
        //GUICE CONFIGURATION
        S3GuiceModule guiceModule = new S3GuiceModule(_migrationConfigModel);
        Injector injector = Guice.createInjector(guiceModule);
        
        this._migrationFromService = injector.getInstance(MigrationFromService.class);
        this._migrationToService = injector.getInstance(MigrationToService.class);
        this._migService = injector.getInstance(MigS3Service.class);
        this._migObjService = injector.getInstance(MigS3ObjService.class);
    
        this.execute(migrationModelList);
        
        guiceModule.close();
        log.info("############################ END ############################");
	}
	/**
     * Abstract method to be implemented by subclasses, defining the specific migration logic.
     * This method receives the prepared migration models and should orchestrate the data transfer
     * or transformation based on the application's requirements.
     *
     * @param migrationModelList The model containing details about the migration model.
     * @throws URISyntaxException If a URI syntax error occurs during path construction within the implementation.
     * @throws IOException If an I/O error occurs during the migration process.
     */
	protected abstract void execute(List<MigrationComplexModel> migrationModelList) throws URISyntaxException, IOException;
	/**
     * Builds a {@link MigrationConfigModel} by reading configuration properties from an XML file.
     * The configuration is expected to be located under the "r01f" application code and "migration" component.
     * It parses various properties such as origin file store type, working directory, audit implementation details,
     * remote root and folder directories, and a sanitize flag.
     *
     * @return A fully populated {@link MigrationConfigModel} instance, or null if essential properties are missing or invalid.
     */
	private MigrationConfigModel _buildMigrationConfigModel() {
		//PROPERTIES
		XMLProperties xmlProps = XMLPropertiesBuilder.create()
													 .notUsingCache();
		XMLPropertiesForApp testProps = xmlProps.forApp(AppCode.forId("r01f"));
		XMLPropertiesForAppComponent props = testProps.forComponent("migration");
		
		String propsRootNode = "/moduleConfigForMigration";
		String originFileStoreTypeStr = props.propertyAt(propsRootNode + "/originSystem")
					  	 	 	   			 .asString("hdfs");
		FileStoreType originFileStoreType = FileStoreType.HDFS;
		if(FileStoreType.HDFS.getCode().equalsIgnoreCase(originFileStoreTypeStr)) {
        	originFileStoreType = FileStoreType.HDFS;
        } else {
            originFileStoreType = FileStoreType.LOCAL;
        }
		String workingDirectory = props.propertyAt(propsRootNode + "/workingDirectory")
					  	 	 		   .asString();
		String auditImpl = props.propertyAt(propsRootNode + "/audit/@impl")
					  	 	 	.asString("csv");
		String csvDirectory = "";
		if("csv".equalsIgnoreCase(auditImpl)) {
			csvDirectory = props.propertyAt(propsRootNode + "/audit[@impl='csv']/csvDirectory")
					  	 	 	.asString();
		}
		//Remotes
		String remoteRootDirectory = props.propertyAt(propsRootNode + "/remote/origin/rootDirectory")
										  .asString();
		List<String> remoteFolderPaths = props.propertyAt(propsRootNode + "/remote/origin/folders")
											  .asListOfStrings();
        List<MigrationConfigRemoteModel> remotes = new ArrayList<MigrationConfigRemoteModel>();
		for(String remoteFolderDirectoryAux: remoteFolderPaths) {
			remotes.add(new MigrationConfigRemoteModel(Path.of(remoteRootDirectory), Path.of(remoteFolderDirectoryAux)));
		}
		//Sanitize
		boolean isSanitize = props.propertyAt(propsRootNode + "/isSanitize")
					  	 	 	  .asBoolean();
		//Number of threads
		int threadPool = props.propertyAt(propsRootNode + "/threadPool")
					  	 	  .asInteger(3);
		return new MigrationConfigModel(originFileStoreType,
										Path.of(workingDirectory),
										auditImpl,
										Path.of(csvDirectory),
										remotes,
										isSanitize,
										threadPool);
	}
}
