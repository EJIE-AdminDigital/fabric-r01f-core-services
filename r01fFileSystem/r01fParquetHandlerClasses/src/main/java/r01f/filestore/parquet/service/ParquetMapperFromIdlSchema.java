package r01f.filestore.parquet.service;

import org.apache.parquet.schema.MessageType;
import org.apache.parquet.schema.MessageTypeParser;

import lombok.Getter;
import lombok.experimental.Accessors;


@Accessors(prefix="_")
public abstract class ParquetMapperFromIdlSchema<T> 
	       implements ParquetMapper<T> {
/////////////////////////////////////////////////////////////////////////////////////////
//	SCHEMA
/////////////////////////////////////////////////////////////////////////////////////////	
	@Getter final public MessageType _messageTypeSchema;
/////////////////////////////////////////////////////////////////////////////////////////
// 	CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public ParquetMapperFromIdlSchema(final MessageType schemaAsIdlDsl) {
		_messageTypeSchema = schemaAsIdlDsl;
	}
	public ParquetMapperFromIdlSchema(final String schemaAsIdlDsl) {
		_messageTypeSchema = MessageTypeParser.parseMessageType(schemaAsIdlDsl);
	}	
}
