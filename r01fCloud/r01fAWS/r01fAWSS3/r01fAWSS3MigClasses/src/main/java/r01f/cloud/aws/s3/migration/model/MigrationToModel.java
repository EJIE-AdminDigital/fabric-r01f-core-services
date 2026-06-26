package r01f.cloud.aws.s3.migration.model;

import java.nio.file.Path;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.cloud.aws.s3.migration.model.MigrationCommonOIDs.MigrationOID;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;

@MarshallType(as="migrationTo")
@ConvertToDirtyStateTrackable
@Accessors(prefix="_")
public class MigrationToModel 
     extends MigrationModel{
	
	private static final long serialVersionUID = 6811283082734109884L;

/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS                                
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Destination Path
	 */
	@MarshallField(as="isSanitize",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private Boolean _isSanitize;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public MigrationToModel(MigrationOID migrationOid, Path srcPath, Path destPath, Boolean isSanitize) {
		super(migrationOid,srcPath,destPath);
		this._isSanitize = isSanitize;
	}
}
