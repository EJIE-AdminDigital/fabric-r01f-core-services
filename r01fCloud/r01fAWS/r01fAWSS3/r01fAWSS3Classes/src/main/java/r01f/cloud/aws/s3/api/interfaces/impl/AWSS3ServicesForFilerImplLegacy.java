package r01f.cloud.aws.s3.api.interfaces.impl;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;
import r01f.cloud.aws.s3.api.interfaces.AWSS3ServicesForFiler;
import r01f.cloud.aws.s3.model.AWSS3Bucket;
import r01f.cloud.aws.s3.model.AWSS3FileFilter;
import r01f.cloud.aws.s3.model.AWSS3FolderPath;
import r01f.cloud.aws.s3.model.AWSS3ObjectKey;
import r01f.cloud.aws.s3.model.AWSS3ObjectSummary;
import r01f.mime.MimeType;
import r01f.mime.MimeTypes;
import r01f.util.types.collections.CollectionUtils;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.S3Object;

@Slf4j
@Deprecated
public class AWSS3ServicesForFilerImplLegacy
	extends AWSS3ServicesBaseImpl
  implements AWSS3ServicesForFiler {
/////////////////////////////////////////////////////////////////////////////////////////
// Includes to see [legacy version, based on aw3.v1...remove as soon as posible.] CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AWSS3ServicesForFilerImplLegacy(final S3Client s3Client)  {
		super(s3Client);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	METHODS TO IMPLEMENT
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Checks:
	 * 	 > Checks the folder has a logical existence within a hierarchy as part of the key of a file.
	 *   > Checks the folder has a physical existence with certain characteristics, the key has the common delimiter, it has 0 bytes.
	 */
	@Override
	public boolean existsFolder(final AWSS3Bucket bucketName,
								final AWSS3FolderPath folderPath,
								final boolean  physicallyExistenceCheck)  {
		log.warn("\nCheck if folder '{}'  exist into bucket '{}' ", folderPath, bucketName );
	    //	Checks the folder has a logical existence within a hierarchy as part of the key of a file.
		boolean folderExistsLogically = CollectionUtils.hasData(listFolderContents(bucketName, folderPath, null, false));
		if (! physicallyExistenceCheck  ) {//|| (physicallyExistenceCheck && !folderExistsLogically ) ) {
			return folderExistsLogically;
		}
		 //	Checks the folder has a physical existence with certain characteristics, the key has the common delimiter, it has 0 bytes.
		try {
		    /*ObjectMetadata objectMetadata =
						_s3Client.getObjectMetadata(bucketName.asString(), folderPath.asString());
		    if (! objectMetadata.getContentType().equals(Mimetypes.MIMETYPE_OCTET_STREAM)) {
		    	log.warn("..content type of supposed folder '{}' is not octect stream", path.asString() );
		    	return false;
		    }
		    if (objectMetadata.getContentLength() > 0) {
		    	log.warn("..content length of supposed folder '{}' has more than 0 bytes...", path.asString() );
		    	return false;
		    }*/
		    if (! folderPath.asString().contains(AWSS3FolderPath.DELIMITER)) {
		    	log.warn("...Not delimiter found '{}'",folderPath.asString());
		    	return false;
		    }
		} catch (final S3Exception s3Exception) {
				log.error(" Error {}",s3Exception.awsErrorDetails());
				throw s3Exception;
		}

		return true;
	}
	
		@Override
	public boolean existsFolder(final AWSS3Bucket bucket, final AWSS3FolderPath path) {
		throw new UnsupportedOperationException(">No implemented yet");
	}
		
	@Override
	public boolean copyFolder(final AWSS3Bucket bucket,
							  final AWSS3FolderPath srcPath,final AWSS3FolderPath dstPath,
							  final boolean overwrite) {
		throw new UnsupportedOperationException(">No implemented yet");
	}


	@Override
	public boolean moveFolder(final AWSS3Bucket bucket,
							  final AWSS3FolderPath srcPath,final AWSS3FolderPath dstPath,
							  final boolean overwrite)  {
		throw new UnsupportedOperationException(">No implemented yet");
	}


	@Override
	public boolean createFolder(final AWSS3Bucket bucket,
								final AWSS3FolderPath path)  {		
		return createFolder(bucket,path,MimeTypes.OCTECT_STREAM,false); //for folders application/x-directory is good option 
	}
	
	@Override
	public boolean createFolder(final AWSS3Bucket bucket,
								final AWSS3FolderPath path,
								final MimeType contentType,
								final boolean physicallyExistenceCheck)  {
		Collection<AWSS3FolderPath> fp = AWSS3FolderPath.getAllFoldersForPath(path);
		for (AWSS3FolderPath folder : fp ) {
			if (! existsFolder(bucket, folder, true)) {
				 log.warn(" '{}' folder does not exist, so will be created",folder);
				 log.warn("folder {} does NOT exists: it'll be created",folder);
				 PutObjectRequest req = PutObjectRequest.builder()
						 								.bucket(bucket.asString())
						 								.key(folder.asString())
						 								.contentType(contentType.toString())
						 								.build();
				 RequestBody body = RequestBody.empty();	// there's NO such a thing as folder in s3: an empty object is created
				 _s3Client.putObject(req,
						 			 body);
			}
		}
		return true;
	}

	@Override
	public boolean deleteFolder(final AWSS3Bucket bucket,
								final AWSS3FolderPath path) {
		throw new UnsupportedOperationException(">No implemented yet");
	}

	public Collection<AWSS3ObjectSummary> listFolderContents(final AWSS3Bucket bucket,
			                                                 final AWSS3FolderPath folderPath,
			                                                 final boolean recursive) {
		return listFolderContents(bucket,folderPath,null,recursive);
	}

	public Collection<AWSS3ObjectSummary> listFolderContents(final AWSS3Bucket bucket,
			                                                 final AWSS3FolderPath folderPath,
			                                                 final boolean recursive, final boolean excludeFolderTypes) {
		return listFolderContents(bucket,folderPath,null,recursive,excludeFolderTypes);
	}


	public Collection<AWSS3ObjectSummary> listFolderContents(final AWSS3Bucket bucket,
			                                                 final AWSS3FolderPath folderPath,final AWSS3FileFilter fileFilter,
			                                                 final boolean recursive) {
		return  listFolderContents(bucket,folderPath,fileFilter,recursive,false);
	}


	@Override
	public Collection<AWSS3ObjectSummary> listFolderContents(final AWSS3Bucket bucket,
			                                                 final AWSS3FolderPath folderPath,final AWSS3FileFilter fileFilter,
			                                                 final boolean recursive,  final boolean excludeFolderTypes) {
		AWSS3FolderPath prefix = folderPath;
		ListObjectsRequest req = _buildListObjectsRequest(bucket,prefix);
		ListObjectsResponse listing = _s3Client.listObjects(req);
		List<AWSS3ObjectSummary> results = Lists.newArrayList();
		// First, Filter folder results and its children if requested (recursive)
		Collection<AWSS3ObjectSummary> folderResults = _listFolderContentsOfTypeFolder(listing,bucket,prefix);
		if (CollectionUtils.hasData(folderResults)) {
			if (!excludeFolderTypes) {
				results.addAll(folderResults);
			}
			if (recursive) {
				for (AWSS3ObjectSummary folder : folderResults ) {
					results.addAll(listFolderContents(bucket, AWSS3FolderPath.fromString(folder.getKey().asString()), null, recursive,excludeFolderTypes));
				}
			}
		}
		// Filter file results.
		Collection<AWSS3ObjectSummary> fileResults = _listFolderContentsOfTypeFile(listing,bucket,prefix);
		if (CollectionUtils.hasData(fileResults)) {
			results.addAll(fileResults);
		}
		return results;
	}



	@Override
	public Collection<AWSS3ObjectSummary> listBucketContents(final AWSS3Bucket bucket,
															 final AWSS3FileFilter fileFilter,
			                                                 final boolean recursive) {
		return this.listFolderContents(bucket,
									   AWSS3FolderPath.fromString("/"),
									   fileFilter,
									   recursive);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * This builds a List Object Reques for a given folderpath.
	 * @param bucket
	 * @param folderPath
	 * @return
	 */
	private static ListObjectsRequest _buildListObjectsRequest(final AWSS3Bucket bucket,
													           final AWSS3FolderPath folderPath) {
		ListObjectsRequest req =
				(folderPath.asString()
							.equalsIgnoreCase(AWSS3FolderPath.DELIMITER))
									// Root CASE, that means..bucket level.
									? ListObjectsRequest.builder()
												.bucket(bucket.asString())
												.delimiter(AWSS3FolderPath.DELIMITER)
												.build()
									// not root
									: ListObjectsRequest.builder()
							                      .bucket(bucket.asString())
							                      .prefix(folderPath.asString())
							                      .delimiter(AWSS3FolderPath.DELIMITER)
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
	@SuppressWarnings("static-method")
	private Collection<AWSS3ObjectSummary> _listFolderContentsOfTypeFolder(final ListObjectsResponse listing ,
																		   final AWSS3Bucket bucket,
                                                                           final AWSS3FolderPath folderPath) {
		// First, Filter folder results and its children if requested (recursive)
		List<AWSS3ObjectSummary> folderResults = null;
		if (CollectionUtils.hasData(listing.commonPrefixes())) {
			folderResults = listing.commonPrefixes()
								   .stream()
								   .map(input -> {
											 AWSS3ObjectSummary folderItem = new AWSS3ObjectSummary();
										     folderItem.setBucket(bucket);
										     folderItem.setKey(AWSS3ObjectKey.forId(input.prefix()));
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
	private static Collection<AWSS3ObjectSummary> _listFolderContentsOfTypeFile(final ListObjectsResponse listing ,
																		  		final AWSS3Bucket bucket,
																		  		final AWSS3FolderPath folderPath) {
		if (CollectionUtils.isNullOrEmpty(listing.contents())) return null;
		
		List<S3Object> summary = listing.contents();

        // Remove root folder (prefix) , returned as object.
		// First , find the root element.
		Collection<S3Object> toRemove = Collections2.filter(summary,
															input -> input.key().equalsIgnoreCase(folderPath.asString()));
		// ..and then, remove from summary ( "toRemove" is a list with just one element)
		if (CollectionUtils.hasData(toRemove)) {
		  summary.removeAll(toRemove);
		}

		return summary.stream()
					  .map(input -> {
								 AWSS3ObjectSummary folderItem = new AWSS3ObjectSummary();
							     folderItem.setBucket(bucket);
							     folderItem.setKey(AWSS3ObjectKey.forId(input.key()));
							     folderItem.setFolder(false);
							     return folderItem;
						   })
					  .collect(Collectors.toList());
	}
	@Override
	public boolean hasSubfolder(final AWSS3Bucket bucket, final AWSS3FolderPath path) {
		// TODO Auto-generated method stub
		return false;
	}

}
