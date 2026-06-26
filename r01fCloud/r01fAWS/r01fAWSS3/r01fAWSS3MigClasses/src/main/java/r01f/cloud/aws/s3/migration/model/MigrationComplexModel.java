package r01f.cloud.aws.s3.migration.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.objectstreamer.annotations.MarshallType;

@MarshallType(as="migrationComplex")
@ConvertToDirtyStateTrackable
@AllArgsConstructor
@Accessors(prefix="_")
public class MigrationComplexModel implements Serializable {
	
	private static final long serialVersionUID = 6811283082734109884L;
	
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS                                
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter @Setter private MigrationFromModel _migrationFromModel;
	@Getter @Setter private MigrationToModel _migrationToModel;
}
