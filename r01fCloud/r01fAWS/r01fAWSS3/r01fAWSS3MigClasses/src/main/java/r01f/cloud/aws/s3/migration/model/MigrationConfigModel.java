package r01f.cloud.aws.s3.migration.model;

import java.io.Serializable;
import java.nio.file.Path;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.filestore.api.FileStoreType;

@AllArgsConstructor
@Accessors(prefix="_")
public class MigrationConfigModel implements Serializable {
	
	private static final long serialVersionUID = 6811283082734109884L;
	
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS                                
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter @Setter private FileStoreType _originFileStoreType;
	@Getter @Setter private Path _workingDirectory;
	
	@Getter @Setter private String _auditImpl;
	@Getter @Setter private Path _csvDirectory;
	
	@Getter @Setter private List<MigrationConfigRemoteModel> _remotes;
	
	@Getter @Setter private boolean _isSanitize;
	
	@Getter @Setter private int _threadPool;
}
