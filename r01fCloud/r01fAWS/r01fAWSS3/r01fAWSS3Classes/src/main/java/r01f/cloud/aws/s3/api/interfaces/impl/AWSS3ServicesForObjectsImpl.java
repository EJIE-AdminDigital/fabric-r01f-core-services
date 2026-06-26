package r01f.cloud.aws.s3.api.interfaces.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.io.IOUtils;

import com.google.common.collect.Maps;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import r01f.cloud.aws.s3.api.interfaces.AWSS3ServicesForObjects;
import r01f.cloud.aws.s3.model.AWSS3Bucket;
import r01f.cloud.aws.s3.model.AWSS3ObjectCopyResult;
import r01f.cloud.aws.s3.model.AWSS3ObjectDeleteResult;
import r01f.cloud.aws.s3.model.AWSS3ObjectGetRequest;
import r01f.cloud.aws.s3.model.AWSS3ObjectGetResult;
import r01f.cloud.aws.s3.model.AWSS3ObjectHeadResult;
import r01f.cloud.aws.s3.model.AWSS3ObjectKey;
import r01f.cloud.aws.s3.model.AWSS3ObjectMetaDataItem;
import r01f.cloud.aws.s3.model.AWSS3ObjectPutRequest;
import r01f.cloud.aws.s3.model.AWSS3ObjectPutResult;
import r01f.cloud.aws.s3.model.AWSS3OperationSettings;
import r01f.cloud.aws.s3.model.AWSS3Range;
import r01f.mime.MimeType;
import r01f.mime.MimeTypes;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;
import r01f.util.types.collections.Lists;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.AbortMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.AbortMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.CompletedMultipartUpload;
import software.amazon.awssdk.services.s3.model.CompletedPart;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.CopyObjectResponse;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;
import software.amazon.awssdk.services.s3.model.GetObjectAttributesRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.ObjectAttributes;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.UploadPartRequest;
import software.amazon.awssdk.services.s3.model.UploadPartResponse;

@Slf4j
public class AWSS3ServicesForObjectsImpl
	 extends AWSS3ServicesBaseImpl
  implements AWSS3ServicesForObjects {
	
	private static final long PART_SIZE = 5 * 1024 * 1024; // 5MB por parte mínimo, tamaño del multipart
	
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AWSS3ServicesForObjectsImpl(final S3Client s3Client)  {
		super(s3Client);
	}
/////////////////////////////////////////////////////////////////////////////////////////
// PUT
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public AWSS3ObjectPutResult putObject(final AWSS3ObjectPutRequest putRequest) {
		return putObject(putRequest.getBucket(), putRequest.getKey(),
				         putRequest.getStreamToUpload(),
				         putRequest.getCustomMetadata(),
				         putRequest.getContentType());
	}

	@Override @SneakyThrows
	public AWSS3ObjectPutResult putObject(final AWSS3Bucket bucket,final AWSS3ObjectKey key,
							   	 	      final InputStream streamToUpload,
							   	 	      final Collection<AWSS3ObjectMetaDataItem> customMetadata) {
		StreamProbe p = _probeEmpty(streamToUpload);
		if (!p.empty) {
			return _putObjectWithMultipartUpload(bucket,key,p.stream,customMetadata,MimeTypes.OCTECT_STREAM);
		} else {
			return putObject(bucket,key,IOUtils.toByteArray(p.stream), customMetadata);
		}
	}
	
	@Override @SneakyThrows
	public AWSS3ObjectPutResult putObject(final AWSS3Bucket bucket,final AWSS3ObjectKey key,
							   	 	      final InputStream streamToUpload,
							   	 	      final Collection<AWSS3ObjectMetaDataItem> customMetadata,
							   	 	      final MimeType contentType) {
		StreamProbe p = _probeEmpty(streamToUpload);
		if (!p.empty) {
			return _putObjectWithMultipartUpload(bucket,key,p.stream,customMetadata,contentType);
		} else {
			return putObject(bucket,key,IOUtils.toByteArray(p.stream),customMetadata,contentType);
		}
	}
	
	@Override
	public AWSS3ObjectPutResult putObject(final AWSS3Bucket bucket,final AWSS3ObjectKey key,
										  final byte[] bytes,
									      final Collection<AWSS3ObjectMetaDataItem> customMetadata) {
		return putObject(bucket,key,bytes,customMetadata,MimeTypes.OCTECT_STREAM); // default mimetype
	}
	
	@Override
	public AWSS3ObjectPutResult putObject(final AWSS3Bucket bucket,final AWSS3ObjectKey key,
										  final File file,
										  final Collection<AWSS3ObjectMetaDataItem> customMetadata) {
		
		return putObject(bucket,key,file,customMetadata,MimeTypes.OCTECT_STREAM); // default mimetype
	}	

	@Override
	public AWSS3ObjectPutResult putObject(final AWSS3Bucket bucket, final AWSS3ObjectKey key,final byte[] bytes,
									      final Collection<AWSS3ObjectMetaDataItem> customMetadata,
									      final MimeType contentType) {
		if (bytes == null) throw new IllegalArgumentException(Strings.customized("The bytes to be stored at bucket/key={}/{} cannot be null!!!",
																  				 bucket,key));
		log.debug("PUT bytes at bucket/key={}/{}",
				 bucket,key);
		PutObjectRequest req = PutObjectRequest.builder()
											   .bucket(bucket.asString())
											   .key(key.asString())
											   // All systems compatible with S3 should provide a metadata system,
											   // ... but there are some that don't, f.e  MINIO
											   .metadata(_customMetadataToMap(customMetadata))
											   .contentType(contentType.asString())
											   .build();
		PutObjectResponse putRes = _s3Client.putObject(req,
													   RequestBody.fromBytes(bytes));
		return AWSS3ObjectPutResult.fromPutObjectResponseOn(bucket,key)
								.with(putRes);
	}
    //https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/best-practices-s3-uploads.html
	private AWSS3ObjectPutResult _putObjectWithMultipartUpload(final AWSS3Bucket bucket, 
															   final AWSS3ObjectKey key,
															   final InputStream streamToUpload,
															   final Collection<AWSS3ObjectMetaDataItem> customMetadata,
															   final MimeType contentType) throws URISyntaxException, IOException {
		if (streamToUpload == null) throw new IllegalArgumentException(Strings.customized("The stream to be uploaded to bucket/key={}/{} cannot be null!!",
																						  		 bucket,key));
		log.warn(".... bucket = {}", bucket);
		log.warn("PUT file at bucket/key={}/{}",
				 bucket,key);
		// [1] - Init the multipart upload
		CreateMultipartUploadRequest createMultipartUploadRequest = CreateMultipartUploadRequest.builder()
																					  			.bucket(bucket.asString())
																					  			.key(key.asString())
																					  			// All systems compatible with S3 should provide a metadata system,
																					  			// ... but there are some that don't, f.e  MINIO
																					  			.metadata(_customMetadataToMap(customMetadata))
																					  			.build();
		CreateMultipartUploadResponse createResponse = _s3Client.createMultipartUpload(createMultipartUploadRequest);
        String uploadId = createResponse.uploadId();
        log.warn("...created multipart upload request with id={}",uploadId);
        
        try {
        	Collection<CompletedPart> completedParts = Lists.newArrayList();
			int partNumber =1;
	        byte[] buffer = new byte[(int) PART_SIZE]; // 5 MB Parts
	        int bytesRead;
	        ByteArrayOutputStream bufferStream = new ByteArrayOutputStream();
	        // [2] Put new Part from streamToUpload
            while ((bytesRead = streamToUpload.read(buffer)) > 0) {
                bufferStream.write(buffer, 0, bytesRead);

                while (bufferStream.size() >= PART_SIZE) {
                    byte[] fullPart = bufferStream.toByteArray();
                    _uploadPart(_s3Client,
                    		   bucket.asString(), key.asString(),
                    		   uploadId,
                    		   fullPart, partNumber++,
                    		   completedParts);

                    bufferStream.reset();
                    bufferStream.write(fullPart, (int) PART_SIZE, fullPart.length - (int) PART_SIZE);
                }
            }
            streamToUpload.close();
            // [3] Upload final Part
            if (bufferStream.size() > 0) {
                _uploadPart(_s3Client,
                			bucket.asString(), key.asString(),
                			uploadId,
                			bufferStream.toByteArray(), partNumber++,
                			completedParts);
            }
			// [4] - Call completeMultipartUpload operation to tell S3 to merge all uploaded
			// 		 parts and finish the multipart operation.
			CompletedMultipartUpload completedMultipartUpload = CompletedMultipartUpload.builder()
																						.parts(completedParts)
																						.build();
			
			CompleteMultipartUploadRequest completeMultipartUploadReq = CompleteMultipartUploadRequest.builder()
																									  .bucket(bucket.asString())
																									  .key(key.asString())
																									  .uploadId(uploadId)
																									  .multipartUpload(completedMultipartUpload)
																									  .build();
			CompleteMultipartUploadResponse res = _s3Client.completeMultipartUpload(completeMultipartUploadReq);
			// [5] - Return
			return AWSS3ObjectPutResult.fromPutObjectResponseOn(bucket,key)
										.with(res);
        } catch (final Throwable e) {
            // Manejo de errores: aborta la subida si falla algo
            AbortMultipartUploadResponse res = _s3Client.abortMultipartUpload(AbortMultipartUploadRequest.builder()
																						                 .bucket(bucket.asString())
																						                 .key(key.asString())
																						                 .uploadId(uploadId)
																						                 .build());
            log.error("Error durante la carga multipart: {}",e);
            // [5] - Return
            return AWSS3ObjectPutResult.fromPutObjectResponseOn(bucket,key)
										.with(res);
        }
	}
		//https://docs.aws.amazon.com/es_es/sdk-for-java/latest/developer-guide/transfer-manager.html
//	@SuppressWarnings("unused")
//	private AWSS3ObjectPutResult _putObjectWithS3Transfer(final AWSS3Bucket bucket, 
//														  final AWSS3ObjectKey key,
//														  final InputStream streamToUpload,
//														  final Collection<AWSS3ObjectMetaDataItem> customMetadata,
//														  final MimeType contentType) throws URISyntaxException, IOException {
//		S3Configuration s3Configuration = S3Configuration.builder()
//				                                         .pathStyleAccessEnabled(true)
//				                                         .checksumValidationEnabled(false)
//				                                         .build();
//		S3AsyncClient s3AsyncClient = S3AsyncClient.builder()
//												   .region(Region.EU_WEST_1)
//		                                           .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create("6VPW0XG22OBYLSV0LWCK",
//				                                                                                                                    "Tnn6nzTQU7VdOI7Re/TyjsFDiwZqB2zIYgSaMjs0")))
//		                                           .serviceConfiguration(s3Configuration)
//		                                           .endpointOverride(new URI("http://s3.itbatera.euskadi.eus"))
//                								   .build();
//		S3TransferManager s3TransferManager = S3TransferManager.builder()
//                		 .s3Client(s3AsyncClient)
//                		 .build();
//		
//		Path tempFile = Paths.get("/temp/upload-"+Calendar.getInstance().getTimeInMillis());
//	    try (OutputStream os = Files.newOutputStream(tempFile)) {
//	        streamToUpload.transferTo(os);
//	    }
//		
//		UploadFileRequest uploadFileRequest = UploadFileRequest.builder()
//														       .putObjectRequest(b -> b
//														       .bucket(bucket.asString())
//														       .key(key.asString()))
//														       .source(tempFile)
//														       .build();
//		FileUpload fileUpload = s3TransferManager.uploadFile(uploadFileRequest);
//        CompletedFileUpload completedFileUpload = fileUpload.completionFuture().join();
//        s3TransferManager.close();
//        Files.deleteIfExists(tempFile);
//		return AWSS3ObjectPutResult.fromPutObjectResponseOn(bucket,key)
//								   .with(completedFileUpload.response());
//	}
	@Override
	public AWSS3ObjectPutResult putObject(final AWSS3Bucket bucket,final AWSS3ObjectKey key,
							     	      final File file,
							     	      final Collection<AWSS3ObjectMetaDataItem> customMetadata,
							     	      final MimeType contentType) {
		if (file == null) throw new IllegalArgumentException(Strings.customized("The file to be stored at bucket/key={}/{} cannot be null!!!",
																  				bucket,key));
		log.debug("PUT file at bucket/key={}/{}",
				 bucket,key);
		PutObjectRequest req = PutObjectRequest.builder()
											   .bucket(bucket.asString())
											   .key(key.asString())
											   // All systems compatible with S3 should provide a metadata system,
											   // ... but there are some that don't, f.e  MINIO
											   .metadata(_customMetadataToMap(customMetadata))
											   .contentType(contentType.asString())
											   .build();
		PutObjectResponse putRes = _s3Client.putObject(req,
													   RequestBody.fromFile(file));
		return AWSS3ObjectPutResult.fromPutObjectResponseOn(bucket,key)
								  .with(putRes);
	}
/////////////////////////////////////////////////////////////////////////////////////////
// APPEND
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override @SneakyThrows
	public AWSS3ObjectPutResult appendToObject(final AWSS3Bucket bucket,final AWSS3ObjectKey key,
									   	 	   final InputStream contentToAppendStream,
									   	 	   final Collection<AWSS3ObjectMetaDataItem> customMetadata) {
			
		if (contentToAppendStream == null) throw new IllegalArgumentException(Strings.customized("The stream to be uploaded to bucket/key={}/{} cannot be null!!",
																						  			bucket,key));
		log.info("APPEND stream on bucket/key={}/{}",
				 bucket,key);
		
		// [1] - Init the multipart upload
		CreateMultipartUploadRequest multipartUploadReq = CreateMultipartUploadRequest.builder()
																					  .bucket(bucket.asString())
																					  .key(key.asString())
																					  // All systems compatible with S3 should provide a metadata system,
																					  // ... but there are some that don't, f.e  MINIO
																					  .metadata(_customMetadataToMap(customMetadata))
																					  .build();
		
		CreateMultipartUploadResponse multipartUploadRes = _s3Client.createMultipartUpload(multipartUploadReq);
		String uploadId = multipartUploadRes.uploadId();
		
		
		log.debug("...created multipart upload request with id={}",uploadId);
		
		
		try {
	            
	        // [2] Leer el archivo existente
			Collection<CompletedPart> completedParts = Lists.newArrayList();
			int partNumber =1;
	        
			GetObjectRequest getRequest = GetObjectRequest.builder()
											                .bucket(bucket.asString())
											                .key(key.asString())
											                .build();
			InputStream existingContentStream = null;
			try {
				existingContentStream = _s3Client.getObject(getRequest);
			} catch (NoSuchKeyException ex) {
				// file does not exists, create new file with appending
			}
	        
	        byte[] buffer = new byte[(int) PART_SIZE]; // 5 MB Parts
	        int bytesRead;
	        ByteArrayOutputStream bufferStream = new ByteArrayOutputStream();
	        
	        if (existingContentStream != null) {
		        while ((bytesRead = existingContentStream.read(buffer)) > 0) {
		        	
		        	bufferStream.write(buffer, 0, bytesRead);
		        	
		        	while (bufferStream.size() >= PART_SIZE) {
	                    byte[] fullPart = bufferStream.toByteArray();
	                    _uploadPart(_s3Client,
	                    		   bucket.asString(), key.asString(),
	                    		   uploadId,
	                    		   fullPart, partNumber++,
	                    		   completedParts);
	
	                    // Dejamos solo los bytes restantes que no alcanzan 5MB
	                    bufferStream.reset();
	                    bufferStream.write(fullPart, (int) PART_SIZE, fullPart.length - (int) PART_SIZE);
	                }
		        }
	
		        existingContentStream.close();
	        }
	        
	        // [3] Agregar la nueva parte desde el InputStream
            while ((bytesRead = contentToAppendStream.read(buffer)) > 0) {
                bufferStream.write(buffer, 0, bytesRead);

                while (bufferStream.size() >= PART_SIZE) {
                    byte[] fullPart = bufferStream.toByteArray();
                    _uploadPart(_s3Client,
                    		   bucket.asString(), key.asString(),
                    		   uploadId,
                    		   fullPart, partNumber++,
                    		   completedParts);

                    bufferStream.reset();
                    bufferStream.write(fullPart, (int) PART_SIZE, fullPart.length - (int) PART_SIZE);
                }
            }

            contentToAppendStream.close();

            // [4] Subimos la parte final (puede ser menor a 5MB), el resto deben ser completas
            if (bufferStream.size() > 0) {
                _uploadPart(_s3Client,
                			bucket.asString(), key.asString(),
                			uploadId,
                			bufferStream.toByteArray(), partNumber++,
                			completedParts);
            }
	        
			// [5] - call completeMultipartUpload operation to tell S3 to merge all uploaded
			// 		 parts and finish the multipart operation.
			CompletedMultipartUpload completedMultipartUpload = CompletedMultipartUpload.builder()
																						.parts(completedParts)
																						.build();
			
			CompleteMultipartUploadRequest completeMultipartUploadReq = CompleteMultipartUploadRequest.builder()
																									  .bucket(bucket.asString())
																									  .key(key.asString())
																									  .uploadId(uploadId)
																									  .multipartUpload(completedMultipartUpload)
																									  .build();
			
			CompleteMultipartUploadResponse res = _s3Client.completeMultipartUpload(completeMultipartUploadReq);
			
			// [4] - Return
			return AWSS3ObjectPutResult.fromPutObjectResponseOn(bucket,key)
										.with(res);
			
		} catch (Exception e) {
            // Manejo de errores: aborta la subida si falla algo
            AbortMultipartUploadResponse res = _s3Client.abortMultipartUpload(AbortMultipartUploadRequest.builder()
																						                    .bucket(bucket.asString())
																						                    .key(key.asString())
																						                    .uploadId(uploadId)
																						                    .build());

            log.error("Error durante la carga multipart: {}",e);
            
            // [4] - Return
            return AWSS3ObjectPutResult.fromPutObjectResponseOn(bucket,key)
										.with(res);
        }	
	}
/////////////////////////////////////////////////////////////////////////////////////////
// PUT BIG OBJECT
/////////////////////////////////////////////////////////////////////////////////////////		
	@Override
	public AWSS3ObjectPutResult putHugeObject(final AWSS3Bucket bucket,final AWSS3ObjectKey key,
				    					  	  final File file,
				    					  	  final AWSS3OperationSettings operationSettings) {		
		return this.putHugeObject(bucket,key,file,operationSettings,null);
	}
    @Override
	public AWSS3ObjectPutResult putHugeObject(final AWSS3Bucket bucket,final AWSS3ObjectKey key,
				    					  	  final File file,
				    					  	  final AWSS3OperationSettings operationSettings,
				    					  	  final Collection<AWSS3ObjectMetaDataItem> customMetadata) {
    	return this.putHugeObject(bucket,key,file,operationSettings,null,MimeTypes.OCTECT_STREAM);
    }    
//	@Override
//	public void putHugeObject(final AWSS3Bucket bucket,final AWSS3ObjectKey key,
//    					  	  final File file,
//    					  	  final AWSS3OperationSettings operationSettings,
//    					  	  final Collection<AWSS3ObjectMetaDataItem> customMetadata,
//    					  	  final MimeType contentType) {
//		log.info("Transfer file to bucket/key={}/{}",
//				 bucket,key);
//		try {
//			S3TransferManager transferManager = S3TransferManager.create();
//	        UploadFileRequest uploadFileRequest = UploadFileRequest.builder()
//														            .putObjectRequest(b -> b
//														                .bucket(bucket.asString())
//														                .key(key.asString()))
//														            .source(file)
//														            .build();
//	        FileUpload fileUpload = transferManager.uploadFile(uploadFileRequest);
//	        fileUpload.completionFuture().join();
//	        transferManager.close();
//		} catch (Exception e) {
//			System.out.println("Error" + e.getMessage());		
//		}
//	}
	@Override
	public AWSS3ObjectPutResult putHugeObject(final AWSS3Bucket bucket,final AWSS3ObjectKey key,
				    					  	  final File file,
				    					  	  final AWSS3OperationSettings operationSettings,
				    					  	  final Collection<AWSS3ObjectMetaDataItem> customMetadata,
				    					  	  final MimeType contentType) {
		
		log.info("PUT BIG file on bucket/key={}/{}",
				 bucket,key);

		// User multi-part uploading
		// [1] - Init the multipart upload
		CreateMultipartUploadRequest multipartUploadReq = CreateMultipartUploadRequest.builder()
																					  .bucket(bucket.asString())
																					  .key(key.asString())
																					  // All systems compatible with S3 should provide a metadata system,
																					  // ... but there are some that don't, f.e  MINIO
																					  .metadata(_customMetadataToMap(customMetadata))
																					  .build();
		
		CreateMultipartUploadResponse multipartUploadRes = _s3Client.createMultipartUpload(multipartUploadReq);
		String multipartUploadId = multipartUploadRes.uploadId();
		log.debug("...created multipart upload request with id={}",multipartUploadId);

		// [2] - Upload individual parts
		Collection<CompletedPart> completedParts = Lists.newArrayList();
		int currPartNum = 1;
		byte[] buffer = new byte[(int) PART_SIZE];	// 5Mb buffer
		
		try (FileInputStream streamToUpload = new FileInputStream(file)) {
			int readed = streamToUpload.read(buffer);
			while (readed > 0) {
				log.debug("\t- part {}",currPartNum);
				UploadPartRequest uploadPartReq = UploadPartRequest.builder()
																   .bucket(bucket.asString())
																   .key(key.asString())
																   .uploadId(multipartUploadId)
																   .partNumber(1)
																   .build();
				String etag = _s3Client.uploadPart(uploadPartReq,
												   RequestBody.fromByteBuffer(ByteBuffer.wrap(buffer,0,readed)))
									   .eTag();
				CompletedPart completedPart = CompletedPart.builder()
														   .partNumber(currPartNum)
														   .eTag(etag)
														   .build();
				completedParts.add(completedPart);
	
				// next part
				readed = streamToUpload.read(buffer);
				currPartNum = currPartNum + 1;
			}		
		
			if (CollectionUtils.isNullOrEmpty(completedParts)) throw new IllegalStateException("No parts to be uploded at bucket/key=" + bucket + "/" + key + ": NO data!!");
	
			// [3] - call completeMultipartUpload operation to tell S3 to merge all uploaded
			// 		 parts and finish the multipart operation.
			CompletedMultipartUpload completedMultipartUpload = CompletedMultipartUpload.builder()
																						.parts(completedParts)
																						.build();
			CompleteMultipartUploadRequest completeMultipartUploadReq = CompleteMultipartUploadRequest.builder()
																									  .bucket(bucket.asString())
																									  .key(key.asString())
																									  .uploadId(multipartUploadId)
																									  .multipartUpload(completedMultipartUpload)
																									  .build();
			CompleteMultipartUploadResponse res = _s3Client.completeMultipartUpload(completeMultipartUploadReq);
	
			// [4] - Return
			return AWSS3ObjectPutResult.fromPutObjectResponseOn(bucket,key)
								.with(res);
			
		} catch (Exception e) {
            // Manejo de errores: aborta la subida si falla algo
            AbortMultipartUploadResponse res = _s3Client.abortMultipartUpload(AbortMultipartUploadRequest.builder()
																						                    .bucket(bucket.asString())
																						                    .key(key.asString())
																						                    .uploadId(multipartUploadId)
																						                    .build());

            log.error("Error durante la carga multipart: {}",e);
            
            // [4] - Return
            return AWSS3ObjectPutResult.fromPutObjectResponseOn(bucket,key)
										.with(res);
        }	
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	HEAD
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public AWSS3ObjectHeadResult headObject(final AWSS3Bucket bucket, final AWSS3ObjectKey key) {
		log.debug("HEAD on object at bucket/key={}/{}",
				                                    bucket,key);
	  //The HEAD operation retrieves metadata from an object without returning the object itself.
	  //This operation is useful if you're only interested in an object's metadata. To use HEAD, you must have READ access to the object.
	  //A HEAD request has the same options as a GET operation on an object.
	  //The response is identical to the GET response except that there is no response body.
		HeadObjectRequest headReq = HeadObjectRequest.builder()
														 .bucket(bucket.asString())
														 .key(key.asString())
													 .build();
		HeadObjectResponse headRes = _s3Client.headObject(headReq);
		return AWSS3ObjectHeadResult.fromHeadResponseOn(bucket,key)
								      .with(headRes);
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	GET
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public AWSS3ObjectGetResult getObject(final AWSS3ObjectGetRequest getRequest) {
		return getObject(getRequest.getBucket(), getRequest.getKey(), getRequest.getRange());
	}
	
	@Override @SuppressWarnings("resource")
	public AWSS3ObjectGetResult getObject(final AWSS3Bucket bucket,final AWSS3ObjectKey key,final AWSS3Range range) {
		log.info("GET object at bucket/key={}/{}/{}",
				 bucket,key, range);
		return _getObject(bucket, key, Optional.ofNullable(range));
	}
	
	@Override @SuppressWarnings("resource")
	public AWSS3ObjectGetResult getObject(final AWSS3Bucket bucket,final AWSS3ObjectKey key) {
		log.info("GET object at bucket/key={}/{}",
				 bucket,key);
		return _getObject(bucket, key, Optional.empty());
	}
	
	private AWSS3ObjectGetResult _getObject(final AWSS3Bucket bucket,final AWSS3ObjectKey key,final Optional<AWSS3Range> rangeOptional) {
		log.info("GET object at bucket/key={}/{}/{}",
				 bucket,key, rangeOptional);
		
		GetObjectRequest.Builder reqBuilder = GetObjectRequest.builder()
                .bucket(bucket.asString())
                .key(key.asString());
        rangeOptional.ifPresent(range -> reqBuilder.range(range.asString()));
        GetObjectRequest req = reqBuilder.build();

		ResponseInputStream<GetObjectResponse> resIs = _s3Client.getObject(req,
																		   ResponseTransformer.toInputStream());

		return AWSS3ObjectGetResult.fromGetObjectResponseOn(bucket,key)
								   .returning(resIs);
	}


	@Override
	public void getHugeObject(final AWSS3Bucket bucket,final AWSS3ObjectKey key,
							  final AWSS3OperationSettings operationSettings) {
		throw new UnsupportedOperationException("Not yet implemented!");		// see https://github.com/aws/aws-sdk-java-v2/tree/master/docs/design/services/s3/transfermanager
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	DELETE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public AWSS3ObjectDeleteResult deleteObject(final AWSS3Bucket bucket,final AWSS3ObjectKey key) {
		log.info("DELETE object at bucket/key={}/{}",
				 bucket,key);
		DeleteObjectRequest req = DeleteObjectRequest.builder()
													 .bucket(bucket.toString())
													 .key(key.asString())
													 .build();
		DeleteObjectResponse res = _s3Client.deleteObject(req);
		return AWSS3ObjectDeleteResult.fromDeleteObjectResponseOn(bucket,key)
							 		  .with(res);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	COPY
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public AWSS3ObjectCopyResult copyObject(final AWSS3Bucket srcBucket,final AWSS3ObjectKey srcKey,
											final AWSS3Bucket dstBucket,final AWSS3ObjectKey dstKey) {
		log.info("COPY object from bucket/key={}/{} to bucket/key={}/{}",
				 							  srcBucket,srcKey,dstBucket,dstKey);
		CopyObjectRequest req = CopyObjectRequest.builder()
													 .sourceBucket(srcBucket.toString())													 
													 .sourceKey(srcKey.asString())
													 .destinationBucket(dstBucket.toString())
													 .destinationKey(dstKey.asString())
													 .build();
		
		CopyObjectResponse res = _s3Client.copyObject(req);
		
		return AWSS3ObjectCopyResult.fromCopyObjectResponseOn(srcBucket,srcKey)
										.to(res, dstBucket, dstKey);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	ATTRIBUTES
/////////////////////////////////////////////////////////////////////////////////////////	
	public GetObjectAttributesRequest getObjectAttributes(final AWSS3Bucket srcBucket,final AWSS3ObjectKey srcKey) {
		log.warn("Attributes for bucket {} and object {}",
				 srcBucket.toString(),srcKey.asString());
		
		 GetObjectAttributesRequest getObjectAttributesRequest = GetObjectAttributesRequest.builder()
																		                    .bucket(srcBucket.toString())
																		                    .key(srcKey.asString())
																		                    .objectAttributes(ObjectAttributes.E_TAG, ObjectAttributes.STORAGE_CLASS,
																		                            			ObjectAttributes.OBJECT_SIZE)
																		                    .build();
		 return getObjectAttributesRequest;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	STREAMPROBE
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * A simple holder class that represents the result of probing an InputStream
	 * to determine whether it contains data or is empty.
	 * <p>
	 * It wraps a usable {@link InputStream} instance (which may be the same as the
	 * original or a wrapped version such as {@link PushbackInputStream}) and a flag
	 * indicating whether the stream was found to be empty.
	 * <p>
	 * The {@code stream} returned is always safe to use for subsequent reads.
	 * If a byte was read during the probe, it has already been "unread" so that
	 * no data is lost.
	 */
	private static final class StreamProbe {
	    final InputStream stream;
	    final boolean empty;
	    StreamProbe(final InputStream stream, final boolean empty) { 
	    	this.stream = stream; 
	    	this.empty = empty; 
	    }
	}
	/**
	 * Probes the given {@link InputStream} to determine whether it is empty,
	 * without consuming its contents.
	 * <p>
	 * This method reads a single byte from the stream to check for EOF. If data is
	 * available, the byte is "unread" using {@link PushbackInputStream#unread(int)}
	 * so that the caller can safely continue reading the stream from the beginning.
	 * <p>
	 * If the input stream does not support pushback, it will be wrapped in a
	 * {@link PushbackInputStream} internally.
	 * <p>
	 * <strong>Note:</strong> The returned {@code StreamProbe.stream} must always be
	 * used for subsequent reads instead of the original stream reference.
	 *
	 * @param in the input stream to probe; may be {@code null}
	 * @return a {@link StreamProbe} containing a usable stream and an "empty" flag
	 * @throws IOException if an I/O error occurs while reading or unreading the byte
	 */
	private StreamProbe _probeEmpty(final InputStream in) throws IOException {
	    if (in == null) return new StreamProbe(new ByteArrayInputStream(new byte[0]), true);
	
	    PushbackInputStream pb = (in instanceof PushbackInputStream)
	            ? (PushbackInputStream) in
	            : new PushbackInputStream(in, 1);
	
	    int b = pb.read();
	    if (b == -1) {
	        return new StreamProbe(pb, true);   // vacío
	    } else {
	        pb.unread(b);                       // devolvemos el byte
	        return new StreamProbe(pb, false);  // no vacío
	    }
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	UTIL
/////////////////////////////////////////////////////////////////////////////////////////
	private static Map<String,String> _customMetadataToMap(final Collection<AWSS3ObjectMetaDataItem> metadata) {
		Map<String,String> outMetaData = null;
		if (CollectionUtils.hasData(metadata)) {
			outMetaData = Maps.newHashMapWithExpectedSize(metadata.size());
			for (AWSS3ObjectMetaDataItem item : metadata) {
				outMetaData.put(item.getId().asString(),
							    item.getValue());
			}
		} else {
			outMetaData = Maps.newHashMap();
		}
		return outMetaData;
	}
	private static void _uploadPart(final S3Client s3,
								    final String bucketName, final String objectKey,
								    final String uploadId,
                                    final byte[] data, final int partNumber,
                                    final Collection<CompletedPart> completedParts) {

        UploadPartResponse uploadPartResponse = s3.uploadPart(UploadPartRequest.builder()
														                        .bucket(bucketName)
														                        .key(objectKey)
														                        .uploadId(uploadId)
														                        .partNumber(partNumber)
														                        .build(),
														      RequestBody.fromBytes(data));

        completedParts.add(CompletedPart.builder()
			                .partNumber(partNumber)
			                .eTag(uploadPartResponse.eTag())
			                .build());

        log.trace("\t ---> Parte {} subida con {} bytes.", partNumber, data.length);
    }
	
}
