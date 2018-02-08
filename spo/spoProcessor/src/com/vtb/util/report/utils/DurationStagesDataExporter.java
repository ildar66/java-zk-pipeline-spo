package com.vtb.util.report.utils;

import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import com.vtb.domain.StandardPeriod;

/**
 * {@link Генератор} электронной таблицы {@link StandardPeriod }
 */
public class DurationStagesDataExporter extends AExcelDataExporter<StandardPeriod> {

	private final static Integer START_ROW = 2;
	private final static Integer COLUMN_COUNT = 8;
	private final static String NEW_LINE = "\n\n";

	private String mdtaskNumber;

	private CellStyle style;
	private CellStyle styleOverdue;

	/**
	 * Конструктор
	 * @param mdtaskNumber номер заявки
	 */
	public DurationStagesDataExporter(String mdtaskNumber) {
		super();
		this.mdtaskNumber = mdtaskNumber;
	}

	/**
	 * Makes an excel transformation
	 * @param domain
	 * @param list
	 * @return
	 * @throws Exception
	 */
	public byte[] proceed(final List<StandardPeriod> list) throws Exception {
		try {
			excel = buildWorkbook();
			style = setStandardStyle(false);
			styleOverdue = setStandardStyle(true);

			fillSheet(excel.getSheetAt(0), list);
			return convert();
		}
		catch (Exception e) {
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
		font.setFontHeightInPoints((short) 9);
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
	protected Sheet fillSheet(Sheet sheet, List<StandardPeriod> tuples) throws Exception {
		try {
			super.fillSheet(sheet, tuples);
			for (int columnIndex = 0; columnIndex < COLUMN_COUNT; columnIndex++)
				sheet.autoSizeColumn(columnIndex);
			sheet.setFitToPage(true);
		}
		catch (Exception e) {
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
			if (sheet == null)
				throw new NullPointerException("sheet is null");

			CellStyle style = excel.createCellStyle();
			Font font = excel.createFont();
			font.setBoldweight(Font.BOLDWEIGHT_BOLD);
			style.setFont(font);

			Row rowTaskNumber = sheet.createRow(0);
			int columnIndex = 0;
			sheet.setColumnWidth(columnIndex, 18 * 256);
			Cell cell = rowTaskNumber.createCell(columnIndex++);
			fillCell("Заявка: " + mdtaskNumber, style, cell);

			sheet.createFreezePane(0, START_ROW);

			Row row = sheet.createRow(1);

			columnIndex = 0;
			sheet.setColumnWidth(columnIndex, 18 * 256);
			cell = row.createCell(columnIndex++);
			fillCell("Этап", style, cell);

			sheet.setColumnWidth(columnIndex, 18 * 256);
			cell = row.createCell(columnIndex++);
			fillCell("Операции", style, cell);

			sheet.setColumnWidth(columnIndex, 18 * 256);
			cell = row.createCell(columnIndex++);
			fillCell("Исполнитель", style, cell);

			sheet.setColumnWidth(columnIndex, 1 * 256);
			cell = row.createCell(columnIndex++);
			fillCell("Нормативный срок (раб. дн.)", style, cell);

			sheet.setColumnWidth(columnIndex, 10 * 256);
			cell = row.createCell(columnIndex++);
			fillCell("Критерий дифференциации", style, cell);

			sheet.setColumnWidth(columnIndex, 1 * 256);
			cell = row.createCell(columnIndex++);
			fillCell("Фактический срок (раб. дн.)", style, cell);

			sheet.setColumnWidth(columnIndex, 5 * 256);
			cell = row.createCell(columnIndex++);
			fillCell("Дата начала - Дата окончания", style, cell);

			sheet.setColumnWidth(columnIndex, 18 * 256);
			cell = row.createCell(columnIndex++);
			fillCell("История изменения", style, cell);

		}
		catch (Exception e) {
			throw e;
		}

		return sheet;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Row fillRow(StandardPeriod tuple, Row row) throws Exception {
		try {
			if (row == null)
				throw new NullPointerException("row is null");
			if (tuple == null)
				throw new NullPointerException("tuple is null");

			CellStyle style = tuple.isExpired() ? this.styleOverdue : this.style;
			int columnIndex = 0;

			// этап
			Cell cell = row.createCell(columnIndex++);
			fillCell(tuple.getStageName(), style, cell);

			// операции
			String operations = concatenateString(tuple.getOperationName(), NEW_LINE);
			cell = row.createCell(columnIndex++);
			fillCell(operations, style, cell);

			// Исполнитель
			String users = concatenateString(tuple.getUser(), NEW_LINE);
			cell = row.createCell(columnIndex++);
			fillCell(users, style, cell);

			// Нормативный срок (раб. дн.)
			cell = row.createCell(columnIndex++);
			fillCell(tuple.getStandardPeriod(), style, cell);

			// Критерий дифференциации
			cell = row.createCell(columnIndex++);
			fillCell(tuple.getCriteria(), style, cell);

			// Фактический срок
			cell = row.createCell(columnIndex++);
			fillCell(tuple.getFactPeriod(), style, cell);

			// Дата начала - Дата окончания
			SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
			String d1 = tuple.getStart() != null ? df.format(tuple.getStart()) : "";
			String d2 = tuple.getFinish() != null ? df.format(tuple.getFinish()) : "";
			cell = row.createCell(columnIndex++);
			if ((!d1.equals("")) || (!d2.equals("")))
				fillCell(d1 + " - " + d2, style, cell);

			// История изменения
			String changeHistory = concatenateString(tuple.getChangeHistory(), NEW_LINE);
			cell = row.createCell(columnIndex++);
			fillCell(changeHistory, style, cell);
		}
		catch (Exception e) {
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
				}
				else
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
}