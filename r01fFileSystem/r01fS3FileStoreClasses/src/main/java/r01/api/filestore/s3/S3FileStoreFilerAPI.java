package r01.api.filestore.s3;

import java.io.IOException;
import java.util.Collection;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import r01.api.filestore.model.oids.S3Folder;
import r01f.cloud.aws.s3.client.api.AWSS3BucketConfig;
import r01f.cloud.aws.s3.client.api.AWSS3ClientConfig;
import r01f.cloud.aws.s3.model.AWSS3FileFilter;
import r01f.cloud.aws.s3.model.AWSS3ObjectSummary;
import r01f.file.FileNameAndExtension;
import r01f.file.FileProperties;
import r01f.filestore.api.FileFilter;
import r01f.filestore.api.FileStoreChecksDelegate;
import r01f.filestore.api.FileStoreFilerAPI;
import r01f.mime.MimeTypes;
import r01f.types.IsPath;
import r01f.types.Path;
import r01f.util.types.collections.CollectionUtils;

@Slf4j
public class S3FileStoreFilerAPI
	 extends S3FileStoreAPIBase
  implements FileStoreFilerAPI {

/////////////////////////////////////////////////////////////////////////////////////////
// 	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	protected final S3FileStoreAPI _api;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public S3FileStoreFilerAPI(final AWSS3ClientConfig conf) {
		super(new S3FileSystemProvider(conf));
		_api = new S3FileStoreAPI(this.getS3FileSystemProvider(),this); // reuse the filesystem provider
		_check = new FileStoreChecksDelegate(_api,this);
		
	}
	public S3FileStoreFilerAPI(final AWSS3ClientConfig conf,
							   final AWSS3BucketConfig confBucket) {
		super(new S3FileSystemProvider(conf,confBucket));
		_api = new S3FileStoreAPI(this.getS3FileSystemProvider(),this); // reuse the filesystem provider
		_check = new FileStoreChecksDelegate(_api,this);
		
	}
	S3FileStoreFilerAPI(final S3FileSystemProvider fsProvider,
						final S3FileStoreAPI fileApi) {
		super(fsProvider);
		_api = fileApi;
		_check = new FileStoreChecksDelegate(fileApi,
					  					  	 this);
	}
		
/////////////////////////////////////////////////////////////////////////////////////////
//  EXISTS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public FileProperties getFolderProperties(final Path path) throws IOException {
		FileProperties props = new S3FileProperties();
		props.setFolder(true);
		props.setPath(path);
		return props;
	}
	@Override
	public boolean existsFolder(final Path path) throws IOException {
		boolean outExists = false;
		
		// exists?
		S3Folder s3Folder = s3FolderFromPath(path,
											 false,
											 this.getS3BucketConfiguration()); // not validate name
		
		outExists = this.getS3Api()
							.forFiler()
							.existsFolder(s3Folder.getBucket(),
										  s3Folder.getPath());
			
		log.debug(" .. existsFolder {}  ...EXISTS {} !!!!!", s3Folder.getPath(),outExists);
		
		return outExists;
	}
	@Override
	public boolean hasSubfolder(final Path folderPath) throws IOException {		
		boolean hasSubfolder = false;
		
		//_check.checkBeforeHasSubfolder(folderPath);
		
		S3Folder s3Folder = s3FolderFromPath(folderPath,
											 false,
											 this.getS3BucketConfiguration()); // not validate name
		
		hasSubfolder = this.getS3Api()
							.forFiler()
							.hasSubfolder(s3Folder.getBucket(),
										  s3Folder.getPath());
			
		log.debug(" .. hasSubfolder {}? {} !!!!!", s3Folder.getPath(),hasSubfolder);
		
		return hasSubfolder;
		
		
	}/////////////////////////////////////////////////////////////////////////////////////////
//  COPY / MOVE / RENAME
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean copyFolder(final Path srcPath,final Path dstPath,
							  final FileFilter fileFilter,
							  final boolean overwrite) throws IOException {
		
		boolean copyFolderStateOK = false;

		log.debug("Copying from {} to {}",srcPath,dstPath);		
		
		S3Folder s3SrcFolder = s3FolderFromPath(srcPath, true, this.getS3BucketConfiguration());
		S3Folder s3DstFolder = s3FolderFromPath(dstPath, true, this.getS3BucketConfiguration());
		if (s3SrcFolder.getBucket().isNOT(s3DstFolder.getBucket())) return false;
		
		// check
		_check.checkBeforeCopyFolder(srcPath,dstPath,overwrite);
		
		// create destination folder
		if (!this.existsFolder(dstPath)) this.createFolder(dstPath);
		
		if (fileFilter != null) {
			// copy folder applying filter (recursive)
			FileProperties[] filteredFiles = this.listFolderContents(srcPath,
																	 fileFilter,
																	 false); // not recursive
			for (int i=0; i<filteredFiles.length; i++) {
				FileProperties currentFileOrFolder = filteredFiles[i];
	
				Path effectiveDstPath = dstPath.joinedWith(currentFileOrFolder.getPath()
																			  .remainingPathFrom(srcPath));
				if (currentFileOrFolder.isFile()) {
					boolean copyFileStateOK = _api.copyFile(filteredFiles[i].getPath(),
															effectiveDstPath, 
															overwrite);
					copyFolderStateOK = copyFileStateOK;					
				} else {
					copyFolderStateOK = this.copyFolder(currentFileOrFolder.getPath(),
														effectiveDstPath,
														fileFilter,
														overwrite);			
				}
			}
		} else {
			// copy folder without applying filter
			copyFolderStateOK = this.getS3Api()
										.forFiler()
										.copyFolder(s3SrcFolder.getBucket(), 
													s3SrcFolder.getPath(), 
													s3DstFolder.getPath(),
													overwrite);
		}

		return copyFolderStateOK;
	}
	@Override
	public boolean moveFolder(final Path srcPath,final Path dstPath,
							  final boolean overwrite) throws IOException {
		boolean moveFolderStateOK = false;
		
		log.debug("Moving folder from {} to {}",srcPath,dstPath);
		
		S3Folder s3SrcFolder = s3FolderFromPath(srcPath,true,this.getS3BucketConfiguration());
		S3Folder s3DstFolder = s3FolderFromPath(dstPath,true,this.getS3BucketConfiguration());
		if (s3SrcFolder.getBucket().isNOT(s3DstFolder.getBucket())) return false;
		
		// check
		_check.checkBeforeMoveFolder(srcPath,dstPath,overwrite);
		
		moveFolderStateOK = this.getS3Api()
									.forFiler()
									.moveFolder(s3SrcFolder.getBucket(),
												s3SrcFolder.getPath(),
												s3DstFolder.getPath(),
												overwrite);
		
		return moveFolderStateOK;
	}
	@Override
	public boolean renameFolder(final Path existingPath,
								final FileNameAndExtension newName) throws IOException {
		
		boolean outRenamed = false;
		
		log.trace("Renaming folder from {} to {}",existingPath,newName);

		Path dstPath = Path.from((IsPath)existingPath.withoutLastPathElement()).joinedWith(newName);
		
		S3Folder s3SrcFolder = s3FolderFromPath(existingPath,true,this.getS3BucketConfiguration());
		S3Folder s3DstFolder = s3FolderFromPath(dstPath,true,this.getS3BucketConfiguration());
		
		// check
		_check.checkBeforeMoveFolder(existingPath,
									 dstPath,
									 false); // DO NOT overwrite

		outRenamed = this.getS3Api()
							.forFiler()
							.moveFolder(s3SrcFolder.getBucket(),
										s3SrcFolder.getPath(),
										s3DstFolder.getPath(),
										false);
		
		return outRenamed;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CREATE & DELETE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean createFolder(final Path path) throws IOException {
		
		boolean opState = false;

		S3Folder s3SrcFolder = s3FolderFromPath(path,true,this.getS3BucketConfiguration());
		
		// check
		_check.checkBeforeCreateFolder(path);
		
		opState = this.getS3Api()
				.forFiler()
				.createFolder(s3SrcFolder.getBucket(),
							  s3SrcFolder.getPath(),
							  MimeTypes.APPLICATION_XDIRECTORY,// Mimetype: application/x-directory
							  true); 	

		return opState;
	}
	@Override
	public boolean deleteFolder(final Path path) throws IOException {
		boolean opState = false;
		
		log.trace("Deleting a folder at {}",path);
		
		S3Folder s3SrcFolder = s3FolderFromPath(path,true,this.getS3BucketConfiguration());		
		
		// check
		_check.checkBeforeRemoveFolder(path);

		opState = this.getS3Api()
						.forFiler()
						.deleteFolder(s3SrcFolder.getBucket(),
									  s3SrcFolder.getPath());		
		return opState;
	}
/////////////////////////////////////////////////////////////////////////////////////////
// LIST
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public FileProperties[] listFolderContents(final Path folderPath,
											   final FileFilter fileFilter,
											   final boolean recursive) throws IOException {
		log.info("Listing folder {} contents",
				  folderPath);
		
		S3Folder s3SrcFolder = s3FolderFromPath(folderPath,true,this.getS3BucketConfiguration());
		
		// check
		_check.checkBeforeListFolderContents(folderPath);
				
		AWSS3FileFilter s3PathFilter = null;	
		// filter
		if (fileFilter != null) {
			s3PathFilter = new AWSS3FileFilter() {
									@Override
									public boolean accept(final Path path) {										
										return fileFilter.accept(path);				
									}
							};
		}
		// list
		Collection<AWSS3ObjectSummary> allContents = this.getS3Api()
															.forFiler()
															.listFolderContents(s3SrcFolder.getBucket(), 
																				s3SrcFolder.getPath(),
																				s3PathFilter,
																				recursive);

		FileProperties[] out = CollectionUtils.hasData(allContents)
													? allContents.stream()
																	.map(p -> {
																			try {
																				return p.getKey().asString().endsWith("/") ? this.getFolderProperties(Path.from(p.getKey().asString()))
																														   : _api.getFileProperties(Path.from(p.getKey().asString()));
																			} catch (IOException e) {
																				e.printStackTrace();
																			}
																			return null;
																		})
																		.filter(obj -> obj!=null) //remove null objects
																		.collect(Collectors.toList())
																		.toArray(new FileProperties[allContents.size()])
																		
													: new FileProperties[] { /* empty */ };
		
		return out;
	}
}
