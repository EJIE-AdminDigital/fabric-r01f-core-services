package r01f.core.batch.exportimport.excel;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.ExecutorService;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.core.batch.ItemFlowProcessorBase;
import r01f.securitycontext.SecurityContext;
import r01f.util.types.collections.CollectionUtils;


@Slf4j
@Accessors(prefix="_")
public abstract class ExportAsExcelItemFlowProcessorBase<T> 
	 		  extends ItemFlowProcessorBase<T> {
	
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	protected final Collection<String> _columnHeaders;
	
	protected final Workbook _xlsWorkbook;
	protected final Sheet _xlsSheet;
	protected final CellStyle _xlsCellBoldStyle;
	protected final CellStyle _xlsCellDefaultStyle;
	protected int _xlsRowNum;
	protected int _xlsColNum;
	
	protected OutputStream _os;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR / BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public ExportAsExcelItemFlowProcessorBase(final ExecutorService executorService,
											  final OutputStream outputStream) {
		this(executorService,
			 outputStream,
			 null);
	}
	public ExportAsExcelItemFlowProcessorBase(final ExecutorService executorService,
											  final OutputStream outputStream,
											  final Collection<String> columnHeaders) {
		super(executorService,
			  outputStream);
		_columnHeaders = columnHeaders;
		
		// excel generation
		_xlsWorkbook = new XSSFWorkbook();
		_xlsSheet = _xlsWorkbook.createSheet();
		_xlsColNum = 0;
		_xlsRowNum = 0;
		
		_xlsCellBoldStyle = _createXlsCellBoldStyle(_xlsWorkbook);
		_xlsCellDefaultStyle = _createXlsCellDefaultStyle(_xlsWorkbook);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected void _init(final SecurityContext securityContext,
						 final OutputStream os) throws IOException {
		// the output stream
		_os = os;
		
		// Header
		if (CollectionUtils.isNullOrEmpty(_columnHeaders)) return;
		
		Row xlsRow = _createXlsRow();
		_columnHeaders.forEach(headCol -> {
									Cell xlsCell = _createXlsCell(xlsRow,headCol);
									xlsCell.setCellStyle(_xlsCellBoldStyle);
							   });
	}
	@Override
	protected void _end(final SecurityContext securityContext) throws IOException {
		// [1] - adjust column widths
		int colNum = CollectionUtils.hasData(_columnHeaders) ? _columnHeaders.size() : 0;
		for (int i = 0; i < colNum; i++) {
			_xlsSheet.autoSizeColumn(i);
		}
		
		// [2] - write the generated excel file
		try {
			_xlsWorkbook.write(_os);
		} catch (IOException ioEx) {
			log.error("... error while writing: {}",
					  ioEx.getMessage(),ioEx);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EXCEL GENERATION
/////////////////////////////////////////////////////////////////////////////////////////
	protected Row _createXlsRow() {
		Row xlsRow = _xlsSheet.createRow(_xlsRowNum);
		_xlsRowNum++;
		_xlsColNum = 0;
		return xlsRow;
	}
	protected Cell _createXlsCell(final Row xlsRow,final Object value) {
		Cell xlsCell = xlsRow.createCell(_xlsColNum);
		_xlsColNum++;
		
		if (value == null) {
			xlsCell.setBlank();
		} else if (value instanceof Boolean boolVal) {
			xlsCell.setCellValue(boolVal);
		} else if (value instanceof Date dateVal) {
			xlsCell.setCellValue(dateVal);
		} else if (value instanceof Calendar calendarVal) {
			xlsCell.setCellValue(calendarVal.getTime());
		} else if (value instanceof Integer intVal) {
			xlsCell.setCellValue(intVal);
		} else if (value instanceof Long longVal) {
			xlsCell.setCellValue(longVal);
		} else if (value instanceof Double doubleVal) {
			xlsCell.setCellValue(doubleVal);
		} else if (value instanceof Float floatVal) {
			xlsCell.setCellValue(floatVal);
		} else if (value instanceof XSSFRichTextString xssfRichTextStringVal) {
			xlsCell.setCellValue(xssfRichTextStringVal);
		} else {
			xlsCell.setCellValue(value.toString());		 
		}
		xlsCell.setCellStyle(_xlsCellDefaultStyle);
		return xlsCell;
	}
	private static CellStyle _createXlsCellBoldStyle(final Workbook workBook) {
		Font bold = workBook.createFont();
		bold.setBold(true);			

		CellStyle boldStyle = workBook.createCellStyle();
		boldStyle.setFont(bold);
		
		return boldStyle;
	}
	
	protected static CellStyle _createXlsCellDefaultStyle(final Workbook workBook) {
		XSSFFont defaultFont = (XSSFFont) workBook.createFont();
		defaultFont.setFontHeight(10);

		CellStyle defaultStyle = workBook.createCellStyle();
		defaultStyle.setFont(defaultFont);
		
		return defaultStyle;
	}
}
