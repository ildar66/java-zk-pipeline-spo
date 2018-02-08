package com.vtb.util.report;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Element;

import com.vtb.domain.NewDocumentsByClaimsReport;
import com.vtb.domain.NewDocumentsByClaimsReportClaim;
import com.vtb.domain.NewDocumentsByClaimsReportHeader;
import com.vtb.exception.MappingException;
import com.vtb.mapping.NewDocumentsByClaimsReportMapper;
import com.vtb.mapping.ReportTemplateMapper;
import com.vtb.model.ReportBuilderActionProcessor.ReportTemplateParams;

/**
 * Builds an 'NewDocumentsByClaims'  (new_documents_for_go_by_oders.rptdesign) report 
 * @author Michael Kuznetsov
 */
public class NewDocumentsByClaimsReportBuilder extends AbstractReportBuilder {
 
	private final Long TEMPLATE_ID = 12L;
	private Long departmentId;
	
	private List<NewDocumentsByClaimsReportClaim> claims;      // data for report
	private NewDocumentsByClaimsReportHeader header;   // header for a report
	private NewDocumentsByClaimsReportMapper mapper;   // mapper for database access.

	
	public NewDocumentsByClaimsReportBuilder(String reportName, NewDocumentsByClaimsReportMapper mapper, ReportTemplateMapper reportMapper) throws Exception {
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
		header = new NewDocumentsByClaimsReportHeader();
		
		String departmentIdStr = getParameter(parameters, ReportTemplateParams.DEPARTMENT_ID.getValue());
		try {
			departmentId = (departmentIdStr != null) ? Long.parseLong(departmentIdStr.trim()) : -1L;
		} catch (NumberFormatException e) {
			departmentId = -1L;
		}
		
		try {
			header.setDepartment_name( mapper.getHeaderData(departmentId));
		} catch (MappingException e) {header.setDepartment_name(""); }
		
		SimpleDateFormat fromFormat = new SimpleDateFormat("dd.MM.yyyy");
		String sendLeftDateStr = getParameter(parameters, ReportTemplateParams.LEFT_DATE.getValue());
		if (sendLeftDateStr != null) {
			try {
				@SuppressWarnings("unused")
				Date date = fromFormat.parse(sendLeftDateStr.trim());
			} catch (Exception e) {
				sendLeftDateStr = "01.01.1980";
			}
			header.setSendLeftDate(sendLeftDateStr);
		} else {
			header.setSendLeftDate("01.01.1980");
		}
		
		String sendRightDateStr = getParameter(parameters, ReportTemplateParams.RIGHT_DATE.getValue());
		if (sendRightDateStr != null) {
			try {
				@SuppressWarnings("unused")
				Date date = fromFormat.parse(sendRightDateStr.trim());
			} catch (Exception e) {
				sendRightDateStr = "01.01.2049";
			}
			header.setSendRightDate(sendRightDateStr);
		} else {
			header.setSendRightDate("01.01.2049");
		}
	}	
	
	/**
	 * {@inheritDoc}
	 * @throws MappingException 
	 */	
	@Override
	protected void getData() throws MappingException {
		try {
			claims = mapper.getClaims(header, departmentId);
		} catch (Exception e) {
			claims = new ArrayList<NewDocumentsByClaimsReportClaim>();
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * @throws MappingException 
	 */
	@Override
	protected Element dataToXML() throws ParserConfigurationException, IllegalArgumentException, IllegalAccessException, TransformerException, MappingException {
		NewDocumentsByClaimsReport report = new NewDocumentsByClaimsReport();
		report.setHeaders(header);

		// get claims for the chosen data
		for (NewDocumentsByClaimsReportClaim claim : claims) {
			// set operations for claim
			claim.setOperations( mapper.getOperationsForClaim(header, departmentId, claim.getClaimId()));
		}
		report.setClaims(claims);

		// collects all data and generates XML DOM.
        return report.toXML(document, rootElement);
	}
}
