package com.vtb.util.report;

import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Element;

import com.vtb.domain.AttributeTreeReport;
import com.vtb.domain.AttributeTreeReportHeader;
import com.vtb.domain.AttributeTreeReportOperation;
import com.vtb.exception.MappingException;
import com.vtb.mapping.AttributeTreeReportMapper;
import com.vtb.mapping.ReportTemplateMapper;
import com.vtb.model.ReportBuilderActionProcessor.ReportTemplateParams;

/**
 * Builds an 'AttributeTree'  (active operations) report 
 * @author Michael Kuznetsov
 */
public class AttributeTreeReportBuilder extends AbstractReportBuilder {
 
	private final Long TEMPLATE_ID = 10L;

	private Long processId;
	
	private List<AttributeTreeReportOperation> operations;      // data for report
	private AttributeTreeReportHeader header;   // header for a report
	private AttributeTreeReportMapper mapper;   // mapper for database access.
	
	public AttributeTreeReportBuilder(String reportName, AttributeTreeReportMapper mapper, ReportTemplateMapper reportMapper) throws Exception {
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
		String processIdStr = getParameter(parameters, ReportTemplateParams.PROCESS_ID.getValue());
		try {
			processId = (processIdStr != null) ? Long.parseLong(processIdStr.trim()) : -1L;
		} catch (NumberFormatException e) {
			processId = -1L;
		}
	}	
	
	/**
	 * {@inheritDoc}
	 * @throws MappingException 
	 */	
	@Override
	protected void getData() throws MappingException {
		header = mapper.getHeaderData(processId);
		operations = mapper.getOperations(processId);		
	}
	
	/**
	 * {@inheritDoc}
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * @throws MappingException 
	 */
	@Override
	protected Element dataToXML() throws ParserConfigurationException, IllegalArgumentException, IllegalAccessException, TransformerException, MappingException {
		AttributeTreeReport report = new AttributeTreeReport();

		// sets headers of the report		 
		report.setHeaders(header);
		
		// set operations for the chosen process		
		report.setOperations(operations);

		// collects all data and generates XML DOM.
        return report.toXML(document, rootElement);
	}
}
