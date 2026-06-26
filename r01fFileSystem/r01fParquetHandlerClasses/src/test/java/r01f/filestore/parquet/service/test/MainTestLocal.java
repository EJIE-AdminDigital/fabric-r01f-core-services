package r01f.filestore.parquet.service.test;

import java.io.File;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.apache.parquet.schema.MessageType;
import org.apache.parquet.schema.MessageTypeParser;

import r01f.filestore.parquet.service.ParquetWriterService;
import r01f.filestore.parquet.service.impl.ParquetServiceDefaultImpl;
import r01f.types.Path;

public class MainTestLocal {

    public static void main(String[] args) {
   
        //[ avro Schema]
      /*  String schemaJson = "{" +
	                "\"type\":\"record\"," +
	                "\"name\":\"MockPersona\"," +
	                "\"fields\":[" +
	                "  {\"name\":\"oid\", \"type\":\"string\"}," +
	                "  {\"name\":\"dni\", \"type\":\"string\"}," +
	                "  {\"name\":\"last_update\", \"type\":\"long\"}," +
	                "  {\"name\":\"tipo_datos\", \"type\":\"string\"}" +
                "]}";*/
    	
    	// 1. Definir el esquema en DSL de Parquet (Súper limpio)
    	String dsl = "message MockPersona {\n" +
	    	             "  required binary oid (UTF8);\n" +
	    	             "  required binary dni (UTF8);\n" +
	    	             "  required int64 last_update;\n" +
	    	             "  required binary tipo_datos (UTF8);\n" +
	    	             "}";
        
        //[ 1 millon ] 
        int number_of_person = 1000000;       
        List<MockPersona> lista = new ArrayList<>();
        for (int i = 1; i <= number_of_person; i++) {
            lista.add(new MockPersona(         	java.util.UUID.randomUUID().toString(), 
								                "DNI" + ( i ), 
								                 Instant.now(), 
								                i % 2 == 0 ? "FILE" : "PAY"
								            ));
        }

        //
        File rutaLocal = new File("c://develop/temp/person-"+ System.currentTimeMillis()  +".parquet" );
        
        try {
            System.out.println("..[init] .generate at : " + rutaLocal);
            MessageType schema = MessageTypeParser.parseMessageType(dsl);
            MockPersonaMapper mapper  = new MockPersonaMapper(schema);
			ParquetWriterService<MockPersona> service = new ParquetServiceDefaultImpl<>(mapper);
            System.out.println( "OK!");
            
            
            
            
            long start = System.currentTimeMillis();
            service.write(lista, Path.from(rutaLocal.toPath())); 
            long end = System.currentTimeMillis();
            
            
            System.out.println(" time " + (end - start) + " ms");
            System.out.println(" parquet file size: " + (rutaLocal.length() / 1024) + " KB");
            
            long bytes = rutaLocal.length();
            double megas = bytes / (1024.0 * 1024.0);

            System.out.println("===========================================");
            System.out.println("📊 REPORT");
            System.out.println("===========================================");

            // %d es para long (sin decimales)
            System.out.printf("Total Bytes:  %,d bytes%n", bytes); 

            // %.2f es para double (con 2 decimales)
            System.out.printf("Total MB:     %.2f MB%n", megas); 

            // Para el coste por registro, forzamos a double para evitar errores
            System.out.printf("Coste/Reg:    %.2f bytes%n", (double) bytes / number_of_person);

            System.out.println("===========================================");
      
            
        } catch (final Throwable e) {
            System.err.println("Error al generar el archivo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}