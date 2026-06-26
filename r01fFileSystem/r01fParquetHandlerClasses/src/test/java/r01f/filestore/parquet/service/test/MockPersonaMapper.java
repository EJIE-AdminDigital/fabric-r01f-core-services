package r01f.filestore.parquet.service.test;

import org.apache.parquet.schema.MessageType;

import blue.strategic.parquet.ValueWriter;
import r01f.filestore.parquet.service.ParquetMapper;
import r01f.filestore.parquet.service.ParquetMapperFromIdlSchema;

public class MockPersonaMapper
		extends ParquetMapperFromIdlSchema<MockPersona> 
	implements ParquetMapper<MockPersona> {
	
/////////////////////////////////////////////////////////////
// CONSTRUCTOR
/////////////////////////////////////////////////////////////
    public MockPersonaMapper(final MessageType schemaAsIdlDsl) {
		super(schemaAsIdlDsl);		
	}
/////////////////////////////////////////////////////////////
// MAP
/////////////////////////////////////////////////////////////
    @Override
    public void mapToValueWriter(final MockPersona p, final ValueWriter writer) {
        writer.write("oid", p.getOid());
        writer.write("dni", p.getDni());
        writer.write("last_update", p.getLastUpdate().toEpochMilli());
        writer.write("tipo_datos", p.getTipoDatos());
    }


}