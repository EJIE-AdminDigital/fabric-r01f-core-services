package r01f.filestore.api.hdfs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;

import com.google.common.collect.Lists;

import r01f.file.FileProperties;
import r01f.file.util.FolderWalker;
import r01f.filestore.api.FileFilter;
import r01f.filestore.api.FileFilters;
import r01f.util.types.collections.CollectionUtils;

/**
 * Recursive file listing under a specified directory.
 */
final class HDFSFolderRecursiveListing 
	extends FolderWalker<FileProperties> {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private final FileSystem _hdfsFileSystem;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public HDFSFolderRecursiveListing(final FileSystem hdfsFileSystem,
									  final FileFilter filter) {
		super(filter,
			  -1);		// no depth limit
		_hdfsFileSystem = hdfsFileSystem;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Recursively walk a directory tree and return a {@link List} of all
	 * @param aStartingDir is a valid directory, which can be read.
	 */
	public Collection<FileProperties> recurseFolderContents(final Path startingFolder) throws IOException {
		Stream<FileProperties> stream = this.recurseFolderContentsStream(startingFolder);
		return stream.collect(Collectors.toList());
	}
	/**
	 * Recursively walk a directory tree and return a {@link Stream} of all
	 * @param aStartingDir is a valid directory, which can be read.
	 */
	public Stream<FileProperties> recurseFolderContentsStream(final Path startingFolder) throws IOException {
		_validateDirectory(startingFolder);
		
		Stream.Builder<FileProperties> streamBuilder = Stream.builder();
		this.walk(HDFSFileStoreAPIBase.hdfsPathToR01FPath(startingFolder),
				  streamBuilder);
		return streamBuilder.build();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected FileProperties getFolderProperties(final r01f.types.Path folderPath) throws IOException {
		Path hdfsPath = HDFSFileStoreAPIBase.r01fPathToHDFSPath(folderPath);
		FileStatus hdfsFile = _hdfsFileSystem.getFileStatus(hdfsPath);
		if (hdfsFile != null
		 && hdfsFile.isDirectory()) {
			return HDFSFileProperties.from(hdfsFile);
		} 
		throw new IllegalArgumentException(folderPath + " is NOT a folder!");
	}
	@Override
	protected Collection<FileProperties> listFolderContents(final r01f.types.Path folderPath,
															final FileFilter filter) throws IOException {
		PathFilter hdfsPathFilter = null;
		if (filter != null) {
			hdfsPathFilter = hdfsPath -> {
								r01f.types.Path path = HDFSFileStoreAPIBase.hdfsPathToR01FPath(hdfsPath);
								return filter.accept(path);				
							 };
		}
		FileStatus[] hdfsFiles = null;
		if (hdfsPathFilter != null) {
			hdfsFiles = _hdfsFileSystem.listStatus(HDFSFileStoreAPIBase.r01fPathToHDFSPath(folderPath),
									   			   hdfsPathFilter);
		} else {
			hdfsFiles = _hdfsFileSystem.listStatus(HDFSFileStoreAPIBase.r01fPathToHDFSPath(folderPath));
		}
		return hdfsFiles != null ? Stream.of(hdfsFiles)
										 .map(HDFSFileProperties::fromOrNull)
										 .filter(Objects::nonNull)
										 .toList()
							 : Lists.<FileProperties>newArrayList();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected boolean _hasToHandleFolder(final FileProperties folderProps,final int depth, 
										 final Stream.Builder<FileProperties> results) throws IOException {
		// since hdfs file system can only handle path-based filters,
		// the filtering MUST be emulated here
		return _filter != null
			&& _filter.accept(folderProps.getPath())
			&& _filter.accept(folderProps);
	}
	@Override
	protected Collection<FileProperties> _filterFolderContents(final FileProperties folderProps,final int depth,
											  		 		   final Collection<FileProperties> files) throws IOException {
		// since hdfs filesystem can only handle path-based filters,
		// the filtering MUST be emulated here
		return CollectionUtils.hasData(files) ? files.stream()
													  .filter(FileFilters.predicateFrom(_filter))
													  .toList()
											  : Lists.<FileProperties>newArrayList();
	}
	@Override
	protected void _handleFolderStart(final FileProperties folderProps,final int depth,
									  final Stream.Builder<FileProperties> results) throws IOException {
		if (results != null) results.add(folderProps);
	}
	@Override
	protected void _handleFile(final FileProperties fileProps,final int depth,
							   final Stream.Builder<FileProperties> results) throws IOException {
		results.add(fileProps);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Directory is valid if it exists, does not represent a file, and can be read.
	 */
	private void _validateDirectory(final Path aDirectory) throws IOException {
		if (aDirectory == null) {
			throw new IllegalArgumentException("Directory should not be null.");
		}
		if (!_hdfsFileSystem.exists(aDirectory)) {
			throw new FileNotFoundException("Directory does not exist: " + aDirectory);
		}
		if (!_hdfsFileSystem.isDirectory(aDirectory)) {
			throw new IllegalArgumentException("Is not a directory: " + aDirectory);
		}
	}
}
