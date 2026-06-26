package r01f.core.batch.exportimport.json;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutorService;

import lombok.extern.slf4j.Slf4j;
import r01f.core.batch.ItemFlowProcessingProgress;
import r01f.core.batch.ItemFlowProcessingProgressFactory;
import r01f.core.batch.ItemFlowProcessorBase;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;

@Slf4j
public abstract class ExportAsJSONItemFlowProcessorBase<T,M,
														P extends ItemFlowProcessingProgress>
	 		  extends ItemFlowProcessorBase<T,P> {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	protected final Marshaller _marshaller;
	
	protected boolean _firstObj;
	private M _prevObject;
	
	protected Writer _writer;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR / BUILDER
/////////////////////////////////////////////////////////////////////////////////////////	
	public ExportAsJSONItemFlowProcessorBase(final Marshaller marshaller,
											 final ExecutorService executorService,
						   			 		 final OutputStream outputStream,
						   			 		 final ItemFlowProcessingProgressFactory<T,P> itemFlowProcessingProgressFactory) {
		super(executorService,
			  outputStream,
			  itemFlowProcessingProgressFactory);
		_marshaller = marshaller;
	}	
/////////////////////////////////////////////////////////////////////////////////////////
//	OVERRIDABLE
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	protected void _init(final SecurityContext securityContext,
						 final OutputStream os) throws IOException {
		// create the writer
		_writer = new OutputStreamWriter(os,Charset.defaultCharset());
		
		// start the array
		_writer.write("[\n");	// start of array
		
		_firstObj = true;
	}
	@Override
	protected void _processItem(final SecurityContext securityContext,
							  final T item) throws IOException {
		// [1] - Transform the item (if needed)
		M obj = _createObjectToBeIncludedInTheJSON(securityContext,
											   	   item);
		// [2] - Write the object
		_writePrev();
		_prevObject = obj;
	}
	/**
	 * Maybe the object being written to the JSON is NOT the item 
	 * (ie the item is an oid used to load the real object written to the JSON)
	 * @param securityContext
	 * @param item
	 * @return
	 */
	protected abstract M _createObjectToBeIncludedInTheJSON(final SecurityContext securityContext,
							  						  		final T item);	
	@Override
	protected void _end(final SecurityContext securityContext) throws IOException {
		_writePrev();
		_writer.write("\n]");	// end of array
		
		// flush
		_writer.flush();
		_writer.close();
		super._end(securityContext);
	}
	private void _writePrev()  {
		if (_prevObject == null) return;
		try {
			// write a separator , if NOT the first object
			if (!_firstObj) _writer.write(",\n");		// bear the ,!!!
			
			// write the object
			String json = _marshaller.forWriting()
									 .toJson(_prevObject);
			_writer.write(json);// beware! _marshaller.forWriting()
								//					  .toJson(_prevObj,writer)
								// 		   automatically CLOSES the WRITER!
								// 		   ... in order to avoid this behavior a FIX is needed at the marshaller (see https://stackoverflow.com/questions/57205187/how-to-fix-stream-closed-exception)
			
			// once the first object was written it's NOT the first object anymore
			if (_firstObj) _firstObj = false;		// once the first object was written
		} catch (IOException ioEx) {
			log.error("Error while generating JSON export file: {}",
					  ioEx.getMessage(),ioEx);
		}
	}

}