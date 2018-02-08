package ru.md.report;

import ru.masterdm.reportsystem.annotation.ReportMark;
import ru.masterdm.reportsystem.annotation.ReportTemplate;
import ru.masterdm.reportsystem.annotation.ReportValueFormatter;
import ru.masterdm.reportsystem.core.domain.IReport;
import ru.masterdm.reportsystem.list.EFormatterType;

import java.math.BigDecimal;

/**
 * Created by Andrey Pavlenko on 03.06.2016.
 */
public class AsposeReportData {
    private byte[] reportTemplate;

    @ReportMark(name = "НазваниеОтчёта")
    private String reportName = "НазваниеОтчёта";

    /**
     * Возвращает {@link Byte шаблон} {@link IReport отчета}
     *
     * @return {@link Byte шаблон} {@link IReport отчета}
     */
    @ReportTemplate
    public byte[] getReportTemplate() {
        return reportTemplate;
    }

    /**
     * Устанавливает {@link Byte шаблон} {@link IReport отчета}
     *
     * @param reportTemplate
     *            {@link Byte шаблон} {@link IReport отчета}
     */
    public void setReportTemplate(byte[] reportTemplate) {
        this.reportTemplate = reportTemplate;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

}
