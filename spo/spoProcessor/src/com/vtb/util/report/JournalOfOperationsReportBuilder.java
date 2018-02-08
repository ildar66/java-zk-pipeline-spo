package com.vtb.util.report;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Element;

import com.vtb.domain.JournalOfOperationsReport;
import com.vtb.domain.JournalOfOperationsReportHeader;
import com.vtb.domain.JournalOfOperationsReportOperation;
import com.vtb.exception.MappingException;
import com.vtb.mapping.JournalOfOperationsReportMapper;
import com.vtb.mapping.ReportTemplateMapper;
import com.vtb.model.ReportBuilderActionProcessor.ReportTemplateParams;

/**
 * Builds an 'Journal of operations for the claim'  (reportOrderStages) report 
 * @author Michael Kuznetsov
 */
public class JournalOfOperationsReportBuilder extends AbstractReportBuilder {
 
	private final Long TEMPLATE_ID = 4L;
	
	private Long operationStatus;  				// report parameters
	private String mdtaskNumber, isDelinquency; // report parameters

	private List<JournalOfOperationsReportOperation> operations;      // data for report
	private JournalOfOperationsReportHeader header;   // header for a report

	
	private JournalOfOperationsReportMapper mapper;   // mapper for database access.
	
	public JournalOfOperationsReportBuilder(String reportName, JournalOfOperationsReportMapper mapper, ReportTemplateMapper reportMapper) throws Exception {
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
		mdtaskNumber = (mdtaskNumberStr != null) ? mdtaskNumberStr.trim().toUpperCase() : "0";
		
		isDelinquency = getParameter(parameters, ReportTemplateParams.IS_DELINQUENCY.getValue());		
		if (isDelinquency == null) isDelinquency = "off"; // default value
		
		String operationStatusStr = getParameter(parameters, ReportTemplateParams.OPERSTION_STATUS.getValue());		
		try {
			operationStatus = (operationStatusStr != null) ? Long.parseLong(operationStatusStr.trim()) : -1L;
		} catch (NumberFormatException e) {
			operationStatus = -1L;
		}
	}
	
	
	/**
	 * {@inheritDoc}
	 */	
	@Override
	protected void getData() throws MappingException {
		try {
			header = mapper.getHeaderData(mdtaskNumber, operationStatus, isDelinquency);
		} catch (Exception e) {
			header = new JournalOfOperationsReportHeader();
		}
		
		if ((header.getCRM_claim_name() != null) && (!header.getCRM_claim_name().equals(header.getInternal_claim_name()))) {
            // CRM claim
		    mdtaskNumber = header.getInternal_claim_name();
		    header.setMdtask( header.getCRM_claim_name() + " (" + mdtaskNumber + ")");
		}
		
		try {
			operations = mapper.getReportData(mdtaskNumber, operationStatus,	isDelinquency);
		} catch (Exception e) {
			operations = new ArrayList<JournalOfOperationsReportOperation>();
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
		JournalOfOperationsReport report = new JournalOfOperationsReport();

		// sets headers of the report.
        report.setHeaders(header); 
        
        // sets operations of the report.
        report.setOperations(operations);

        // collects all data and generates XML DOM.
        return report.toXML(document, rootElement);
	}
}
