package r01f.cloud.aws.s3.migration.model;

import java.io.Serializable;
import java.nio.file.Path;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Accessors(prefix="_")
public class MigrationConfigRemoteModel implements Serializable{
	
	private static final long serialVersionUID = -5043332274742771481L;

/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS                                
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter @Setter private Path _remoteRootDirectory;
	@Getter @Setter private Path _remoteFolderDirectory;
}
