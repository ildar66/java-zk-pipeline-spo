package com.vtb.util.report;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.joda.time.DateTime;
import org.w3c.dom.Element;

import ru.masterdm.spo.utils.SBeanLocator;
import ru.md.domain.AuditDurationStage;
import ru.md.domain.AuditDurationTasksHistory;
import ru.md.persistence.ReportMapper;
import ru.md.spo.ejb.StandardPeriodBeanLocal;
import ru.md.spo.report.TaskStandardPeriod;

import com.vtb.domain.StandardPeriod;
import com.vtb.exception.MappingException;
import com.vtb.mapping.DurationStagesReportMapper;
import com.vtb.mapping.ReportTemplateMapper;
import com.vtb.report.renderer.MSExcelRenderer;
import com.vtb.report.renderer.ReportRenderer;
import com.vtb.util.Formatter;
import com.vtb.util.report.utils.AuditDurationStagesDataExporter;
import com.vtb.util.report.utils.DurationStagesDataExporter;

/**
 * @author Andrey Pavlenko
 */
public class AuditDurationStagesReportBuilder extends AbstractReportBuilder {
 
	private final Long TEMPLATE_ID = 4L;
	
	private Date from;
	private Date to;
	private Long processID;

	private List<AuditDurationStage> operations;      // data for report

	
	public AuditDurationStagesReportBuilder(String reportName, DurationStagesReportMapper mapper, ReportTemplateMapper reportMapper) throws Exception {
		super(reportName, reportMapper);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Long getTemplateId() {
		return TEMPLATE_ID;
	}
	
	/**
	 * {@inheritDoc}
	 */	
	@Override
	protected void getReportParameters(@SuppressWarnings("rawtypes") Map parameters) {
		String processIDStr = getParameter(parameters, "currentProcess");
		processID = Long.parseLong(processIDStr);
		LOGGER.info("processID="+processID);
		from = Formatter.parseDate(getParameter(parameters, "sendLeftDate"));
		LOGGER.info("from="+from);
		to = Formatter.parseDate(getParameter(parameters, "sendRightDate"));
		LOGGER.info("to="+to);
	}


	/**
	 * {@inheritDoc}
	 */	
	@Override
	protected void getData() throws MappingException {
		try {
			StandardPeriodBeanLocal spLocal = com.vtb.util.EjbLocator.getInstance().getReference(StandardPeriodBeanLocal.class);
			operations = spLocal.getAuditDurationStagesReport(from, to, processID);
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
			throw new MappingException(e.getMessage());
		}
	}


	
	/**
	 * {@inheritDoc}
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * @throws MappingException 
	 */
	@Override
	protected Element dataToXML() throws ParserConfigurationException, IllegalArgumentException, IllegalAccessException, TransformerException {
		throw new IllegalArgumentException("Method is not implemented!");
	}
	
	@Override
	public ReportRenderer buildReport(@SuppressWarnings("rawtypes") Map parameters) throws Exception {
		getReportParameters(parameters);
		if(from==null || to==null)
			throw new Exception("Необходимо заполнить даты в фильтре");
		getData();
		try {
			// Write to Excel 
			AuditDurationStagesDataExporter exporter = new AuditDurationStagesDataExporter(processID,from,to);
			byte[] result = exporter.proceed(operations);
			
			// Create data for Renderer
			MSExcelRenderer renderer = new MSExcelRenderer();
			renderer.setReport(null);
			renderer.setReportName("Аудит прохождения заявки");   // rptTmpl.getName()
			renderer.setReportBytes(result);
			return renderer;
		} catch (Exception e) {
			throw new Exception ("Error in XSLT transformation (report building, AbstractReportBuilder.buildReport)\n" +
					"    Nested exception is: " + e.getMessage(), e);
		}
	}
	
}
