package r01f.filestore.parquet.service;

import r01f.types.Path;



public record ParquetWriterResult(Path path,
		                          long exportedRecordsCount) {
}
