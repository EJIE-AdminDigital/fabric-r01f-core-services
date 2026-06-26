package r01f.filestore.parquet.service;

import org.apache.avro.Schema;
import org.apache.parquet.avro.AvroSchemaConverter;
import org.apache.parquet.schema.MessageType;

import lombok.experimental.Accessors;


@Accessors(prefix="_")
public abstract class ParquetMapperFromAvroSchema<T> 
          	  extends ParquetMapperFromIdlSchema<T> 
           implements ParquetMapper<T>  {	
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////	
	public ParquetMapperFromAvroSchema(final org.apache.avro.Schema schema) {
		super(_convertAvroToParquetIdl(schema));
		
	}
	public ParquetMapperFromAvroSchema(final String avroSchemaAsJson) {
		this(new Schema.Parser().parse(avroSchemaAsJson));		
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
    protected static MessageType _convertAvroToParquetIdl(final org.apache.avro.Schema schema) {
    	AvroSchemaConverter converter = new AvroSchemaConverter();    
    	MessageType parquetSchema = converter.convert(schema);
    	return parquetSchema;
    	/* StringBuilder sb = new StringBuilder();
        sb.append("message ").append(schema.getName()).append(" {\n");
        
        for (org.apache.avro.Schema.Field field : schema.getFields()) {
            sb.append("  required ");
            
            // Mapeo básico de tipos
            switch (field.schema().getType()) {
                case INT:    sb.append("int32 "); break;
                case LONG:   sb.append("int64 "); break;
                case DOUBLE: sb.append("double "); break;
                case FLOAT:  sb.append("float "); break;
                case BOOLEAN:sb.append("boolean "); break;
                case STRING: sb.append("binary ").append(field.name()).append(" (UTF8)"); break;
                default:     sb.append("binary "); // Por defecto para otros
            }
            
            if (field.schema().getType() != org.apache.avro.Schema.Type.STRING) {
                sb.append(field.name());
            }
            sb.append(";\n");
        }
        sb.append("}");*/
      
    }
 

}
