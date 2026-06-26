package r01f.filestore.parquet.service.test;

import java.time.Instant;

import lombok.AllArgsConstructor;
import  lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@Builder 
@NoArgsConstructor
@AllArgsConstructor
public class MockPersona {
    private String oid;
    private String dni;
    private Instant lastUpdate;
    private String tipoDatos;

}