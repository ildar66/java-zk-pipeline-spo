package com.vtb.util.report;

import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Element;

import ru.md.spo.ejb.StandardPeriodBeanLocal;

import com.vtb.domain.StandardPeriod;
import com.vtb.exception.MappingException;
import com.vtb.model.ReportBuilderActionProcessor.ReportTemplateParams;
import com.vtb.report.renderer.MSExcelRenderer;
import com.vtb.report.renderer.ReportRenderer;
import com.vtb.util.report.utils.DurationExpertiseDataExporter;

/**
 * Builds an 'Journal of operations for the claim' (reportOrderStages) report
 * @author Michael Kuznetsov
 */
public class DurationExpertiseReportBuilder extends AbstractReportBuilder {

	private Long mdtaskNumber;

	private List<StandardPeriod> operations; // data for report

	public DurationExpertiseReportBuilder(String reportName) throws Exception {
		super(reportName, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Long getTemplateId() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings({ "rawtypes" })
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
			StandardPeriodBeanLocal spLocal = com.vtb.util.EjbLocator.getInstance().getReference(
					StandardPeriodBeanLocal.class);
			operations = spLocal.getStandartPeriodReportByNumber(mdtaskNumber, true);
		}
		catch (Exception e) {
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
	protected Element dataToXML() throws ParserConfigurationException, IllegalArgumentException,
			IllegalAccessException, TransformerException {
		throw new IllegalArgumentException("Method is not implemented!");
	}

	@Override
	@SuppressWarnings("rawtypes")
	public ReportRenderer buildReport(Map parameters) throws Exception {
		// get data for the report
		// get report parameters
		getReportParameters(parameters);
		// get report data with the found parameters
		getData();
		try {
			// Write to Excel
			DurationExpertiseDataExporter exporter = new DurationExpertiseDataExporter(String
					.valueOf(mdtaskNumber));
			byte[] result = exporter.proceed(operations);

			// Create data for Renderer
			MSExcelRenderer renderer = new MSExcelRenderer();
			renderer.setReport(null);
			renderer.setReportName("Сроки прохождения экспертиз"); // rptTmpl.getName()
			renderer.setReportBytes(result);
			return renderer;
		}
		catch (Exception e) {
			throw new Exception(
					"Error in transformation (report building, AbstractReportBuilder.buildReport)\n"
							+ "    Nested exception is: " + e.getMessage(), e);
		}
	}
}
