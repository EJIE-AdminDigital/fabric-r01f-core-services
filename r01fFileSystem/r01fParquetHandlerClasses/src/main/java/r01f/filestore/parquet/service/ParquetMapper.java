package r01f.filestore.parquet.service;

import org.apache.parquet.schema.MessageType;

import blue.strategic.parquet.ValueWriter;


public interface ParquetMapper<T> {
   
    public MessageType getMessageTypeSchema();
     
    public void mapToValueWriter(final T source, final ValueWriter writer);
}