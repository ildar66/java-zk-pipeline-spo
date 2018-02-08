package com.vtb.util.report;

import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Element;

import ru.md.spo.ejb.StandardPeriodBeanLocal;

import com.vtb.domain.DurationStagesReport;
import com.vtb.domain.ReportTemplate;
import com.vtb.domain.StandardPeriod;
import com.vtb.exception.MappingException;
import com.vtb.mapping.DurationStagesReportMapper;
import com.vtb.mapping.ReportTemplateMapper;
import com.vtb.model.ReportBuilderActionProcessor.ReportTemplateParams;
import com.vtb.report.renderer.MSExcelRenderer;
import com.vtb.report.renderer.ReportRenderer;
import com.vtb.util.report.utils.DurationStagesDataExporter;

/**
 * Builds an 'Journal of operations for the claim'  (reportOrderStages) report 
 * @author Michael Kuznetsov
 */
public class DurationStagesReportBuilder extends AbstractReportBuilder {
 
	private final Long TEMPLATE_ID = 4L;
	
	private Long mdtaskNumber;

	private List<StandardPeriod> operations;      // data for report
	//private JournalOfOperationsReportHeader header;   // header for a report
	private DurationStagesReportMapper mapper;   // mapper for database access.

	
	public DurationStagesReportBuilder(String reportName, DurationStagesReportMapper mapper, ReportTemplateMapper reportMapper) throws Exception {
		super(reportName, reportMapper);
		this.mapper = mapper;
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
	@SuppressWarnings("unchecked")
	@Override
	protected void getReportParameters(Map parameters) {
		String mdtaskNumberStr = getParameter(parameters, ReportTemplateParams.MDTASK_NUMBER.getValue());
		mdtaskNumber = Long.parseLong(mdtaskNumberStr);
	}
	
	
	/**
	 * {@inheritDoc}
	 */	
	@Override
	protected void getData() throws MappingException {
		try {
		    StandardPeriodBeanLocal spLocal = com.vtb.util.EjbLocator.getInstance().getReference(StandardPeriodBeanLocal.class);
			operations = spLocal.getStandartPeriodReportByNumber(mdtaskNumber, false);
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
	
	protected Object dataToExcel() throws IllegalArgumentException {
		DurationStagesReport report = new DurationStagesReport();

		// sets headers of the report.
        //report.setHeaders(header); 
        
        // sets operations of the report.
        report.setOperations(operations);

        // collects all data and generates XML DOM.
        return null;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public ReportRenderer buildReport(Map parameters) throws Exception {
		String template;
		ReportTemplate rptTmpl; 
			getReportParameters(parameters);		
			getData();
		try {
			// Write to Excel 
			DurationStagesDataExporter exporter = new DurationStagesDataExporter(String.valueOf(mdtaskNumber));
			byte[] result = exporter.proceed(operations);
			
			// Create data for Renderer
			MSExcelRenderer renderer = new MSExcelRenderer();
			renderer.setReport(null);
			renderer.setReportName("Сроки прохождения этапов");   // rptTmpl.getName()
			renderer.setReportBytes(result);
			return renderer;
		} catch (Exception e) {
			throw new Exception ("Error in XSLT transformation (report building, AbstractReportBuilder.buildReport)\n" +
					"    Nested exception is: " + e.getMessage(), e);
		}
	}
	
}
