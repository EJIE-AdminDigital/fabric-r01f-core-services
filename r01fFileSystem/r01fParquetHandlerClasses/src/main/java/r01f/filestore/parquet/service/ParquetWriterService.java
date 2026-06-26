package r01f.filestore.parquet.service;

import java.io.InputStream;

import java.util.stream.Stream;

import r01f.types.Path;

public interface ParquetWriterService<T> {
	
   public ParquetWriterResult write(final Iterable<T> data, 
		   			                final Path outputDestination) ;
   
   public ParquetWriterResult write(final Stream<T> dataStream,
			                        final Path outputDestination);
   
   public InputStream write (final Iterable<T> data);
   
}