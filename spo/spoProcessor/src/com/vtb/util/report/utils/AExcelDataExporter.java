package com.vtb.util.report.utils;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * Реализация по умолчанию {@link IDataExporter экспорта данных} в {@link Workbook Excel}
 * 
 * @param <T> тип возвращаемого объекта
 * @param <E> тип доменного объекта
 *
 * @author svaliev@masterdm.ru
 * transferred by MKuznetsov
 */
public abstract class AExcelDataExporter<E> {

	protected Workbook excel;
	
	/**
	 * Создание {@link Workbook объекта Excel}
	 *
	 * @return {@link Workbook объект Excel}
	 * @throws Exception {@link Exception ошибка}
	 */
	protected Workbook buildWorkbook() throws Exception {
		try {
			excel = new HSSFWorkbook();
			
			if (excel == null)
				throw new NullPointerException("excel is null");
			excel.createSheet();
		} catch (Exception e) {
			throw e;
		}
		
		return excel;
	}
	
	/**
	 * Заполнение {@link Sheet страницы} {@link Workbook Excel}
	 *
	 * @param sheet {@link Sheet страница} {@link Workbook Excel}
	 * @param tuples {@link List список} кортежей
	 * @return заполненная {@link Sheet страница} {@link Workbook Excel}
	 * @throws Exception {@link Exception ошибка}
	 */
	protected Sheet fillSheet(Sheet sheet, List<E> tuples) throws Exception {
		try {
			if (sheet == null) throw new NullPointerException("sheet is null");
			if (tuples == null) throw new NullPointerException("tuples is null");
			
			sheet = fillCaption(sheet);
			Integer startRowIndex = getStartRowIndex();
			for (int i = 0; i < tuples.size(); i++) {
				E tuple = tuples.get(i);
				Row row = sheet.createRow(startRowIndex + i);
				row = fillRow(tuple, row);
			}
		} catch (Exception e) {
			throw e;
		}
		
		return sheet;
	}
	
	/**
	 * Заполнение заголовка {@link Sheet страницы} {@link Workbook Excel}
	 *
	 * @param sheet {@link Sheet страница} {@link Workbook Excel}
	 * @return заполненная {@link Sheet страница} {@link Workbook Excel}
	 * @throws Exception {@link Exception ошибка}
	 */
	protected Sheet fillCaption(Sheet sheet) throws Exception {
		return sheet;
	}
	
	/**
	 * Заполнение {@link Row строки} данными из кортежа
	 *
	 * @param tuple кортеж
	 * @param row {@link Row строка}
	 * @return заполненная {@link Row строка}
	 * @throws Exception {@link Exception ошибка}
	 */
	protected abstract Row fillRow(E tuple, Row row) throws Exception;
	
	/**
	 * Заполнение {@link Cell ячейки} {@link Object значением}
	 *
	 * @param value {@link Object значение}
	 * @param style {@link CellStyle стиль} {@link Cell ячейки}. Если <code>null</code>, то параметр игнорируется
	 * @param cell {@link Cell ячейка}
	 * @return заполненная {@link Cell ячейка}
	 * @throws Exception {@link Exception ошибка}
	 */
	protected Cell fillCell(Object value, CellStyle style, Cell cell) throws Exception {
		try {
			if (cell == null)
				throw new NullPointerException("cell is null");
			
			if (value != null) {
				if (value instanceof Boolean)
					cell.setCellValue((Boolean)value);
				else if (value instanceof Calendar)
					cell.setCellValue((Calendar)value);
				else if (value instanceof Date)
					cell.setCellValue((Date)value);
				else if (value instanceof Double)
					cell.setCellValue((Double)value);
				else if (value instanceof RichTextString)
					cell.setCellValue((RichTextString)value);
				else
					cell.setCellValue(value.toString());
				
				if (style != null)
					cell.setCellStyle(style);
			}
		} catch (Exception e) {
			throw e;
		}
		
		return cell;
	}
	
	/**
	 * Возвращает {@link Integer индекс} {@link Row строки}, 
	 * с которого начинается заполнение данными из кортежа
	 *
	 * @return {@link Integer индекс} {@link Row строки}
	 * @throws Exception {@link Exception ошибка}
	 */
	protected abstract Integer getStartRowIndex() throws Exception;
	
	/**
	 * Конвертирует {@link Workbook объект Excel} в {@link ExcelData доменный объект}
	 *
	 * @return {@link ExcelData доменный объект}
	 * @throws Exception {@link Exception ошибка}
	 */
	protected byte[] convert() throws Exception {
		try {
			if (excel == null) throw new NullPointerException("excel object is null");
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			excel.write(baos);
			return baos.toByteArray();
		} catch (Exception e) {
			throw e;
		}
	}
}