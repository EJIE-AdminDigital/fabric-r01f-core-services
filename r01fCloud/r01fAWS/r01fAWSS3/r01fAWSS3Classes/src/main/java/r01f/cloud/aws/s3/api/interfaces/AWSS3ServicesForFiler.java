package r01f.cloud.aws.s3.api.interfaces;

import java.io.IOException;
import java.util.Collection;

import r01f.cloud.aws.s3.model.AWSS3Bucket;
import r01f.cloud.aws.s3.model.AWSS3FileFilter;
import r01f.cloud.aws.s3.model.AWSS3FolderPath;
import r01f.cloud.aws.s3.model.AWSS3ObjectSummary;
import r01f.mime.MimeType;

public interface AWSS3ServicesForFiler {
/////////////////////////////////////////////////////////////////////////////////////////
// EXISTENCE
/////////////////////////////////////////////////////////////////////////////////////////
		/**
	 * Checks if a folder exists into bucket
	 * @param bucket
	 * @param folderPath
	 * @return
	 */
	public boolean existsFolder(final AWSS3Bucket bucket,final AWSS3FolderPath folderPath);
	
	/**
	 * Checks if a folder exists into bucket
	 * @param bucket
	 * @param folderPath
	 * @param physicallyExistenceCheck :
	 *		  - true: a folder can exist as a 0-bytes object into a bucket with mimetype application/x-directory or
	 *		  - false: just and only implicitly forming part of the name of an object foo/myfolder
	 * @return
	 */
	public boolean existsFolder(final AWSS3Bucket bucket,final AWSS3FolderPath folderPath, 
								final boolean physicallyExistenceCheck);
	
	/**
	 * Check if folder has at least one subfolder
	 * @param bucket
	 * @param folderPath
	 * @return
	 */
	public boolean hasSubfolder(final AWSS3Bucket bucket,
								final AWSS3FolderPath folderPath);
	
/////////////////////////////////////////////////////////////////////////////////////////
//	COPY / MOVE / RENAME
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Copies a folder
	 * @param srcPath
	 * @param dstPath
	 * @return
	 * @throws IOException
	 */
	public boolean copyFolder(final AWSS3Bucket bucket,
							  final AWSS3FolderPath srcPath,final AWSS3FolderPath dstPath,
							  final boolean overwrite) throws IOException;
	/**
	 * Moves a folder
	 * @param srcPath
	 * @param dstPath
	 * @param fileFilter
	 * @param overwrite
	 * @return
	 */
	public boolean moveFolder(final AWSS3Bucket bucket,
							  final AWSS3FolderPath srcPath,final AWSS3FolderPath dstPath,
							  final boolean overwrite);

/////////////////////////////////////////////////////////////////////////////////////////
//  CREATE & DELETE
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Creates a dir
	 * @param folderPath
	 * @return
	 * @throws IOException
	 */
	public boolean createFolder(final AWSS3Bucket bucket,final AWSS3FolderPath folderPath);
	/**
	 * Creates a dir
	 * @param bucket
	 * @param folderPath
	 * @param contentType for folder could be application/x-directory the best option
	 * @param physicallyExistenceCheck :
	 *		  - true: a folder can exist as a 0-bytes object into a bucket with mimetype application/x-directory or
	 *		  - false: just and only implicitly forming part of the name of an object foo/myfolder
	 * @return
	 * @throws IOException
	 */
	public boolean createFolder(final AWSS3Bucket bucket,final AWSS3FolderPath folderPath,final MimeType contentType,final boolean physicallyExistenceCheck);
	/**
	 * Deletes a directory no matter if it's not empty
	 * @param folderPath
	 * @return
	 * @throws IOException
	 */
	public boolean deleteFolder(final  AWSS3Bucket bucket,final AWSS3FolderPath folderPath) ;
/////////////////////////////////////////////////////////////////////////////////////////
//  LIST
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Lists a bucket's contents
	 * @param bucket
	 * @param fileFilter
	 * @param recursive
	 * @return
	 */
	public Collection<AWSS3ObjectSummary> listBucketContents(final AWSS3Bucket bucket,
															 final AWSS3FileFilter fileFilter,
															 final boolean recursive);
	/**
	 * Lists a dir's contents, 1st level or complete subdirectories if recursive mode to true, but excluding folder types.
	 * @param bucket
	 * @param folderPath
	 * @param filter the filter, <code>null</code> if filter is not applied.
	 * @param recursive if <code>true</code> explore all subdir files.
	 * @return files and dirs
	 * @throws IOException
	 */
	public Collection<AWSS3ObjectSummary> listFolderContents(final AWSS3Bucket bucket,final AWSS3FolderPath folderPath, 
															 final AWSS3FileFilter fileFilter,
															 final boolean recursive, 
															 final boolean excludeFolderTypes);
}
