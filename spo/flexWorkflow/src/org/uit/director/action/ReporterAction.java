package org.uit.director.action;


import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ru.masterdm.spo.service.IReporterService;
import ru.masterdm.spo.utils.SBeanLocator;
import ru.md.report.TestAsposeReportData;
import ru.md.report.TestRow;

import com.aspose.cells.CellsHelper;
import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
import com.aspose.cells.XlsSaveOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Action вывод тестового отчёта
 *
 */
public class ReporterAction extends Action {
    private static final Logger LOGGER = Logger.getLogger(ReporterAction.class.getName());
    //private static final String DASHBOARD_EXCEL_REPORT = "dashboard_report_xlsx";
    private static final String DASHBOARD_WORD_REPORT = "dashboard_report_docx";

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		LOGGER.info("================================= Тестовый отчёт word");
		IReporterService reporterService = SBeanLocator.singleton().getReporterService();
		if (reporterService == null) {
			LOGGER.info("================================= reporterService == null");
			return null;
		}
		TestAsposeReportData container = new TestAsposeReportData();
        List<TestRow> list = new ArrayList<TestRow>();
        list.add(new TestRow(1L,2L,"первый"));
        list.add(new TestRow(2L,1L,"второй"));
        list.add(new TestRow(4L,5L,"третий"));
        container.setList(list);
        container.setTestValue("проверочное поле установлено!");

        byte[] report = reporterService.buidWordReport(DASHBOARD_WORD_REPORT, container);
		response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
		response.setHeader("Content-Disposition","attachment; filename=test_report.docx");
		OutputStream os = response.getOutputStream();
        LOGGER.info("====================== report  before writing = " + report);
		if (report != null)
			os.write(report);
        return null;
    }
}
