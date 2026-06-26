package r01f.core.fileexplorer.command;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import r01f.core.fileexplorer.FileExplorerConstants;
import r01f.core.fileexplorer.FileExplorerItemPathHash;
import r01f.core.fileexplorer.FileExplorerStorage;
import r01f.core.fileexplorer.FileExplorerVolume;
import r01f.core.fileexplorer.command.response.FileExplorerCommandResponseForOpen;
import r01f.core.fileexplorer.command.response.FileExplorerCommandResponseObject;
import r01f.core.fileexplorer.command.response.FileExplorerFileStoreItem;
import r01f.file.FileProperties;
import r01f.objectstreamer.Marshaller;
import r01f.types.Path;
import r01f.util.types.collections.CollectionUtils;

/**
 * see: https://github.com/Studio-42/elFinder/wiki/Client-Server-API-2.1#open
 *
 * Opens information about a requested directory and its content.
 * Optionally, can return the directory tree as files and options for the current volume.
 *
 * @param init (true|false|not set) - Optional parameter. If true, indicates that this request is an initialization request.
 *                                  The response must include the value 'api' (number or string >= 2) and should include the options object, but it will still work without it.
 *                                  Also, this option affects the processing of the 'target' hash value. If init == true and target is not set or the directory doesn't exist, the data connector must return the root directory of the default volume.
 *                                  Otherwise, it must return an error: "File not found".
 * @param target (string) - Hash of the directory to open. Required if init == false or init is not set.
 * @param tree (true|false) - Optional. If true, the response must contain the top-level object of other volumes.
 *
 * @return A response object containing the requested information about the directory and its options.
 *
 * Response includes:
 * - api (Float): The version number of the protocol, must be >= 2.1. **ATTENTION** - Return api ONLY for init request!
 * - cwd (Object): Current Working Directory - Information about the current directory (File/Directory).
 * - files (Array): Array of objects - files and directories in the current directory. If 'tree' == true, added objects of other volumes to the root folder.
 *                  The order of files is not important.
 * - netDrivers (Array): List of network protocols that can be mounted on the fly (using 'netmount' command). Currently, only FTP is supported.
 *
 * Optional:
 * - uplMaxFile (Number): Maximum number of files allowed to be uploaded per request. For example, 20.
 * - uplMaxSize (String): Maximum file size allowed per upload request. For example, "32M".
 * - options (Object): Additional information about the folder and its volume.
 */
public class FileExplorerCommandForOpen 
	 extends FileExplorerCommandBase 
  implements FileExplorerCommand {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
	public FileExplorerCommandForOpen(final FileExplorerStorage storage,
									  final Marshaller marshaller) {
		super(storage,
			  marshaller);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * There are three possilbe states:
	 *     - Init with tartet parameter:
	 *     		- [<volName>_<path_hash>] -> for multiVolumeMode or singleModuleMode
	 *     		- [<path_hash>] -> fos singleModuleMode only
	 *     - Init without target parameter:
	 *     		- [null | empty] -> Initialized in default volume root
	 *     - No init parameter with (MANDATORY) target parameter
	 *     		- [<volId>_<path_hash>]
	 *     
	 */
	@Override
	public FileExplorerCommandResponseObject execute(final HttpServletRequest req) throws ServletException,
																						  IOException {

		boolean init = _toBoolean(req.getParameter(FileExplorerConstants.REQ_PARAMETER_INIT));
		boolean tree = _toBoolean(req.getParameter(FileExplorerConstants.REQ_PARAMETER_TREE));
		String target = req.getParameter(FileExplorerConstants.REQ_PARAMETER_TARGET);
		
		// validate
		_validateParameters(init, target);
		
		// resolve
		FileExplorerItemPathHash currentFolderPathHash = _resolveTarget(target);
		
		// create the returned data
		FileExplorerCommandResponseObject cmdResponse = this.execute(currentFolderPathHash,
									   						  		 init,tree);
		return cmdResponse;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EXPOSED FOR TESTING PURPOSES
/////////////////////////////////////////////////////////////////////////////////////////
	public FileExplorerCommandResponseForOpen execute(final FileExplorerItemPathHash currentFolderPathHash,
							  				  		  final boolean init,final boolean tree) throws IOException {
		
		// Create response
		FileExplorerCommandResponseForOpen outResponse = _initializeResponse(init);
		
		// Build tree volume items if necesary
		Set<FileExplorerFileStoreItem> fileStoreItems = new HashSet<>();
		if (tree) {
			fileStoreItems.addAll(_buildVolumeItems());
		}
		
		FileExplorerVolume currentVolume = _storage.getVolumeFor(currentFolderPathHash);
		Path currentFolderPath = currentFolderPathHash.getRelativePathFromRoot();
		FileExplorerFileStoreItem currentFlderItem = _buildCurrentFolderItems(currentVolume,currentFolderPath,
																			  fileStoreItems);
		
		_addAditionalOptions(outResponse, 
							 currentFlderItem,currentFolderPath);
		
		outResponse.setFiles(fileStoreItems);
		outResponse.setDebug(this.createDebugInfo());
		return outResponse;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	Private methods
/////////////////////////////////////////////////////////////////////////////////////////
	private void _validateParameters(final boolean init, 
									 final String target) throws IOException {
		// validate: target required if init == false
		if (!init && (target == null || target.isBlank())) 
			throw new IllegalStateException("[target] param is mandatory if init == false");
		
		// If init is false(only), validate the existence of the target path.
		// Note: When init is true and the target path does not exist, 
		//       it opens the root folder in the default volume.
		if (init == false && !_existsTarget(target))
			throw new FileNotFoundException("File not found for path hash = " + target);
	}
	private FileExplorerItemPathHash _resolveTarget(final String target) throws IOException {
		if (target == null || target.isBlank()) {
			return _storage.getDefaultVolume()
						   .getRootPathHash();
		}
		
		FileExplorerVolume volume = _findVolumeForTarget(target, _storage.getVolumes())
										.orElse(_storage.getDefaultVolume());
		
		String sanitizedTarget = _sanitizeTarget(target, volume);
		
		boolean existsTarget = _existsTarget(sanitizedTarget);
		
		FileExplorerItemPathHash resolvedPathHash = existsTarget ? FileExplorerItemPathHash.from(sanitizedTarget)
							 				  					  : _storage.getDefaultVolume()
							 				  								.getRootPathHash();
		return resolvedPathHash;
	}
	private boolean _existsTarget(final String target) throws IOException {
		FileExplorerItemPathHash pathHash = FileExplorerItemPathHash.from(target);
		FileExplorerVolume vol = _storage.getVolumeFor(pathHash);
		Path path = pathHash.getRelativePathFromRoot();
		// Exists if volume contains this path or is the root path
		return vol.existsFolder(path) || vol.getId().asString().equals(target);
    }
	private static String _sanitizeTarget(final String target,
										  final FileExplorerVolume volume) {
		if (target.startsWith(volume.getId().asString())) 
			return target;
		if (target.startsWith(volume.getName()))
			return target.replaceFirst("^"+volume.getName(),
									   volume.getId().asString());
		return volume.getId().asString()
					 .concat(target);
	}
	private static Optional<FileExplorerVolume> _findVolumeForTarget(final String target,
																	 final Collection<FileExplorerVolume> volumes) {
		return volumes.stream()
						.filter(volume -> target.startsWith(volume.getId().asString()) || target.startsWith(volume.getName()))
						.collect(collectingAndThen(
									toList(),
									filteredList -> {
										if (filteredList.size() > 1) throw new IllegalStateException("Ambiguous target resolution");
										return filteredList.stream()
														   .findFirst();
									}));
	}
	private FileExplorerCommandResponseForOpen _initializeResponse(final boolean init) {
		FileExplorerCommandResponseForOpen response = new FileExplorerCommandResponseForOpen();
		if (init) {
			response.setApi(_storage.getApiVersion() != null ? _storage.getApiVersion().asString()
															 : FileExplorerConstants.API_VERSION.asString());
			response.setNetDrivers(null);	// no network drives
		}
		return response;
	}
	private Set<FileExplorerFileStoreItem> _buildVolumeItems() throws IOException {
		Set<FileExplorerFileStoreItem> volumeItems = new HashSet<>();
		for (FileExplorerVolume volume : _storage.getVolumes()) {
			// create a [file item] for the [volume]
			FileExplorerFileStoreItem rootItem = FileExplorerFileStoreItem.itemBuilderForVolume(volume)
																		  .forItem(volume.getRootProperties())
																		  .build();
			rootItem.setName(volume.getAlias());
			volumeItems.add(rootItem);
			
			// add the volume sub-items
			Collection<FileProperties> rootChildItems = volume.listChildren(volume.getRootPath());
			Collection<FileExplorerFileStoreItem> rootChild = FileExplorerFileStoreItem.itemBuilderForVolume(volume)
																					   .forItems(rootChildItems)
																					   .build();
			volumeItems.addAll(rootChild);
		}
		return volumeItems;
	}
	private FileExplorerFileStoreItem _buildCurrentFolderItems(final FileExplorerVolume volume,final Path currentFolderPath,
															   final Set<FileExplorerFileStoreItem> items) throws IOException {
		FileProperties currentFolderFile = volume.getItemProperties(currentFolderPath);
		FileExplorerFileStoreItem currentFolderItem = FileExplorerFileStoreItem.itemBuilderForVolume(volume)
																						 .forItem(currentFolderFile)
																						 .build();
		items.add(currentFolderItem);
		
		if (!volume.isRoot(currentFolderPath)) {
			Collection<FileProperties> childItems = volume.listChildren(currentFolderPath);
			if (CollectionUtils.hasData(childItems)) {
				childItems.stream()
						  .map(childItem -> FileExplorerFileStoreItem.itemBuilderForVolume(volume)
																	 .forItem(childItem)
																	 .build())
						  .forEach(items::add);
			} 
		}
		return currentFolderItem;
	}
	private void _addAditionalOptions(final FileExplorerCommandResponseForOpen response,
									  final FileExplorerFileStoreItem cwd, Path currentFolderPath) {
		response.setCwd(cwd);
		if (currentFolderPath != null) {
			response.setOptions(this.getOptionsFor(currentFolderPath));
		}
		response.setTextMimes(FileExplorerConstants.DEFAULT_TEXT_MIMES);
	}
}


/////////////////////////////////////////////////////////////////////////////////////////
//	Full documentation for "open"
/////////////////////////////////////////////////////////////////////////////////////////

/**
 * Opens information about a requested directory and its content.
 * Optionally, can return the directory tree as files and options for the current volume.
 *
 * @param init (true|false|not set) - Optional parameter. If true, indicates that this request is an initialization request.
 *                                  The response must include the value 'api' (number or string >= 2) and should include the options object, but it will still work without it.
 *                                  Also, this option affects the processing of the 'target' hash value. If init == true and target is not set or the directory doesn't exist, the data connector must return the root directory of the default volume.
 *                                  Otherwise, it must return an error: "File not found".
 * @param target (string) - Hash of the directory to open. Required if init == false or init is not set.
 * @param tree (true|false) - Optional. If true, the response must contain the top-level object of other volumes.
 *
 * @return A response object containing the requested information about the directory and its options.
 *
 * Response includes:
 * - api (Float): The version number of the protocol, must be >= 2.1. **ATTENTION** - Return api ONLY for init request!
 * - cwd (Object): Current Working Directory - Information about the current directory (File/Directory).
 * - files (Array): Array of objects - files and directories in the current directory. If 'tree' == true, added objects of other volumes to the root folder.
 *                  The order of files is not important.
 * - netDrivers (Array): List of network protocols that can be mounted on the fly (using 'netmount' command). Currently, only FTP is supported.
 *
 * Optional:
 * - uplMaxFile (Number): Maximum number of files allowed to be uploaded per request. For example, 20.
 * - uplMaxSize (String): Maximum file size allowed per upload request. For example, "32M".
 * - options (Object): Additional information about the folder and its volume.
 *
 * Example structure of the 'options' object:
 * {
 *   "path": "files/folder42",                        // (String) Current folder path
 *   "url": "http://localhost/elfinder/files/",        // (String) Current folder URL
 *   "tmbURL": "http://localhost/elfinder/files/.tmb/", // (String) Thumbnails folder URL
 *   "separator": "/",                                // (String) Path separator for the current volume
 *   "disabled": [],                                  // (Array) List of commands not allowed (disabled) on this volume
 *   "copyOverwrite": 1,                              // (Number) Whether to overwrite files with the same name when copying
 *   "uploadOverwrite": 1,                            // (Number) Whether to overwrite files with the same name when uploading
 *   "uploadMaxSize": 1073741824,                     // (Number) Max file size for uploads
 *   "uploadMaxConn": 3,                              // (Number) Maximum chunked upload connections. '-1' to disable chunked upload
 *   "uploadMime": {                                  // (Object) MIME type checker for uploads
 *     "allow": ["image", "text/plain"],              // (Array) Allowed MIME types
 *     "deny": ["all"],                               // (Array) Denied MIME types
 *     "firstOrder": "deny"                           // (String) First order to check ("deny" or "allow")
 *   },
 *   "dispInlineRegex": "^(?:image|text/plain$)",      // (String) Regex for inline MIME types with 'file' command
 *   "jpgQuality": 100,                               // (Number) JPEG quality for image resize/crop/rotate (1-100)
 *   "syncChkAsTs": 1,                               // (Number) Whether the volume can detect updates by timestamp
 *   "syncMinMs": 30000,                             // (Number) Minimum interval in milliseconds for auto-sync
 *   "uiCmdMap": { "chmod": "perm" },                 // (Object) Command conversion map for the volume (e.g., chmod(ui) to perm(connector))
 *   "i18nFolderName": 1,                             // (Number) Whether to enable i18n folder names
 *   "archivers": {                                  // (Object) Archive settings
 *     "create": ["application/zip", "application/x-tar", "application/x-gzip"],   // (Array) Allowed archive types for creation
 *     "extract": ["application/zip", "application/x-tar", "application/x-gzip"],  // (Array) Allowed archive types for extraction
 *     "createext": { "application/zip": "zip", "application/x-tar": "tar", "application/x-gzip": "tgz" } // (Object) Map MIME type to file extension
 *   }
 * }
 *
 * debug (Object) - Debugging information (if specified in the connector options):
 * {
 *   "connector": "php",                   // (String) Connector type
 *   "phpver": "5.3.6",                    // (String) PHP version
 *   "time": 0.0749430656433,              // (Number) Execution time
 *   "memory": "3348Kb / 2507Kb / 128M",   // (String) Used / Free / Available memory
 *   "volumes": [                          // (Array) Debugging by volume
 *     {
 *       "id": "l1_",                     // (String) Volume ID
 *       "driver": "localfilesystem",     // (String) Driver type (class name)
 *       "mimeDetect": "internal",        // (String) Method for detecting MIME type
 *       "imgLib": "gd"                   // (String) Image library used
 *     }
 *   ],
 *   "mountErrors": [                     // (Array) List of mount errors for volumes
 *     "Root folder has not read and write permissions."
 *   ]
 * }
 */
