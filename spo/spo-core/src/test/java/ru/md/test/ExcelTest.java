package ru.md.test;

import com.aspose.cells.Chart;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import org.apache.poi.util.IOUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import ru.masterdm.reportsystem.ReportBuilderFactory;
import ru.masterdm.reportsystem.core.IReportBuilder;
import ru.masterdm.reportsystem.core.domain.IReport;
import ru.masterdm.reportsystem.list.EBuilderType;
import ru.md.report.TestAsposeReportData;
import ru.md.report.TestRow;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ExcelTest extends Assert {

	@Test(timeout=2000) @Ignore
	public  void testExcel() throws Exception {
		//задача сформировать отчёт с диаграммой и выгрузить её в отдельный файл как картинку
        TestAsposeReportData container = new TestAsposeReportData();
        FileInputStream fstream = new FileInputStream("D:\\tmp\\template.xls");
		IReportBuilder reportBuilder = ReportBuilderFactory.newInstance(EBuilderType.ASPOSE_EXTENDED_EXCEL);
        container.setReportTemplate(IOUtils.toByteArray(fstream));
        List<TestRow> list = new ArrayList<TestRow>();
        list.add(new TestRow(1L,2L,"первый"));
        list.add(new TestRow(2L,1L,"второй"));
        list.add(new TestRow(4L,5L,"третий"));
        container.setList(list);
        container.setReportName("hello, world");
        IReport report = reportBuilder.build(container);
        byte[] completedReport = report.getData();
        System.out.println("report size " + completedReport.length);
        FileOutputStream fos = new FileOutputStream("D:\\tmp\\result.xls");
        fos.write(completedReport);
        fos.close();

        Workbook workbook = new Workbook("D:\\tmp\\result.xls");
        Worksheet worksheet = workbook.getWorksheets().get(0);
        Chart chart = worksheet.getCharts().get(0);
        chart.toImage("D:\\tmp\\chart.jpeg");
	}

}
