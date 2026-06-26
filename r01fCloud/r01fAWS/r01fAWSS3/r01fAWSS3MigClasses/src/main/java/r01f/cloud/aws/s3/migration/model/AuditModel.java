package r01f.cloud.aws.s3.migration.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;

@MarshallType(as="audit")
@ConvertToDirtyStateTrackable
@AllArgsConstructor
@Accessors(prefix="_")
public class AuditModel implements Serializable{
	
	private static final long serialVersionUID = 6811283082734109884L;
	
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS                                
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="migrationOid",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private String _migrationOid;
	/**
	 * Origin Path
	 */
	@MarshallField(as="srcPath",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private String _srcPath;
	/**
	 * Local Tmp Path
	 */
	@MarshallField(as="localPath",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private String _localPath;
	/**
	 * Destination Path
	 */
	@MarshallField(as="dstPath",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private String _dstPath;
	/**
	 * Warning
	 */
	@MarshallField(as="warning",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private String _warning;
	/**
	 * Warning
	 */
	@MarshallField(as="error",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private String _error;
}
