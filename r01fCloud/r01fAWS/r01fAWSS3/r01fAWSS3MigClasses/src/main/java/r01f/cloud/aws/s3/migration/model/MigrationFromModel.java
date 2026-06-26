package r01f.cloud.aws.s3.migration.model;

import java.nio.file.Path;

import lombok.experimental.Accessors;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.cloud.aws.s3.migration.model.MigrationCommonOIDs.MigrationOID;
import r01f.objectstreamer.annotations.MarshallType;

@MarshallType(as="migrationFrom")
@ConvertToDirtyStateTrackable
@Accessors(prefix="_")
public class MigrationFromModel 
     extends MigrationModel{
	
	private static final long serialVersionUID = 6811283082734109884L;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public MigrationFromModel(MigrationOID migrationOid, Path srcPath, Path destPath) {
		super(migrationOid,srcPath,destPath);
	}
}
