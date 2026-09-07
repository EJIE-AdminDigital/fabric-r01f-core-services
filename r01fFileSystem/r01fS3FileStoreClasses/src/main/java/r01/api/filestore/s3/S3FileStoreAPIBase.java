package r01.api.filestore.s3;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.experimental.Accessors;
import r01.api.filestore.model.oids.S3File;
import r01.api.filestore.model.oids.S3Folder;
import r01.api.filestore.model.oids.S3KeyPath;
import r01f.cloud.aws.s3.client.api.AWSS3BucketConfig;
import r01f.cloud.aws.s3.client.api.AWSS3ClientAPI;
import r01f.cloud.aws.s3.client.api.AWSS3ClientConfig;
import r01f.cloud.aws.s3.model.AWSS3BucketID;
import r01f.cloud.aws.s3.model.AWSS3FolderPath;
import r01f.cloud.aws.s3.model.AWSS3ObjectKey;
import r01f.filestore.api.FileStoreChecksDelegate;
import r01f.filestore.api.FileStoreType;
import r01f.types.Path;
import r01f.util.types.Strings;

@Accessors(prefix="_")
abstract class S3FileStoreAPIBase {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////	

/////////////////////////////////////////////////////////////////////////////////////////
// 	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Common checkings
	 */
	protected FileStoreChecksDelegate _check;
	/**
	 * Generic class to access and manage S3 files/directories.
	 */
	private final S3FileSystemProvider _fsProvider;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR / BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	 S3FileStoreAPIBase(final S3FileSystemProvider fsProvider) {
		 _fsProvider = fsProvider;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	STORE TYPE
/////////////////////////////////////////////////////////////////////////////////////////
	public FileStoreType getStoreType() {
		return FileStoreType.S3;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	GET
/////////////////////////////////////////////////////////////////////////////////////////	 
	public S3FileSystemProvider getS3FileSystemProvider() {
		return _fsProvider;
	}
	public AWSS3ClientAPI getS3Api() {
		return _fsProvider.getS3Api();
	}
	public AWSS3ClientConfig getS3Configuration() {
		return _fsProvider.getS3Config();
	}
	public AWSS3BucketConfig getS3BucketConfiguration() {
		return _fsProvider.getS3BucketConfig();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	protected static S3KeyPath r01fPathToS3Path(final r01f.types.Path path) {
		return S3KeyPath.from(path.asAbsoluteString());
	}
	protected static S3File s3FileFromKeyPath(final S3KeyPath kPath, final AWSS3BucketConfig bucketConf) {
		_checkPathName(kPath);
		return new S3File(new AWSS3BucketID(bucketConf.getDefaultBucket()), 
						  new AWSS3ObjectKey(kPath.asString()));
	}
	protected static S3Folder s3FolderFromPath(final Path path,
											   final boolean validateName,
											   AWSS3BucketConfig bucketConf) {
		if (path==null 
				|| Strings.isNullOrEmpty(path.asString()))
			throw new IllegalArgumentException("The path cannot be null or empty");
		
		_checkPathName(path);
		return new S3Folder(new AWSS3BucketID(bucketConf.getDefaultBucket()), 
							AWSS3FolderPath.fromPath(path));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private static void _checkPathName(final Path path) {
		Path pathToValidate = Path.from(path);
		String regex = "^[\\p{L}0-9_\\-~.(),%'&\\[\\]¦! +]+$";
	    Pattern pattern = Pattern.compile(regex);
	    Matcher matcher = pattern.matcher(pathToValidate.getLastPathElement());

        if (!matcher.matches())
        	throw new IllegalArgumentException("The path name is invalid");
	}
}
