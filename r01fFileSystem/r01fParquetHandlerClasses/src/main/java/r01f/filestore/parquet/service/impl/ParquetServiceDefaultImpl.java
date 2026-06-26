package r01f.filestore.parquet.service.impl;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

import org.apache.parquet.hadoop.metadata.CompressionCodecName;
import org.apache.parquet.schema.MessageType;

import blue.strategic.parquet.Dehydrator;
import r01f.filestore.parquet.service.ParquetMapper;
import r01f.filestore.parquet.service.ParquetServiceBase;
import r01f.filestore.parquet.service.ParquetWriterResult;
import r01f.filestore.parquet.writter.ParquetWriter;
import r01f.types.Path;

public class ParquetServiceDefaultImpl<T>
	 extends ParquetServiceBase<T> {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
    public ParquetServiceDefaultImpl(final ParquetMapper<T> mapper) { 
        super(mapper); 
    }
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public ParquetWriterResult write(final Iterable<T> data,
    				                 final Path filePathInput) {
        try {        	
        	java.nio.file.Path filePath = filePathInput.asJavaNIOPath();
            // [1] Obtain Parquet idl schema           
            MessageType schema = _mapper.getMessageTypeSchema();
            
            // [2] Define (Blue Strategic) Dehydrator
            Dehydrator<T> dehydrator = (item,valWriter) -> {
            								_mapper.mapToValueWriter(item, valWriter);
            							};

            // [3] Write to ZSTD compress format  [ Ocean Libray don't expose this method, so  ParquetWriter has been rewrited)         
            long exportedRecordsCount = 0;
            try (ParquetWriter<T> writer = ParquetWriter.writeFile(schema,
												                   filePath.toFile(), 
												                   dehydrator,
												                   CompressionCodecName.ZSTD)) {
            	
                for (T item : data) {
                    writer.write(item);
                    exportedRecordsCount++;
                }
            }
            
            return  new ParquetWriterResult(filePathInput,exportedRecordsCount);
            
            
        } catch (final Throwable e) {           
            try { 
            	Files.deleteIfExists(filePathInput.asJavaNIOPath());  /// delete temp file
            } catch (IOException ignored) {
            	// ignore error deleting temp file
            }
            throw new IllegalStateException("Error creating parquet file: " + e.getMessage(), e);
        }
        
    }
    
    @Override
    public ParquetWriterResult write(final Stream<T> dataStream,
    				                 final Path filePathInput) {
    	
    	java.nio.file.Path filePath = filePathInput.asJavaNIOPath();
    	 // [1] Obtain Parquet idl schema           
        MessageType schema = _mapper.getMessageTypeSchema();
        
        // [2] Define (Blue Strategic) Dehydrator
        Dehydrator<T> dehydrator = _mapper::mapToValueWriter;

        // [3] Write to ZSTD compress formar  [ Ocena Libray don't expose this method, so  ParquetWriter has been rewrited)         
        try (dataStream; 
            ParquetWriter<T> writer = ParquetWriter.writeFile(schema,
                                                               filePath.toFile(), 
                                                               dehydrator,
                                                               CompressionCodecName.ZSTD)) {
        	AtomicLong exportedRecordsCount = new AtomicLong(0);
            dataStream.forEach(item -> {
							                try {
							                    writer.write(item);
							                    exportedRecordsCount.incrementAndGet();
							                } catch (final IOException e) {
							                    throw new UncheckedIOException(e);
							                }
            });
            return new ParquetWriterResult(filePathInput,exportedRecordsCount.get());
        } catch (final Throwable e) {
        	e.printStackTrace();
            try { 
                Files.deleteIfExists(filePath); 
            } catch (IOException ignored) {
            	//
            }            
            throw new IllegalStateException("Error creating parquet file: " + e.getMessage(), e);
        }       
    }
    
    
    
    @Override
    public InputStream write(final Iterable<T> data) {
        try {        
            //[1] Create temp file
        	java.nio.file.Path tempPath = Files.createTempFile("export-",".parquet");          
            
            //[2] generate
            write(data, Path.from(tempPath.toString()));  
            
            //[3]
            return new FileInputStream(tempPath.toFile()) {
		                @Override
		                public void close() throws IOException {
		                    try {
		                        super.close();
		                    } finally {		                  
		                        Files.deleteIfExists(tempPath);
		                    }
		                }
		            };
        } catch (final Throwable e) {
             throw new IllegalStateException("Error generating parquet stream: " + e.getMessage(), e);
        }
    }
}