package r01f.core.fileexplorer;

import java.util.ArrayList;
import java.util.Collection;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.core.fileexplorer.config.FileExplorerConfig;
import r01f.filestore.api.FileNameSanitizer;
import r01f.types.AppVersion;
import r01f.util.types.collections.CollectionUtils;
import r01f.util.types.collections.Lists;

/**
 * Creation:
 * <pre class='brush:java'>
 * 		// [1] - load the file-store config and file explorer config
 * 		FileExplorerConfig fileExplorerConfig = FileExplorerConfigLoader.loadFileExplorerConfigFrom(xmlProps);
 * 		FileExplorerStoreConfig storeConfig = FileExplorerConfigLoader.loadFileExplorerStoreConfigFrom(xmlProps);
 * 
 * 		// [2] - Create the store mediator
 * 		FileExplorerStoreMediator fsMediator = FileExplorerStoreMediator.createUsing(storeConfig);
 * 
 * 		// [3] - Create the storage
 * 		FileExplorerStorage storage = FileExplorerStorage.createFor(fileExplorerConfig,
 * 																	fsMediator);
 * </pre>
 * 
 * Once created, just get a [volume] and access the store
 * <pre class='brush:java'>
 * 		FileExplorerVolume vol = storage.getVolumeFor(volId);
 * 		FileProperties file = vol.getFileProperties(filePath);
 * </pre>
 */
@Accessors(prefix="_")
public class FileExplorerStorage {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final Collection<FileExplorerVolume> _volumes;
	
	@Getter @Setter private AppVersion _apiVersion = FileExplorerConstants.API_VERSION;
	@Getter @Setter private FileNameSanitizer _fileNameSanitizer;	// file name sanitizer: used when uploading files
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public FileExplorerStorage(final FileExplorerVolume... volumes) {
		this(Lists.newArrayList(volumes));
	}
	public FileExplorerStorage(final Collection<FileExplorerVolume> volumes) {
		Collection<FileExplorerVolume> theVolumes = new ArrayList<>(volumes.size());
		
		// set the volume ids
		FileExplorerVolumeID prevVolId = null;
		for (FileExplorerVolume vol : volumes) {
			FileExplorerVolumeID volId = prevVolId != null ? prevVolId.nextVolumeId()
														   : FileExplorerVolumeID.firstVolumeId();
			theVolumes.add(vol.cloneWithId(volId));
			
			prevVolId = volId;
		}
		_volumes = theVolumes;
	}
	public static FileExplorerStorage createFor(final FileExplorerConfig config,
												final FileExplorerStoreMediator fsMediator) {
		Collection<FileExplorerVolume> volumes = FileExplorerVolumeFactory.createVolumesFor(config,
																							fsMediator);
		return new FileExplorerStorage(volumes);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	VOLUME ACCESS
/////////////////////////////////////////////////////////////////////////////////////////
	public FileExplorerVolume getDefaultVolume() {
		return CollectionUtils.hasData(_volumes)
					? _volumes.stream()
							  .findFirst().orElse(null)
					: null;
	}
	public FileExplorerVolume getVolumeFor(final FileExplorerVolumeID volId) {
		return CollectionUtils.hasData(_volumes)
					? _volumes.stream()
							  .filter(vol -> vol.getId().is(volId))
							  .findFirst().orElse(null)
					: null;
	}
	public FileExplorerVolume getVolumeFor(final FileExplorerItemPathHash pathHash) {
		// Path hash is built as:
		//		{volumeId}_{localHash} 
		// where {localHash} is a hash created from the relative path from volume root
		// of a given path
		FileExplorerVolumeID volId = pathHash.getVolumeId();
		return this.getVolumeFor(volId);
	}
}
