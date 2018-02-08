package com.vtb.util.report;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Element;

import ru.masterdm.compendium.model.CompendiumActionProcessor;

import com.vtb.domain.CRMClaimsReport;
import com.vtb.domain.CRMClaimsReportHeader;
import com.vtb.domain.CRMClaimsReportOperation;
import com.vtb.exception.MappingException;
import com.vtb.mapping.CRMClaimsReportMapper;
import com.vtb.mapping.ReportTemplateMapper;
import com.vtb.model.ReportBuilderActionProcessor.ReportTemplateParams;

/**
 * Builds an 'CRMClaims'  (active operations) report 
 * @author Michael Kuznetsov
 */
public class CRMClaimsReportBuilder extends AbstractReportBuilder {
 
	private final Long TEMPLATE_ID = 11L;
	
	private List<CRMClaimsReportOperation> operations;      // data for report
	private CRMClaimsReportHeader header;   // header for a report
	private CRMClaimsReportMapper mapper;   // mapper for database access.
	
	public CRMClaimsReportBuilder(String reportName, CRMClaimsReportMapper mapper, ReportTemplateMapper reportMapper) throws Exception {
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
		header = new CRMClaimsReportHeader();

		String departmentIdStr = getParameter(parameters, ReportTemplateParams.DEPARTMENT_ID.getValue());
		header.setDepartmentId( (departmentIdStr != null) ? departmentIdStr.trim() : "-1");
		
		SimpleDateFormat fromFormat = new SimpleDateFormat("dd.MM.yyyy");
		String sendLeftDateStr = getParameter(parameters, "sendLeftDate");
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
		
		String sendRightDateStr = getParameter(parameters, "sendRightDate");
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
		
		String acceptLeftDateStr = getParameter(parameters, ReportTemplateParams.ACCEPT_LEFT_DATE.getValue());
		if (acceptLeftDateStr != null) {
			try {
				@SuppressWarnings("unused")
				Date date = fromFormat.parse(acceptLeftDateStr.trim());
			} catch (Exception e) {
				acceptLeftDateStr = "01.01.2008";
			}
			header.setAcceptLeftDate(acceptLeftDateStr);
		} else {
			header.setAcceptLeftDate("01.01.2008");
		}
		
		String acceptRightDateStr = getParameter(parameters, "acceptRightDate");
		if (acceptRightDateStr != null) {
			try {
				@SuppressWarnings("unused")
				Date date = fromFormat.parse(acceptRightDateStr.trim());
			} catch (Exception e) {
				acceptRightDateStr = "01.01.2049";
			}
			header.setAcceptRightDate(acceptRightDateStr);
		} else {
			header.setAcceptRightDate("01.01.2049");
		}
	}	
	
	/**
	 * {@inheritDoc}
	 * @throws MappingException 
	 */	
	@Override
	protected void getData() throws MappingException {
		try {
			operations = mapper.getOperations(header);
			CompendiumActionProcessor compendium = (CompendiumActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("Compendium");
			for(CRMClaimsReportOperation o : operations){
    			//заполнить ФИО
			    o.setFio(compendium.findUserByLogin(o.getUserLogin()).getVo().getFullName());
    			//расшифровать статус
                if(o.getStatusCode().equals("1")) o.setStatusCode("Успешно загружено");
                if(o.getStatusCode().equals("2")) o.setStatusCode("Ошибка загрузки");
			}
		} catch (Exception e) {
			operations = new ArrayList<CRMClaimsReportOperation>();
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
		CRMClaimsReport report = new CRMClaimsReport();
		// set header data for the chosen process
		report.setHeaders(header);
		// set operations for the chosen process		
		report.setOperations(operations);

		// collects all data and generates XML DOM.
        return report.toXML(document, rootElement);
	}
}
