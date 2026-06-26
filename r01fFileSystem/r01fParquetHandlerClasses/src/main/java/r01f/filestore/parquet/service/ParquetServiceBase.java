package r01f.filestore.parquet.service;

import java.io.File;

import org.apache.parquet.schema.MessageType;

import blue.strategic.parquet.Dehydrator;
import blue.strategic.parquet.ParquetWriter;

public abstract class ParquetServiceBase<T> 
		   implements ParquetWriterService<T> {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////	
    protected final ParquetMapper<T> _mapper;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
    protected ParquetServiceBase(final ParquetMapper<T> mapper) {
        _mapper = mapper;
    }
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////    
    protected void writeToPath(final File file, final Iterable<T> data) throws Exception {       
        MessageType schema = _mapper.getMessageTypeSchema();
        // Implement Dehydrator using  mapper //Dehydrates a rich java object into a Parquet row.
        Dehydrator<T> dehydrator = (item,valWriter) -> {          
							            _mapper.mapToValueWriter(item, 
							            		                 valWriter); 
        							};
        try (ParquetWriter<T> writer = ParquetWriter.writeFile(schema, 
        													   file, 
        													   dehydrator)) {
            for (T item : data) {
                writer.write(item);
            }
        }
    }
/////////////////////////////////////////////////////////////////////////////////////////
// 	PROTECTED METHODS
/////////////////////////////////////////////////////////////////////////////////////////  
   
}