package r01f.cloud.aws.s3.migration.model;

import java.io.Serializable;
import java.nio.file.Path;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.cloud.aws.s3.migration.model.MigrationCommonOIDs.MigrationOID;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;

@MarshallType(as="migration")
@ConvertToDirtyStateTrackable
@AllArgsConstructor
@Accessors(prefix="_")
public abstract class MigrationModel implements Serializable{
	
	private static final long serialVersionUID = 6811283082734109884L;
	
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS                                
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="migrationOid",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private MigrationOID _migrationOid;
	/**
	 * Origin Path
	 */
	@MarshallField(as="srcPath",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private Path _srcPath;
	/**
	 * Destination Path
	 */
	@MarshallField(as="dstPath",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private Path _dstPath;
}
