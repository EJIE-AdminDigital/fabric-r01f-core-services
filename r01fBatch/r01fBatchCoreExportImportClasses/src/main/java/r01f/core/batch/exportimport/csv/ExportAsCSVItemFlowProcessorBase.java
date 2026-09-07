package r01f.core.batch.exportimport.csv;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.concurrent.ExecutorService;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import lombok.experimental.Accessors;
import r01f.core.batch.ItemFlowProcessorBase;
import r01f.securitycontext.SecurityContext;
import r01f.util.types.collections.CollectionUtils;


@Accessors(prefix="_")
public abstract class ExportAsCSVItemFlowProcessorBase<T>
	 		  extends ItemFlowProcessorBase<T> {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	protected final Collection<String> _columnHeaders;

	protected CSVPrinter _csvPrinter;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR / BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public ExportAsCSVItemFlowProcessorBase(final ExecutorService executorService,
											final OutputStream outputStream) {
		this(executorService,
			 outputStream,
			 null);
	}
	public ExportAsCSVItemFlowProcessorBase(final ExecutorService executorService,
											final OutputStream outputStream,
							   		  	   	final Collection<String> columnHeaders) {
		super(executorService,
			  outputStream);
		_columnHeaders = columnHeaders;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected void _init(final SecurityContext securityContext,
						 final OutputStream os) throws IOException {
		CSVFormat csvFormat = CollectionUtils.hasData(_columnHeaders) ? CSVFormat.Builder.create(CSVFormat.DEFAULT)
									   											 		 .setHeader(CollectionUtils.toArray(_columnHeaders,String.class))
									   											 		 .build()
									   								  : CSVFormat.Builder.create(CSVFormat.DEFAULT)
									   								  					 .build();
		Writer writer = new OutputStreamWriter(os,StandardCharsets.UTF_8);
		_csvPrinter = new CSVPrinter(writer,csvFormat);
	}
	@Override
	protected void _end(final SecurityContext securityContext) throws IOException {
		// flush
		_csvPrinter.flush();
		_csvPrinter.close();
		super._end(securityContext);
	}
}
