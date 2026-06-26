package r01f.cloud.aws.s3.api.interfaces.impl;

import static r01f.cloud.aws.s3.model.AWSS3FolderPath.DELIMITER;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;
import r01f.cloud.aws.s3.api.interfaces.AWSS3ServicesForFiler;
import r01f.cloud.aws.s3.model.AWSS3Bucket;
import r01f.cloud.aws.s3.model.AWSS3FileFilter;
import r01f.cloud.aws.s3.model.AWSS3FolderPath;
import r01f.cloud.aws.s3.model.AWSS3ObjectKey;
import r01f.cloud.aws.s3.model.AWSS3ObjectSummary;
import r01f.httpclient.HttpResponseCode;
import r01f.mime.MimeType;
import r01f.mime.MimeTypes;
import r01f.types.Path;
import r01f.util.types.collections.CollectionUtils;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.S3Object;

@Slf4j
public class AWSS3ServicesForFilerImpl
	 extends AWSS3ServicesBaseImpl
  implements AWSS3ServicesForFiler {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AWSS3ServicesForFilerImpl(final S3Client s3Client)  {
		super(s3Client);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	METHODS TO IMPLEMENT
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean existsFolder(final AWSS3Bucket bucket,
								final AWSS3FolderPath folderPath) {
		log.warn("Check if folder exists bucket/key={}/{}",bucket,folderPath);

		if (!folderPath.asString().contains(AWSS3FolderPath.DELIMITER)) {
			log.warn("... object bucket/key={}/{}",
					 bucket,folderPath);
			return false;
		}
		
		HeadObjectRequest req = HeadObjectRequest.builder()
													 .bucket(bucket.asString())
													 .key(folderPath.asString())
												 .build();
		
		try {
			HeadObjectResponse headRes = _s3Client.headObject(req);
			
			if (! headRes.contentType().equals(MimeTypes.APPLICATION_XDIRECTORY.asString())) {
				log.warn("... object bucket/key={}/{} was supposed to be a FOLDER but it has content-type=octect-stream",
						 bucket,folderPath);
				return false;
			}
			if (headRes.contentLength() > 0) {
				log.warn("... object bucket/key={}/{} was supposed to be a FOLDER but it has content-length > 0",
						 bucket,folderPath);
				return false;
			}			
		} catch (final S3Exception s3Ex) {
			if (HttpResponseCode.of(s3Ex.statusCode()) == HttpResponseCode.NOT_FOUND ) {
				log.warn("Folder NOT found at={}",
						 folderPath.asString());
				return false;
			}
			log.error(" Error {}",
					  s3Ex.getMessage(),s3Ex);
			throw s3Ex;
		}
		
		return true;
	}
	
	/**
	 * Checks:
	 * 	 > Checks the folder has a logical existence within a hierarchy as part of the key of a file.
	 *   > Checks the folder has a physical existence with certain characteristics, the key has the common delimiter, the MimeType must be application/x-directory, it has 0 bytes.
	 */
	@Override
	public boolean existsFolder(final AWSS3Bucket bucket,
								final AWSS3FolderPath folderPath,
								final boolean physicallyExistenceCheck)  {
		log.warn("Check if folder exists bucket/key={}/{}",bucket,folderPath);

		if (!folderPath.asString().contains(AWSS3FolderPath.DELIMITER)) {
			log.warn("... object bucket/key={}/{} was supposed to be a FOLDER but it has NO path delimiter={}",
					 bucket,folderPath,DELIMITER);
			return false;
		}

		//	Checks the folder has a logical existence within a hierarchy as part of the key of a file.
		ListObjectsV2Request request = ListObjectsV2Request.builder()
															.bucket(bucket.asString())
															.prefix(folderPath.asString())
															.maxKeys(1) // Solo necesitamos saber si hay al menos un objeto
															.build();

		ListObjectsV2Response response = _s3Client.listObjectsV2(request);			
		
		boolean folderExistsLogically = !response.contents().isEmpty();
		
		if (!physicallyExistenceCheck) {	//|| (physicallyExistenceCheck && !folderExistsLogically ) ) {
			return folderExistsLogically;
		}
		
		//	Checks the folder has a physical existence with certain characteristics, the key has the common delimiter, it has 0 bytes.
		//AWSS3FolderPath folderPath = AWSS3FolderPath.forPath(path);
		try {
			HeadObjectRequest req = HeadObjectRequest.builder()
														 .bucket(bucket.asString())
														 .key(folderPath.asString())
													 .build();
			
			HeadObjectResponse headRes = _s3Client.headObject(req);
			
			if (! headRes.contentType().equals(MimeTypes.APPLICATION_XDIRECTORY.asString())) {
				log.warn("... object bucket/key={}/{} was supposed to be a FOLDER but it has content-type=application/x-directory",
						 bucket,folderPath);
				return false;
			}
			if (headRes.contentLength() > 0) {
				log.warn("... object bucket/key={}/{} was supposed to be a FOLDER but it has content-length > 0",
						 bucket,folderPath);
				return false;
			}
			
		} catch (final S3Exception s3Ex) {
			if (HttpResponseCode.of(s3Ex.statusCode()) == HttpResponseCode.NOT_FOUND ) {
				log.warn("Folder NOT found at={}",
						 folderPath.asString());
				return false;
			}
			log.error(" Error {}",
					  s3Ex.getMessage(),s3Ex);
			throw s3Ex;
		}
		return true;
	}
	@Override
	public boolean hasSubfolder(final AWSS3Bucket bucket,
								final AWSS3FolderPath folderPath)  {
		log.warn("Check if folder bucket/key={}/{} has at least one subfolder",bucket,folderPath);

		if (!folderPath.asString().contains(AWSS3FolderPath.DELIMITER)) {
			log.warn("... object bucket/key={}/{} was supposed to be a FOLDER but it has NO path delimiter={}",
					 bucket,folderPath,DELIMITER);
			return false;
		}

		ListObjectsV2Request req = _buildListObjectsRequest(bucket, folderPath);		
		ListObjectsV2Response listing = _s3Client.listObjectsV2(req);
		
		//Filtering folders
		Collection<AWSS3ObjectSummary> folderResults = _listFolderContentsOfTypeFolder(listing,
																						bucket,
																						folderPath);
		
		if (CollectionUtils.hasData(folderResults)) return true;

		return false;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	COPY / MOVE / RENAME
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean copyFolder(final AWSS3Bucket bucket,
							  final AWSS3FolderPath srcFolderPath,final AWSS3FolderPath dstFolderPath,
							  final boolean overwrite) {
		
		 ListObjectsV2Request listReq = ListObjectsV2Request.builder()
											                .bucket(bucket.getId())
											                .prefix(srcFolderPath.asString())
											                .build();
		 
		ListObjectsV2Response listRes = _s3Client.listObjectsV2(listReq);

        for (S3Object s3Object : listRes.contents()) {
            String sourceKey = s3Object.key();
            String destinationKey = dstFolderPath.asString() + sourceKey.substring(srcFolderPath.asString().length());

            // Copy each file
            CopyObjectRequest copyReq = CopyObjectRequest.builder()
										                    .sourceBucket(bucket.getId())
										                    .sourceKey(sourceKey)
										                    .destinationBucket(bucket.getId())
										                    .destinationKey(destinationKey)
										                  .build();

            _s3Client.copyObject(copyReq);
            
            log.debug("Copied: {} -> {}", sourceKey, destinationKey);
        }
        
        return true;
	}
	@Override
	public boolean moveFolder(final AWSS3Bucket bucket,
							  final AWSS3FolderPath srcFolderPath,final AWSS3FolderPath dstFolderPath,
							  final boolean overwrite)  {
		// List objects
        ListObjectsV2Request listReq = ListObjectsV2Request.builder()
											                .bucket(bucket.getId())
											                .prefix(srcFolderPath.asString())
											                .build();

        ListObjectsV2Response listRes = _s3Client.listObjectsV2(listReq);
        
        for (S3Object s3Object : listRes.contents()) {
            String sourceKey = s3Object.key();
            String destinationKey = dstFolderPath.asString() + sourceKey.substring(srcFolderPath.asString().length());

            // Copy object
            CopyObjectRequest copyReq = CopyObjectRequest.builder()
											                    .sourceBucket(bucket.getId())
											                    .sourceKey(sourceKey)
											                    .destinationBucket(bucket.getId())
											                    .destinationKey(destinationKey)
										                    .build();

            _s3Client.copyObject(copyReq);
            
            log.debug("Copied: {} -> {}",sourceKey,destinationKey);

            
        }
        
        // Delete source folder
        deleteFolder(bucket,srcFolderPath);        
            
        log.debug("Deleted folder: {}", srcFolderPath);
        
        return true;
	}

/////////////////////////////////////////////////////////////////////////////////////////
//	CREATE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean createFolder(final AWSS3Bucket bucket,
								final AWSS3FolderPath folderPath)  {		
		return createFolder(bucket,folderPath,MimeTypes.OCTECT_STREAM,false); //for folders application/x-directory is good option 
	}
	@Override
	public boolean createFolder(final AWSS3Bucket bucket,
								final AWSS3FolderPath folderPath,
								final MimeType contentType,
								final boolean physicallyExistenceCheck)  {
		Collection<AWSS3FolderPath> fp = AWSS3FolderPath.getAllFoldersForPath(folderPath);
		for (AWSS3FolderPath folder : fp ) {
			boolean existsFolder = this.existsFolder(bucket,
													 folder,
													 true);		// physicallyExistenceCheck
			if (!existsFolder) {
				 log.warn("folder {} does NOT exists: it'll be created",folder);
				 PutObjectRequest req = PutObjectRequest.builder()
							 								.bucket(bucket.asString())
							 								.key(folder.asString())
							 								.contentType(contentType.asString())
						 								.build();
				 RequestBody body = RequestBody.empty();	// there's NO such a thing as folder in s3: an empty object is created
				 _s3Client.putObject(req,
						 			 body);
			}
		}
		return true;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	DELETE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean deleteFolder(final AWSS3Bucket bucket,
								final AWSS3FolderPath folderPath) {
				
		// List objects, must end the key with delimiter '/' because if not, delete all directories started with the same prefix
        ListObjectsV2Request listReq = ListObjectsV2Request.builder()
												                .bucket(bucket.getId())
												                .prefix(folderPath.asString())
											                .build();

        ListObjectsV2Response listRes = _s3Client.listObjectsV2(listReq);
        
        List<ObjectIdentifier> toDelete = listRes.contents().stream()
												                .map(s3Object -> ObjectIdentifier.builder().key(s3Object.key()).build())
												                .collect(Collectors.toList());

        if (!toDelete.isEmpty()) {
            DeleteObjectsRequest deleteReq = DeleteObjectsRequest.builder()
												                    .bucket(bucket.getId())
												                    .delete(d -> d.objects(toDelete))
												                  .build();

            _s3Client.deleteObjects(deleteReq);
            
            log.debug("Deleted folder: {}", folderPath.asString());
        }
        return true;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	LIST
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Collection<AWSS3ObjectSummary> listBucketContents(final AWSS3Bucket bucket,
															 final AWSS3FileFilter fileFilter,
															 final boolean recursive) {
		return this.listFolderContents(bucket,AWSS3FolderPath.fromString(AWSS3FolderPath.DELIMITER),
									   fileFilter,
									   recursive,
									   false);		// do not exclude folders
	}
	@Override
	public Collection<AWSS3ObjectSummary> listFolderContents(final AWSS3Bucket bucket,
															 final AWSS3FolderPath folderPath,
															 final AWSS3FileFilter fileFilter,
															 final boolean recursive,
															 final boolean excludeFolderTypes) {
		
		ListObjectsV2Request req = _buildListObjectsRequest(bucket, folderPath);		
		ListObjectsV2Response listing = _s3Client.listObjectsV2(req);
		
		Collection<AWSS3ObjectSummary> results = Lists.newArrayList();
		
		// [1] - Filter folder results and its children if requested (recursive)
		Collection<AWSS3ObjectSummary> folderResults = _listFolderContentsOfTypeFolder(listing,
																						bucket,
																						folderPath);

		
		if (CollectionUtils.hasData(folderResults)) {
			if (!excludeFolderTypes) {
				results.addAll(folderResults);
			}
			if (recursive) {
				for (final AWSS3ObjectSummary folder : folderResults ) {
					Collection<AWSS3ObjectSummary> contents = listFolderContents(bucket,
																				 AWSS3FolderPath.fromString(folder.getKey().asString()),
																				 null,		// no file filter
																				 recursive,
																				 excludeFolderTypes);
					results.addAll(contents);
				}
			}
		}
		
		// [2] - Filter file results.
		Collection<AWSS3ObjectSummary> fileResults = _listFolderContentsOfTypeFile(listing,
																					bucket,
																					folderPath);
		if (CollectionUtils.hasData(fileResults)) {
			if (fileFilter!= null) {
				results.addAll(fileResults.stream()
										      .filter(p -> fileFilter.accept(Path.valueOf(p.getKey().asString())))
										      .collect(Collectors.toList()));
			} else {
				results.addAll(fileResults);
			}
		}		
		return results;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * This builds a List Object Requests for a given folderpath.
	 * @param bucket
	 * @param folderPath
	 * @return
	 */
	private static ListObjectsV2Request _buildListObjectsRequest(final AWSS3Bucket bucket,
													    	     final AWSS3FolderPath folderPath) {
		ListObjectsV2Request req = folderPath.asString()
										   .equalsIgnoreCase(DELIMITER)
									// Root CASE, that means..bucket level.
									? ListObjectsV2Request.builder()
															.bucket(bucket.asString())
															.delimiter(DELIMITER)
															.build()
									// not root
									: ListObjectsV2Request.builder()
															.bucket(bucket.asString())
															.delimiter(DELIMITER)
															.prefix(folderPath.asString())
															.build();
	   return req;
	}
	/**
	 * From a ObjectListing result, gets the folder objects.
	 * @param listing
	 * @param bucket
	 * @param folderPath
	 * @return
	 */
	private static Collection<AWSS3ObjectSummary> _listFolderContentsOfTypeFolder(final ListObjectsV2Response listing,
																				  final AWSS3Bucket bucket,
																				  final AWSS3FolderPath folderPath) {
		// Filter folder results and its children if requested (recursive)
		Collection<AWSS3ObjectSummary> folderResults = null;
		if (CollectionUtils.hasData(listing.commonPrefixes())) {
			folderResults = listing.commonPrefixes()
								   .stream()
								   .map(prefix -> {
											 AWSS3ObjectSummary folderItem = new AWSS3ObjectSummary();
											 folderItem.setBucket(bucket);
											 folderItem.setKey(AWSS3ObjectKey.forId(prefix.prefix()));
											 folderItem.setFolder(true);
											 return folderItem;
										})
								   .collect(Collectors.toList());
		}
		return folderResults;
	}
	/**
	 * From a ObjectListing result, gets the file objects.
	 * @param listing
	 * @param bucket
	 * @param folderPath
	 * @return
	 */
	private static Collection<AWSS3ObjectSummary> _listFolderContentsOfTypeFile(final ListObjectsV2Response listing,
																		  		final AWSS3Bucket bucket,
																		  	    final AWSS3FolderPath folderPath) {
		if (!listing.hasContents()) {
			return null;
		}

		List<S3Object> s3Objs = listing.contents();
		Collection<AWSS3ObjectSummary> fileResults = s3Objs.stream()
																.map(s3Obj -> {
																		// Remove root folder (prefix) , returned as object.
																		if (s3Obj.key().equalsIgnoreCase(folderPath.asString())) return null;
																		
																		AWSS3ObjectSummary folderItem = new AWSS3ObjectSummary();
																		folderItem.setBucket(bucket);
																		folderItem.setKey(AWSS3ObjectKey.forId(s3Obj.key()));
																		folderItem.setFolder(false);
																		return folderItem;
																		
																	 })
																.filter(n -> n!=null)
																.collect(Collectors.toList());
		return fileResults;
	}
}
