package com.vtb.util.report.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.vtb.util.CollectionUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.masterdm.compendium.model.CompendiumActionProcessor;
import ru.masterdm.spo.service.IStandardPeriodService;
import ru.masterdm.spo.utils.SBeanLocator;
import ru.md.domain.AuditDurationStage;
import ru.md.domain.TaskComment;
import ru.md.persistence.ReportMapper;
import ru.md.spo.ejb.PupFacadeLocal;

import com.vtb.domain.StandardPeriod;
import com.vtb.util.Formatter;
import ru.md.spo.ejb.StandardPeriodBeanLocal;

/**
 *  Генератор электронной таблицы
 * {@link StandardPeriod }
 */
public class AuditDurationStagesDataExporter extends AExcelDataExporter<AuditDurationStage> {
	private static final Logger LOGGER = LoggerFactory.getLogger(AuditDurationStagesDataExporter.class.getName());
	private final static Integer START_ROW = 3;
	private final static Integer COLUMN_COUNT = 8;
	private final static String  NEW_LINE = "\n\n";
	
	private CellStyle style;
	private CellStyle styleOverdue;
	
	private Date from;
	private Date to;
	private Long processID;

	/**
	 * 
	 * Конструктор
	 *
	 */
	public AuditDurationStagesDataExporter(Long processID, Date from, Date to) {
		super();
		this.processID=processID;
		this.from=from;
		this.to=to;
	}

	/**
	 * Makes an excel transformation
	 * @throws Exception
	 */
	public byte[] proceed(final List<AuditDurationStage> list) throws Exception {
		try {
			excel = buildWorkbook();
			style = setStandardStyle(false);
			styleOverdue = setStandardStyle(true);
			

			fillSheet(excel.getSheetAt(0), list);
			return convert();
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Sets the ordinary style's properties.
	 * @param overdue is the style for overdue rows or not 
	 * @return created style 
	 */
	private CellStyle setStandardStyle(boolean overdue) {
		CreationHelper createHelper = excel.getCreationHelper();
		CellStyle style = excel.createCellStyle();
		style.setDataFormat(createHelper.createDataFormat().getFormat("dd.mm.yyyy"));
		style.setWrapText(true);
		style.setAlignment(CellStyle.ALIGN_LEFT);
		style.setVerticalAlignment(CellStyle.VERTICAL_BOTTOM);
		
		Font font = excel.createFont();
		font.setFontHeightInPoints((short)9);
		if (overdue) {
			font.setColor(Font.COLOR_RED);
		}
		style.setFont(font);
		return style;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Sheet fillSheet(Sheet sheet, List<AuditDurationStage> tuples) throws Exception {
		try {
			super.fillSheet(sheet, tuples);
			//for (int columnIndex = 0; columnIndex < COLUMN_COUNT; columnIndex++)
			//	sheet.autoSizeColumn(columnIndex);
			sheet.setFitToPage(true);
		} catch (Exception e) {
			throw e;
		}
		return sheet;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Sheet fillCaption(Sheet sheet) throws Exception {
		try {
			if (sheet == null) throw new NullPointerException("sheet is null");
			
			CellStyle style = excel.createCellStyle();
			Font font = excel.createFont();
			font.setBoldweight(Font.BOLDWEIGHT_BOLD);
			style.setFont(font);
			
			Row rowProcess = sheet.createRow(0);
			rowProcess.setHeight((short) 400);
			int columnIndex = 0;
			sheet.setColumnWidth(columnIndex, 18 * 256);
			Cell cell = rowProcess.createCell(columnIndex++);
			fillCell("Процесс: '" + pup().getProcessTypeById(processID).getDescriptionProcess() + 
					"'", style, cell);

			Row rowDate = sheet.createRow(1);
			rowDate.setHeight((short) 400);
			columnIndex = 0;
			sheet.setColumnWidth(columnIndex, 18 * 256);
			cell = rowDate.createCell(columnIndex++);
			fillCell(" за период с " + Formatter.format(from) + " по " + Formatter.format(to), style, cell);
			
			sheet.createFreezePane(0, START_ROW);

			Row row = sheet.createRow(2);
			
			columnIndex = 0;
			
			sheet.setColumnWidth(columnIndex, 18 * 256);
			cell = row.createCell(columnIndex++);
			fillCell("Номер заявки", style, cell);
			
			sheet.setColumnWidth(columnIndex, 18 * 256);
			cell = row.createCell(columnIndex++);
			fillCell("Контрагент", style, cell);
			
			sheet.setColumnWidth(columnIndex, 30 * 256);
			cell = row.createCell(columnIndex++);
			fillCell("Сумма", style, cell);

			sheet.setColumnWidth(columnIndex, 18 * 256);
			cell = row.createCell(columnIndex++);
			fillCell("Валюта", style, cell);
			
			sheet.setColumnWidth(columnIndex, 18 * 256);
			cell = row.createCell(columnIndex++);
			fillCell("Тип заявки", style, cell);
			
			sheet.setColumnWidth(columnIndex, 25 * 256);
			cell = row.createCell(columnIndex++);
			fillCell("Статус", style, cell);
			
			sheet.setColumnWidth(columnIndex, 18 * 256);
			cell = row.createCell(columnIndex++);
			fillCell("Инициирующее подразделение", style, cell);
			
			sheet.setColumnWidth(columnIndex, 18 * 256);
			cell = row.createCell(columnIndex++);
			fillCell("Клиентский менеджер", style, cell);
			
			sheet.setColumnWidth(columnIndex, 23 * 256);
			cell = row.createCell(columnIndex++);
			fillCell("Продуктовый менеджер", style, cell);
			
			sheet.setColumnWidth(columnIndex, 18 * 256);
			cell = row.createCell(columnIndex++);
			fillCell("Структуратор", style, cell);
			
			sheet.setColumnWidth(columnIndex, 22 * 256);
			cell = row.createCell(columnIndex++);
			fillCell("Кредитный аналитик", style, cell);
			
			sheet.setColumnWidth(columnIndex, 60 * 256);
			cell = row.createCell(columnIndex++);
			fillCell("Название этапа", style, cell);
			
			sheet.setColumnWidth(columnIndex, 30 * 256);
			cell = row.createCell(columnIndex++);
			fillCell("Начало", style, cell);
			
			sheet.setColumnWidth(columnIndex, 30 * 256);
			cell = row.createCell(columnIndex++);
			fillCell("Конец", style, cell);
			
			sheet.setColumnWidth(columnIndex, 18 * 256);
			cell = row.createCell(columnIndex++);
			fillCell("Норматив", style, cell);

			sheet.setColumnWidth(columnIndex, 27 * 256);
			cell = row.createCell(columnIndex++);
			fillCell("Комментарии по стадии", style, cell);

			sheet.setColumnWidth(columnIndex, 30 * 256);
			cell = row.createCell(columnIndex++);
			fillCell("ФИО разместившего комментарии", style, cell);	
			
			sheet.setColumnWidth(columnIndex, 22 * 256);
			cell = row.createCell(columnIndex++);
			fillCell("Подразделение разместившего комментарии", style, cell);
		} catch (Exception e) {
			throw e;
		}
		
		return sheet;
	}

	/**
	 * по дате старта и количеству дней срока возвратит до какого дата\время
	 * нужно уложиться. То есть вернет 9:00 того рабочего дня, когда заявка
	 * будет просрочена
	 *
	 * @param from
	 *            date from
	 * @param interval
	 *            промежуток времени
	 * @return требуемую дату. Null, если неверно.
	 */
	private Date getDeadLineDate(Date from, Integer interval) {
		CompendiumActionProcessor compenduim = (CompendiumActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory
				.getActionProcessor("Compendium");
		return compenduim.findDeadlineDate(true, from, interval);
	}

	public boolean isExpired(Date deadline, Date finish) {
		if(deadline==null)
			return false;
		if(finish!=null){
			return deadline.before(finish);
		} else {
			return deadline.before(new Date());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Row fillRow(AuditDurationStage audit, Row row) throws Exception {
		try {
			if (row == null) throw new NullPointerException("row is null");
			if (audit == null) throw new NullPointerException("tuple is null");

			CellStyle style = isExpired(getDeadLineDate(audit.getStageStart(),audit.getPeriod()==null?null:audit.getPeriod().intValue()),
					audit.getStageEnd()) ? this.styleOverdue : this.style;
			int columnIndex = 0;
			
			//Номер заявки
			Cell cell = row.createCell(columnIndex++);
			fillCell(audit.getMdtaskNumber(), style, cell);
			//Контрагент
			cell = row.createCell(columnIndex++);fillCell(audit.getEkName(), style, cell);
			//Сумма   
			cell = row.createCell(columnIndex++);fillCell(Formatter.format(audit.getMdtaskSum()), style, cell);
			cell = row.createCell(columnIndex++);fillCell(audit.getCurrency(), style, cell);
			//Тип заявки      
			cell = row.createCell(columnIndex++);fillCell(audit.getTasktypeDisplay(), style, cell);
			//Статус
			cell = row.createCell(columnIndex++);fillCell(audit.getStatus(), style, cell);
			//Инициирующее подразделение      
			cell = row.createCell(columnIndex++);fillCell(audit.getInitDepName(), style, cell);
			//Клиентский менеджер     
			cell = row.createCell(columnIndex++);fillCell(audit.getClientManager(), style, cell);
			//Продуктовый менеджер        
			cell = row.createCell(columnIndex++);fillCell(audit.getProguctManager(), style, cell);
			//Структуратор    
			cell = row.createCell(columnIndex++);fillCell(audit.getStructurator(), style, cell);
			//Кредитный аналитик
			cell = row.createCell(columnIndex++);fillCell(audit.getAnalist(), style, cell);
			//Название этапа
			cell = row.createCell(columnIndex++);
			fillCell(audit.getStageNameDisplay(), style, cell);
			//Начало  Конец   
			SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");
			String d1 = audit.getStageStart() != null ? df.format(audit.getStageStart()) : "";
			String d2 = audit.getStageEnd() != null ? df.format(audit.getStageEnd()) : "";
			cell = row.createCell(columnIndex++);
			fillCell(d1, style, cell);
			cell = row.createCell(columnIndex++);
			fillCell(d2, style, cell);
			//Норматив
			cell = row.createCell(columnIndex++);
			fillCell(Formatter.format(audit.getPeriod()), style, cell);
			//Комментарии по стадии
			cell = row.createCell(columnIndex++);
			fillCell(audit.getCmnt(), style, cell);
			//ФИО разместившего комментарий
			cell = row.createCell(columnIndex++);
			fillCell(audit.getCmntUser(), style, cell);
			//Подразделение разместившего комментарий
			cell = row.createCell(columnIndex++);
			fillCell(audit.getCmntDep(), style, cell);
		} catch (Exception e) {
			throw e;
		}
		return row;
	}

	/**
	 * Concatenates List<T> in one String with a given delimiter 
	 * @param <T> type of object in the list
	 * @param list list of objects to concatenate
	 * @param delimiter delimiter between
	 * @return
	 */
	private <T> String concatenateString(List<T> list, String delimiter) {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		if (list != null) 
			for (T element : list) {
				if (first) {
					sb.append(element.toString());
					first = false;
				} else
					sb.append(delimiter + element.toString());
			}
		return sb.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Integer getStartRowIndex() throws Exception {
		return START_ROW;
	}
	public PupFacadeLocal pup(){
	   	 try {
	   		 return com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
	   	 } catch (Exception e) {
	   		 LOGGER.error(e.getMessage(), e);
	   	 }
	   	 return null;
    }
}