package r01f.core.fileexplorer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.regex.Pattern;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.core.fileexplorer.config.FileExplorerVolumeSpec;
import r01f.file.FileProperties;
import r01f.file.FilePropertiesBase;
import r01f.filestore.api.FileFilter;
import r01f.mime.MimeType;
import r01f.types.Path;


@Accessors(prefix="_")
public class FileExplorerVolume {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final FileExplorerVolumeID _id;
	@Getter private final FileExplorerVolumeSpec _volSpec;
	@Getter private final FileExplorerStoreMediator _fsMediator;
	
	@Getter private final Path _rootPath;
	@Getter private final FileExplorerItemPathHash _rootPathHash;

/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public FileExplorerVolume(final FileExplorerVolumeSpec volSpec,
				   	   		  final FileExplorerStoreMediator fsMediator) throws IOException {
		this(null,volSpec,	// no id
			 fsMediator);
	}
	public FileExplorerVolume(final FileExplorerVolumeID id,final FileExplorerVolumeSpec volSpec,
				   	   		  final FileExplorerStoreMediator fsMediator) throws IOException {
		if (!volSpec.isValid()) throw new IllegalArgumentException("[File Explorer] volume config is NOT valid because " + volSpec.validate()
																														 		.asNOKValidationResult()
																														 		.getReason());
		_id = id != null ? id : FileExplorerVolumeID.firstVolumeId();
		_volSpec = volSpec;
		_fsMediator = fsMediator;
		
		// create the root folder if it does not exist
		_rootPath = fsMediator.getStoreRootPath()
							  .joinedWith(volSpec.getPath());
		if (!this.existsFolder(_rootPath)) this.createFolder(_rootPath);
		
		// root path hash
		_rootPathHash = FileExplorerItemPathHash.from(_id,_rootPath);
	}
	private FileExplorerVolume(final FileExplorerVolumeID id,final FileExplorerVolumeSpec config,
							   final FileExplorerStoreMediator fsMediator,
							   final Path rootPath,final FileExplorerItemPathHash rootPathHash) {
		_id = id;
		_volSpec = config;
		_fsMediator = fsMediator;
		_rootPath = rootPath;
		_rootPathHash = rootPathHash;
	}
	public FileExplorerVolume cloneWithId(final FileExplorerVolumeID id) {
		return new FileExplorerVolume(id,_volSpec,
									  _fsMediator,
									  _rootPath,_rootPathHash);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	CONFIG
/////////////////////////////////////////////////////////////////////////////////////////
	public String getName() {
		return _volSpec.getName();
	}
	public String getAlias() {
		return _volSpec.getAlias();
	}
	public String getTmpDir() {
		return _volSpec.getTmpDir();
	}
	public Pattern getSecurityPattern() {
		return _volSpec.getSecurityPattern();
	}
	public boolean isLocked() {
		return _volSpec.isLocked();
	}
	public boolean isReadable() {
		return _volSpec.isReadable();
	}
	public boolean isWritable() {
		return _volSpec.isWritable();
	}
	public boolean isReadOnlyFile(final Path path) {
		Path absPath = this.getAbsolutePathOf(path);
		return _volSpec.isReadOnlyFile(absPath);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	HASH
/////////////////////////////////////////////////////////////////////////////////////////
	public FileExplorerItemPathHash itemPathHashOf(final FileProperties itemProps) {
		return this.itemPathHashOf(itemProps.getPath());
	}
	public FileExplorerItemPathHash itemPathHashOf(final Path path) {
		// compose the hash as {volumeId}_{localHash}
		// beware that the local hash MUST be created using the relative path from volume root
		Path relativePath = this.getRelativePathFromRootOf(path);	// beware!! relative path
		return FileExplorerItemPathHash.from(_id,relativePath);
	}
	public Path relativePathFromRootOf(final FileExplorerItemPathHash relPathHash) {
		return relPathHash.getRelativePathFromRoot();
	}
	public Path absolutePathOf(final FileExplorerItemPathHash relPathHash) {
		Path relPath = this.relativePathFromRootOf(relPathHash);
		return this.getRootPath()
				   .joinedWith(relPath);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	PATH
/////////////////////////////////////////////////////////////////////////////////////////	
	public boolean isRoot(final Path path) {
		Path rootPath = this.getRootPath();
		return rootPath != null && path != null ? rootPath.is(path)
												: (rootPath != null && path == null)
												  ||
												  (rootPath == null && path != null) ? false
												   									 : true;	// both null
	}
	public Path getParentPathOf(final Path path) {
		Path absPath = this.getAbsolutePathOf(path);
		return absPath.getParentFolderPath();
	}
	public Path getRelativePathFromRootOf(final Path path) {
		Path absPath = this.getAbsolutePathOf(path);
		return absPath.remainingPathFrom(this.getRootPath());
	}
	public Path getRelativePathFromRootOf(final FileProperties itemProps) {
		return this.getRelativePathFromRootOf(itemProps.getPath());
	}
	public Path getAbsolutePathOf(final Path path) {
		Path outAbsPath = null;
		if (!this.isRoot(path)
		 && !path.startsWith(this.getRootPath())) {
			outAbsPath = this.getRootPath()
							 .joinedWith(path);
		} else {
			outAbsPath = path;
		}
		return outAbsPath;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	CRUD
/////////////////////////////////////////////////////////////////////////////////////////
	public void createFile(final Path path) throws IOException {
		Path absPath = this.getAbsolutePathOf(path);
		_fsMediator.createFile(absPath);
	}
	public void createFolder(final Path path) throws IOException {
		Path absPath = this.getAbsolutePathOf(path); 
		_fsMediator.createFolder(absPath);
	}
	public void deleteFile(final Path path) throws IOException {
		Path absPath = this.getAbsolutePathOf(path);
		_fsMediator.deleteFile(absPath);
	}
	public void deleteFolder(final Path path) throws IOException {
		Path absPath = this.getAbsolutePathOf(path);
		_fsMediator.deleteFolder(absPath);
	}
	public void rename(final Path origin,final Path destination) throws IOException {
		Path absOriginPath = this.getAbsolutePathOf(origin);
		Path absDestinationPath = this.getAbsolutePathOf(destination);
		_fsMediator.rename(absOriginPath,absDestinationPath);
	}	
	public boolean existsFile(final Path path) throws IOException {
		Path absPath = this.getAbsolutePathOf(path);
		return _fsMediator.existsFile(absPath);
	}
	public boolean existsFolder(final Path path) throws IOException {
		Path absPath = this.getAbsolutePathOf(path);
		return _fsMediator.existsFolder(absPath);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	COPY
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean copyFile(final Path srcPath,
							final Path dstPath,
							final boolean overwrite) throws IOException {
		Path absPathSrc = this.getAbsolutePathOf(srcPath);
		Path absPathDst = this.getAbsolutePathOf(dstPath);
		return _fsMediator.copyFile(absPathSrc, absPathDst, overwrite);
	}
	public boolean copyFolder(final Path srcPath,
							  final Path dstPath,
							  final FileFilter fileFilter,
							  final boolean overwrite) throws IOException {
		Path absPathSrc = this.getAbsolutePathOf(srcPath);
		Path absPathDst = this.getAbsolutePathOf(dstPath);
		return _fsMediator.copyFolder(absPathSrc, absPathDst, fileFilter, overwrite);
	}
	public boolean moveFolder(final Path srcPath,
							  final Path dstPath,
							  final boolean overwrite) throws IOException {
		Path absPathSrc = this.getAbsolutePathOf(srcPath);
		Path absPathDst = this.getAbsolutePathOf(dstPath);
		return _fsMediator.moveFolder(absPathSrc, absPathDst, overwrite);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	METADATA
/////////////////////////////////////////////////////////////////////////////////////////
	public FileProperties getRootProperties() {
		FileProperties outFile = new FilePropertiesBase() {
										private static final long serialVersionUID = 1L;
								 };
		outFile.setPath(this.getRootPath());
		outFile.setFolder(true);
		return outFile;
	}
	public FileProperties getItemProperties(final Path path) throws IOException {
		if (this.getRootPath().is(path)) return this.getRootProperties();
		
		Path absPath = this.getAbsolutePathOf(path);
		return _fsMediator.getFileProperties(absPath);
	}
	public MimeType getMimeType(final FileProperties fileProperties) throws IOException {
		return FileExplorerMimeTypeDetector.detect(fileProperties);
	}
	public MimeType getMimeType(final InputStream is) throws IOException {
		return FileExplorerMimeTypeDetector.detect(is);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	CHILD
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean hasChildFolder(final Path path) throws IOException {
		Path absPath = this.getAbsolutePathOf(path);
		return _fsMediator.hasChildFolder(absPath);
	}
	public Collection<FileProperties> listChildren(final Path path) throws IOException {
		Path absPath = this.getAbsolutePathOf(path);
		return _fsMediator.listChildren(absPath);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	INPUT / OUTPUT
/////////////////////////////////////////////////////////////////////////////////////////
	public InputStream openInputStream(final Path path) throws IOException {
		Path absPath = this.getAbsolutePathOf(path);
		return _fsMediator.openInputStream(absPath);
	}
	public OutputStream openOutputStream(final Path path) throws IOException {
		Path absPath = this.getAbsolutePathOf(path);
		return _fsMediator.openOutputStream(absPath);
	}
	public OutputStream openOutputStreamForAppending(final Path path) throws IOException {
		Path absPath = this.getAbsolutePathOf(path);
		return _fsMediator.openOutputStreamForAppending(absPath);		
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	READ / WRITE
/////////////////////////////////////////////////////////////////////////////////////////
	public void write(final Path path,
					  final InputStream srcIS) throws IOException {
		Path absPath = this.getAbsolutePathOf(path);
		_fsMediator.write(absPath,srcIS);
	}
	public void append(final Path path,
					   final InputStream srcIS) throws IOException {
		Path absPath = this.getAbsolutePathOf(path);
		_fsMediator.append(absPath,srcIS);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	SEARCH
/////////////////////////////////////////////////////////////////////////////////////////
	public Collection<FileProperties> search(final Path startingPath,
											 final String query,final Collection<MimeType> mimes) throws IOException {
		Path absPath = this.getAbsolutePathOf(startingPath);
		return _fsMediator.search(absPath,
								  query,mimes);
	}
}