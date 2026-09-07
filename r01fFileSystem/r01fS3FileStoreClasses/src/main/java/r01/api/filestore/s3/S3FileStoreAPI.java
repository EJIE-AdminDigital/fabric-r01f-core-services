package r01.api.filestore.s3;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;

import com.google.common.base.Preconditions;

import lombok.extern.slf4j.Slf4j;
import r01.api.filestore.model.oids.S3File;
import r01.api.filestore.model.oids.S3KeyPath;
import r01f.cloud.aws.s3.client.api.AWSS3BucketConfig;
import r01f.cloud.aws.s3.client.api.AWSS3ClientConfig;
import r01f.cloud.aws.s3.model.AWSS3FolderPath;
import r01f.cloud.aws.s3.model.AWSS3ObjectCopyResult;
import r01f.cloud.aws.s3.model.AWSS3ObjectDeleteResult;
import r01f.cloud.aws.s3.model.AWSS3ObjectGetResult;
import r01f.cloud.aws.s3.model.AWSS3ObjectHeadResult;
import r01f.cloud.aws.s3.model.AWSS3ObjectKey;
import r01f.cloud.aws.s3.model.AWSS3ObjectPutRequest;
import r01f.cloud.aws.s3.model.AWSS3ObjectPutResult;
import r01f.cloud.aws.s3.model.AWSS3Range;
import r01f.file.FileID;
import r01f.file.FileProperties;
import r01f.filestore.api.FileStoreAPI;
import r01f.filestore.api.FileStoreChecksDelegate;
import r01f.mime.MimeType;
import r01f.mime.MimeTypes;
import r01f.types.Path;
import r01f.util.types.Strings;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;

/**
 * Documentation:
 * https://docs.aws.amazon.com/es_es/AmazonS3/latest/userguide/Welcome.html
 * API: https://docs.aws.amazon.com/es_es/AmazonS3/latest/API/API_Operations_Amazon_Simple_Storage_Service.html
 * 
 * Code examples:
 * https://docs.aws.amazon.com/es_es/AmazonS3/latest/API/Welcome.html
 * https://docs.aws.amazon.com/es_es/sdk-for-java/latest/developer-guide/java_s3_code_examples.html
 * https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/example_code/s3#code-examples
 */
@Slf4j
public class S3FileStoreAPI
	 extends S3FileStoreAPIBase
  implements FileStoreAPI {

/////////////////////////////////////////////////////////////////////////////////////////
// 	FILESYSTEM STATIC INIT
/////////////////////////////////////////////////////////////////////////////////////////	
	public S3FileStoreAPI(final AWSS3ClientConfig s3Config) {
		super(new S3FileSystemProvider(s3Config));
		_check = new FileStoreChecksDelegate(this,
										  	 new S3FileStoreFilerAPI(this.getS3FileSystemProvider(),this));	// reuse the filesystem provider
	}
	public S3FileStoreAPI(final AWSS3ClientConfig s3Config, 
						  final AWSS3BucketConfig confBucket) {
		super(new S3FileSystemProvider(s3Config,confBucket));
		_check = new FileStoreChecksDelegate(this,
										  	 new S3FileStoreFilerAPI(this.getS3FileSystemProvider(),this));	// reuse the filesystem provider
	}
	S3FileStoreAPI(final S3FileSystemProvider fsProvider,
				   final S3FileStoreFilerAPI filerApi) {
		super(fsProvider);
		_check = new FileStoreChecksDelegate(this,
										  	 filerApi);	
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	private static r01f.types.Path _fileIdToPath(final FileID fileId) {
		if (fileId == null) throw new IllegalArgumentException("fileId MUST NOT be null!");
		if (Strings.isNullOrEmpty(fileId.asString())) throw new IllegalArgumentException("Can not create a Path from an empty string");		
		if (!(fileId instanceof r01f.types.Path)) throw new IllegalArgumentException(Strings.customized("The {} instance MUST be a {} instance",
																										FileID.class,r01f.types.Path.class));
		// the path must start with character or digit
		if (!Character.isLetterOrDigit(fileId.asString().charAt(0))) throw new IllegalArgumentException("The path " + fileId.asString() + " is NOT a valid WORKAREA path");
		
		return (r01f.types.Path)fileId;
	}
	private static S3KeyPath _fileIdToS3KeyPath(final FileID fileId) {
		return r01fPathToS3Path(_fileIdToPath(fileId));
	}

/////////////////////////////////////////////////////////////////////////////////////////
//  EXISTS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean existsFile(final FileID fileId) throws IOException {
		boolean outExists = false;

		_check.checkFileId(fileId);

		// exists?
		S3File s3File = s3FileFromKeyPath(_fileIdToS3KeyPath(fileId),
										  this.getS3BucketConfiguration());
		try {
			AWSS3ObjectHeadResult headResult = this.getS3Api()
													.forObjects()
													.headObject(s3File.getBucket(),
																s3File.getObjKey());
			
			if (headResult != null ) {
				log.debug(" .. existsFile {}  ...EXISTS True !!!!!", s3File.getObjKey() );
				outExists = true;
			}
		} catch (final NoSuchKeyException s3Exception) {
			log.debug(" .. existsFile {}  ...EXISTS False !!!!!", s3File.getObjKey() );
			outExists = false;
		}
		
		return outExists;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  COPY & RENAME
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean copyFile(final FileID srcFileId,final FileID dstFileId,
							final boolean overwrite) throws IOException {
		
		boolean copyFileStateOK = false;

		// check
		_check.checkFileId(srcFileId,dstFileId);
		_check.checkBeforeCopyFile(srcFileId,dstFileId,
								   overwrite);

		// copy
		S3File s3SrcFile = s3FileFromKeyPath(_fileIdToS3KeyPath(srcFileId),
											 this.getS3BucketConfiguration());
		S3File s3DstFile = s3FileFromKeyPath(_fileIdToS3KeyPath(dstFileId),
											 this.getS3BucketConfiguration());
		AWSS3ObjectCopyResult copyResult = this.getS3Api()
													.forObjects()
													.copyObject(s3SrcFile.getBucket(),s3SrcFile.getObjKey(),
																s3DstFile.getBucket(),s3DstFile.getObjKey());
		
		if (copyResult.getKey().is(s3SrcFile.getObjKey())) {
			copyFileStateOK = true;
			log.debug(" \n the file {}/{} has been copied to {}/{}",
							    s3SrcFile.getBucket(),
							    s3SrcFile.getObjKey(),
							    s3DstFile.getBucket(),
							    s3DstFile.getObjKey());
		} else {
			log.error("Error coping {}/{} to {}/{}.Error:{}",
									 s3SrcFile.getBucket(),
									 s3SrcFile.getObjKey(),
							    	 s3DstFile.getBucket(),
							    	 s3DstFile.getObjKey(),
							    	 copyResult.debugInfo());	
		}
		
		return copyFileStateOK;
	}
	@Override
	public boolean renameFile(final FileID srcFileId,
							  final FileID dstFileId) throws IOException {
		boolean isRenamed = false;

		// check
		_check.checkFileId(srcFileId,dstFileId);
		_check.checkBeforeMoveFile(srcFileId,
								   dstFileId,
								   false);		// DO NOT overwrite

		// rename
		S3File s3SrcFile = s3FileFromKeyPath(_fileIdToS3KeyPath(srcFileId),
											 this.getS3BucketConfiguration());
		S3File s3DstFile = s3FileFromKeyPath(_fileIdToS3KeyPath(dstFileId),
											 this.getS3BucketConfiguration());
		
		AWSS3ObjectCopyResult copyResult = this.getS3Api()
											   .forObjects()
											   .copyObject(s3SrcFile.getBucket(),
													       s3SrcFile.getObjKey(),
													       s3DstFile.getBucket(),
													       s3DstFile.getObjKey());
		if (copyResult.getKey().is(s3SrcFile.getObjKey())) {
			isRenamed = true;
			log.debug(" \n the file {}/{} has been copied to {}/{}", s3SrcFile.getBucket(),s3SrcFile.getObjKey(),					 
																	 s3DstFile.getBucket(),s3DstFile.getObjKey());
			
			this.getS3Api().forObjects().deleteObject(s3SrcFile.getBucket(), s3SrcFile.getObjKey());
			log.debug(" \n the file {}/{} has been deleted", s3SrcFile.getBucket(), s3SrcFile.getObjKey());
		} else {
			log.error("Error coping {}/{} to {}/{}.Error:{}", s3SrcFile.getBucket(),
															  s3SrcFile.getObjKey(),
															  s3DstFile.getBucket(),
															  s3DstFile.getObjKey(), 
															  copyResult.debugInfo());	
		}
				
		return isRenamed;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  WRITE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public OutputStream getFileOutputStreamForWriting(final FileID dstFileId,
													  final boolean overwrite) throws IOException {
		return this.getFileOutputStreamForWriting(dstFileId,
												  0,	// start at the beginning of the file
												  overwrite);
	}
	@Override
	public OutputStream getFileOutputStreamForWriting(final FileID dstFileId,final long offset,
													  final boolean overwrite) throws IOException {
		OutputStream out = null;
		
		try {
			// ------------------------------------------------------------
			log.trace("get outputstream for writing file {} (overwrite={})",
					  dstFileId,overwrite);
			if (offset > 0) throw new UnsupportedOperationException("NOT supports random writes (only sequential writing from the beginning of the file or appending are supported)");
			// check
			_check.checkFileId(dstFileId);
			_check.checkBeforeWriteToFile(dstFileId,
										  overwrite);
			// write
			out = _prepareFileOutputStream(dstFileId,
										   false,		// append
										   overwrite);
		} catch (IOException ioEx) {
			throw ioEx;
		}
			
		return out;
	}
	@Override
	public void writeToFile(final InputStream srcIS,
							final FileID dstFileId,
							final boolean overwrite) throws IOException {
		
		//writeToFile(srcIS,dstFileId,overwrite,MimeTypes.from(srcIS));
		writeToFile(srcIS,dstFileId,overwrite,MimeTypes.OCTECT_STREAM);
	}
	public void writeToFile(final InputStream srcIS,
							final FileID dstFileId,
							final boolean overwrite,
							final MimeType contentType) throws IOException {
		
		// check
		Preconditions.checkArgument(srcIS != null,"The source input stream cannot be null");
		_check.checkFileId(dstFileId);
		_check.checkBeforeWriteToFile(dstFileId,
									  overwrite);

		// prepare source & destination
		InputStream in = new BufferedInputStream(srcIS);		
		S3File s3File = s3FileFromKeyPath(_fileIdToS3KeyPath(dstFileId),
										  this.getS3BucketConfiguration());
		AWSS3ObjectPutRequest putReq = new AWSS3ObjectPutRequest(s3File.getBucket(),s3File.getObjKey(),in,contentType);
		AWSS3ObjectPutResult putResult = this.getS3Api()
											 .forObjects()
			  								 .putObject(putReq);
		
		log.debug(" \n put result after te or update {}",putResult.debugInfo());

	}
	@Override
	public void writeChunkToFile(final byte[] data,
								 final FileID dstFileId,final long offset,
								 final boolean overwrite) throws IOException {
		throw new UnsupportedOperationException("S3 does NOT supports this operation, only upload complete objects (new version is created if object exists.");
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override @SuppressWarnings("resource")
	public OutputStream getFileOutputStreamForAppending(final FileID dstFileId) throws IOException {
		
		log.trace("Append to file {}",dstFileId);
		
		OutputStream dstFOS = null;		
		try {
			// check
			_check.checkFileId(dstFileId);
			_check.checkBeforeAppendToFile(dstFileId);
	
			// write
			dstFOS = _prepareFileOutputStream(dstFileId,
											  true,	  // append
											  false); // overwrite
		} catch (IOException ioEx) {
			throw ioEx;
		}
				
		return dstFOS;
	}
	@Override @SuppressWarnings("resource")
	public void appendToFile(final InputStream srcIS,
							 final FileID dstFileId) throws IOException {

		// check
		Preconditions.checkArgument(srcIS != null,"The source input stream cannot be null");
		_check.checkFileId(dstFileId);
		_check.checkBeforeWriteToFile(dstFileId,
									  true); //overwrite

		// prepare source & destination
		InputStream in = new BufferedInputStream(srcIS);		
		S3File s3File = s3FileFromKeyPath(_fileIdToS3KeyPath(dstFileId),
										  this.getS3BucketConfiguration());
		
		AWSS3ObjectPutResult putResult = this.getS3Api()
												.forObjects()
			  									  .appendToObject(s3File.getBucket(),
			  											  		  s3File.getObjKey(),
			  											  		  in,
			  											  		  null);
		
		log.debug(" \n put result after create or update {}",
														putResult.debugInfo());
	}
	@Override @SuppressWarnings("resource")
	public void appendChunkToFile(final byte[] srcDataChunk,
								  final FileID dstFileId) throws IOException {

		Preconditions.checkArgument(dstFileId != null,"The path cannot be null");
		log.debug("Append chunk to file");
		if (srcDataChunk == null || srcDataChunk.length == 0) {
			log.warn("The data to write in file is NULL!!!");
			return;
		}

		// Prepare source and destination
		try (InputStream srcIS = new BufferedInputStream(new ByteArrayInputStream(srcDataChunk));
			 OutputStream out = this.getFileOutputStreamForAppending(dstFileId)) {

			// write
			IOUtils.copy(srcIS,out);	// close after write
		}
	}
	@SuppressWarnings("resource")
	private OutputStream _prepareFileOutputStream(final FileID dstFileId,
												  final boolean appendToFile,
												  final boolean overwrite) throws IOException {
		try {
			log.trace("\tPrepare file {} to be written (append={}, overwrite={})",
					  dstFileId,appendToFile,overwrite);
			
			S3File s3File = s3FileFromKeyPath(_fileIdToS3KeyPath(dstFileId),
											  this.getS3BucketConfiguration());
			S3FileOutputStream out = new S3FileOutputStream(this.getS3Api(),
															s3File.getBucket(),
															s3File.getObjKey());
			// check if the file exists
			boolean prevExists = this.existsFile(dstFileId);
	
			if (prevExists) {
				log.debug("\tFile {} already exists",dstFileId);
				if (appendToFile) {
					log.trace("Appending to file {}...",dstFileId);
					InputStream originalInputStream = this.readFromFile(dstFileId);					
					originalInputStream.transferTo(out);	
				} else if (overwrite) {
					log.trace("Overwrite file {}...",dstFileId);
				} else {
					throw new IOException(Strings.customized("Cannot write to file {}: it previously exists and append=false / overwrite=false",
															 dstFileId));
				}
			} else {
				log.trace("\tCreate new file {}...",dstFileId);
				// Create empty file				
				this.getS3Api()
						.forObjects()
						.putObject(s3File.getBucket(),s3File.getObjKey(),new ByteArrayInputStream(new byte[0]));
			}
			return out;
		} catch (IOException ioEx) {
			 throw ioEx;
		}		
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  READ
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public InputStream readFromFile(final FileID fileId) throws IOException {
		return this.readFromFile(fileId,
								 0); 		// starting at the beginning of the file
	}
	
	@Override @SuppressWarnings("resource")
	public InputStream readFromFile(final FileID fileId,final long offset) throws IOException {
		InputStream outIS = null;

		log.trace("Reading file {}",fileId);
		
		// check
		_check.checkFileId(fileId);
		_check.checkBeforeReadingFromFile(fileId);
		
		// read
		S3File s3File = s3FileFromKeyPath(_fileIdToS3KeyPath(fileId),
										  this.getS3BucketConfiguration());		

		outIS = this.getS3Api()
						.forObjects()
						.getObject(s3File.getBucket(),
								   s3File.getObjKey())
						.getInputStream();
		
		log.debug(" \n the file {}/{} has been read",s3File.getBucket(),s3File.getObjKey());
		
		return outIS;

	}
	@Override @SuppressWarnings("resource")
	public byte[] readChunkFromFile(final FileID fileId,
			  		   				final long offset,
			  		   				final int len) throws IOException {
		log.trace("Reading file {}",fileId);
		
		// check
		_check.checkFileId(fileId);
		_check.checkBeforeReadingFromFile(fileId);
		
		// read
		S3File s3File = s3FileFromKeyPath(_fileIdToS3KeyPath(fileId),
										  this.getS3BucketConfiguration());
		
	    long end = offset + Math.max(0, len) - 1;
	    AWSS3Range s3Range = AWSS3Range.forId("bytes=" + offset + "-" + end);
	    
	    AWSS3ObjectGetResult result = this.getS3Api().getForObjects()
	    											 .getObject(s3File.getBucket(), 
	    													 	s3File.getObjKey(), 
	    													 	s3Range);
	    try (InputStream in = result.getInputStream()) {
	        byte[] buf = in.readAllBytes();
	        return buf;
	    } finally {
	    	log.trace("Read bytes in range {}", s3Range.asString());
	    }
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  DELETE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean deleteFile(final FileID fileId) throws IOException {
		boolean opState = false;

		log.trace("Deleting file {}",fileId);

		// check
		_check.checkFileId(fileId);
		_check.checkBeforeDeleteFile(fileId);
		// delete
		S3File s3File = s3FileFromKeyPath(_fileIdToS3KeyPath(fileId),
										  this.getS3BucketConfiguration());		
		AWSS3ObjectDeleteResult deleteResult = this.getS3Api()
													.forObjects()
													.deleteObject(s3File.getBucket(),
																  s3File.getObjKey());
		if (deleteResult.getKey().is(s3File.getObjKey())) {
			opState = true;
			log.debug(" \n the file {}/{} has been deleted",s3File.getBucket(),s3File.getObjKey());
		} else {
			log.error("Error deleting {}/{}.Error:{}",s3File.getBucket(),s3File.getObjKey(),deleteResult.debugInfo());	
		}
		
		return opState;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override @SuppressWarnings("resource")
	public FileProperties getFileProperties(final FileID fileId) throws IOException {
		
		S3File s3File = s3FileFromKeyPath(_fileIdToS3KeyPath(fileId),
										  this.getS3BucketConfiguration());
		AWSS3ObjectHeadResult headObj;
		try {
			headObj = this.getS3Api().forObjects()
									  .headObject(s3File.getBucket(),
												  s3File.getObjKey());
						
		} catch (NoSuchKeyException e) {
			Path folderPath = _fileIdToS3KeyPath(fileId);
			headObj = this.getS3Api().forObjects()
									  .headObject(s3File.getBucket(),
												  new AWSS3ObjectKey(AWSS3FolderPath.fromPath(folderPath).asString()));
		}
		
		FileProperties fileprops = S3FileProperties.from(headObj,
											 			 _fileIdToPath(fileId));

		return fileprops;
	}
	@Override @SuppressWarnings("resource")
	public void setFileModifiedDate(final FileID fileId, final long modifiedTimeInMillis) throws IOException {
		return;
	}
}

