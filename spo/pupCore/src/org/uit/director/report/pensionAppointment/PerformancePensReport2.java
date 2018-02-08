package org.uit.director.report.pensionAppointment;

import java.util.List;

import org.uit.director.contexts.WorkflowSessionContext;

/**
 * Created by IntelliJ IDEA.
 * User: PD190390
 * Date: 08.09.2006
 * Time: 11:49:24
 * To change this template use File | Settings | File Templates.
 */
public class PerformancePensReport2 extends PerformancePensReport1 {

    @Override
	public void init(WorkflowSessionContext wsc, List params) {

        nameReport = "Производительность процессов с учетом выходных дней и даты подачи последнего недостающего документа";
        super.init(wsc, params);

    }

    @Override
	public void generateReport() {
        genByCallProcedure(1);
    }

}
