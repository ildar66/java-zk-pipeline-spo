package ru.md.report;

import ru.masterdm.reportsystem.annotation.ReportMark;
import ru.md.domain.dashboard.DetailReportRow;

import java.util.List;

/**
 * Контейнер для данных отчёта Dashboard. 
 * @author slysenkov
 */
public class DashboardReportData extends AsposeReportData {
    private List<DetailReportRow> reportRows;

    @ReportMark(name = "Заголовок")
    private String title;
    
    @ReportMark(name = "Лимит")
    private boolean limit;

	@ReportMark(name = "ИмяЛиста")
	private String workSheetName;

    /**
     * Возвращает список объектов, содержащих данные строки отчёта.
     * @return список объектов, содержащих данные строки отчёта
     */
    @ReportMark(name = "СтрокиОтчёта.", complex = true, collection = true)
    public List<DetailReportRow> getReportRows() {
		return reportRows;
	}

    /**
     * Устанавливает список объектов, содержащих данные строки отчёта.
     * @param reportRows список объектов, содержащих данные строки отчёта
     */
	public void setReportRows(List<DetailReportRow> reportRow) {
		this.reportRows = reportRow;
	}

    /**
     * Возвращает заголовок отчёта.
     * @return заголовок отчёта
     */
	public String getTitle() {
		return title;
	}

    /**
     * Устанавливает заголовок отчёта.
     * @param title заголовок отчёта
     */
	public void setTitle(String title) {
		this.title = title;
	}

    /**
     * Возвращает флаг лимита.
     * @return флаг лимита
     */
	public boolean isLimit() {
		return limit;
	}

    /**
     * Устанавливает флаг лимита.
     * @param limit флаг лимита
     */
	public void setLimit(boolean limit) {
		this.limit = limit;
	}

	/**
	 * Возвращает имя листа для отчёта Excel.
	 * @return имя листа для отчёта Excel
	 */
	public String getWorkSheetName() {
		return workSheetName;
	}

	/**
	 * Устанавливает имя листа для отчёта Excel.
	 * @param workSheetName имя листа для отчёта Excel
	 */
	public void setWorkSheetName(String workSheetName) {
		this.workSheetName = workSheetName;
	}
}
