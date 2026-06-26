package r01f.cloud.aws.s3.client.api.delegates;

import java.util.Collection;

import r01f.cloud.aws.s3.api.interfaces.AWSS3ServicesForFiler;
import r01f.cloud.aws.s3.api.interfaces.impl.AWSS3ServicesForFilerImpl;
import r01f.cloud.aws.s3.model.AWSS3Bucket;
import r01f.cloud.aws.s3.model.AWSS3FileFilter;
import r01f.cloud.aws.s3.model.AWSS3FolderPath;
import r01f.cloud.aws.s3.model.AWSS3ObjectSummary;
import r01f.mime.MimeType;
import software.amazon.awssdk.services.s3.S3Client;

public class AWSS3ClientAPIDelegateForFiler
  implements AWSS3ServicesForFiler {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////	
	protected final AWSS3ServicesForFilerImpl _serviceForFolderFilerImpl;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AWSS3ClientAPIDelegateForFiler(final S3Client s3Client) {
		_serviceForFolderFilerImpl = new  AWSS3ServicesForFilerImpl(s3Client);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EXISTENCE
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public boolean existsFolder(final AWSS3Bucket bucket,
								final AWSS3FolderPath path) {
		return _serviceForFolderFilerImpl.existsFolder(bucket,
													   path);
	}
	@Override
	public boolean existsFolder(final AWSS3Bucket bucket,
								final AWSS3FolderPath path,
								final boolean physicallyExistenceCheck) {
		return _serviceForFolderFilerImpl.existsFolder(bucket,
													   path,
													   physicallyExistenceCheck);
	}
	@Override
	public boolean hasSubfolder(final AWSS3Bucket bucket,
								final AWSS3FolderPath path) {
		return _serviceForFolderFilerImpl.hasSubfolder(bucket,
													   path);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	COPY / MOVE / RENAME
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean copyFolder(final AWSS3Bucket bucket,
							  final AWSS3FolderPath srcPath,final AWSS3FolderPath dstPath,
							  final boolean overwrite) {
		return _serviceForFolderFilerImpl.copyFolder(bucket,
													 srcPath,dstPath,
													 overwrite);
	}
	@Override
	public boolean moveFolder(final AWSS3Bucket bucket,
							  final AWSS3FolderPath srcPath,final AWSS3FolderPath dstPath,
							  final boolean overwrite) {
		return _serviceForFolderFilerImpl.moveFolder(bucket,
													 srcPath,dstPath,
													 overwrite);
	}

/////////////////////////////////////////////////////////////////////////////////////////
//	CREATE & DELETE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean createFolder(final AWSS3Bucket bucket,
								final AWSS3FolderPath folderPath) {
		return _serviceForFolderFilerImpl.createFolder(bucket,
													   folderPath);
	}
	@Override
	public boolean createFolder(final AWSS3Bucket bucket,
								final AWSS3FolderPath folderPath,
								final MimeType contentType,
								final boolean physicallyExistenceCheck) {
		return _serviceForFolderFilerImpl.createFolder(bucket,
													   folderPath,
													   contentType,
													   physicallyExistenceCheck);
	}
	@Override
	public boolean deleteFolder(final AWSS3Bucket bucket,
								final AWSS3FolderPath folderPath) {
		return _serviceForFolderFilerImpl.deleteFolder(bucket,
													   folderPath);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	LIST
/////////////////////////////////////////////////////////////////////////////////////////
	public Collection<AWSS3ObjectSummary> listFolderContents(final AWSS3Bucket bucket,final  AWSS3FolderPath folderPath,
															 final AWSS3FileFilter fileFilter,
															 final boolean recursive)  {
		return _serviceForFolderFilerImpl.listFolderContents(bucket,folderPath,
															 fileFilter,
															 recursive,
															 false);		// do NOT exclude folders
	}
	public Collection<AWSS3ObjectSummary> listFolderContents(final AWSS3Bucket bucket,final  AWSS3FolderPath folderPath,
															  final boolean recursive)  {
		return _serviceForFolderFilerImpl.listFolderContents(bucket,folderPath,
															 null,
															 recursive,
															 false);		// do NOT exclude folders
	}
	@Override
	public Collection<AWSS3ObjectSummary> listFolderContents(final AWSS3Bucket bucket,final AWSS3FolderPath folderPath,
															 final AWSS3FileFilter fileFilter,
															 final boolean recursive,
															 final boolean excludeFolderTypes) {
		return _serviceForFolderFilerImpl.listFolderContents(bucket,folderPath,
															 fileFilter,
															 recursive,
															 excludeFolderTypes);
	}
	public Collection<AWSS3ObjectSummary> listFolderContents(final AWSS3Bucket bucket,final AWSS3FolderPath folderPath,
															 final boolean recursive,
															 final boolean excludeFolderTypes) {
		return _serviceForFolderFilerImpl.listFolderContents(bucket,
															 folderPath,
															 null,			// no filter
															 recursive,
															 excludeFolderTypes);
	}
	@Override
	public Collection<AWSS3ObjectSummary> listBucketContents(final AWSS3Bucket bucket,
															 final AWSS3FileFilter fileFilter,
															 final boolean recursive) {
		return _serviceForFolderFilerImpl.listBucketContents(bucket,
															 fileFilter,
															 recursive);
	}	
}
