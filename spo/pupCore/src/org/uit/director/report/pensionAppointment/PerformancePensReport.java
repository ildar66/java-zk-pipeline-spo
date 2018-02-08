package org.uit.director.report.pensionAppointment;

import java.util.List;

import org.uit.director.contexts.WorkflowSessionContext;
import org.uit.director.report.mainreports.PerformanceReport;

/**
 * Стистика производительности по процессу "Назначение пенсии".
 */
public class PerformancePensReport extends PerformanceReport {

    @Override
	public void init(WorkflowSessionContext wsc, List params) {

        nameReport = "Производительность процессов с учетом даты подачи последнего недостающего документа";
        super.init(wsc, params);

    }

    @Override
	public void generateReport() {
        genReportByProcedure("REPORT_PERFOMANCE_PENS_PROCESS");
    }
}
