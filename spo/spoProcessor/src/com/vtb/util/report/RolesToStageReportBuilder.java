package com.vtb.util.report;

import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Element;

import com.vtb.domain.RolesToStageReport;
import com.vtb.domain.RolesToStageReportHeader;
import com.vtb.domain.RolesToStageReportStage;
import com.vtb.exception.MappingException;
import com.vtb.mapping.ReportTemplateMapper;
import com.vtb.mapping.RolesToStageReportMapper;
import com.vtb.model.ReportBuilderActionProcessor.ReportTemplateParams;

/**
 * Builds an 'Roles To Stage'  (reportOrderStages) report (rolesByOperation.rptdesign)
 * @author Michael Kuznetsov
 */
public class RolesToStageReportBuilder extends AbstractReportBuilder {
 
	private final Long TEMPLATE_ID = 7L;
	
	private Long processId, stageId;  // report parameters

	private List<RolesToStageReportStage> stages;      // data for report
	private RolesToStageReportHeader header;   // header for a report

	private RolesToStageReportMapper mapper;   // mapper for database access.
	
	public RolesToStageReportBuilder(String reportName, RolesToStageReportMapper mapper, ReportTemplateMapper reportMapper) throws Exception {
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
		
		String stageIdStr = getParameter(parameters, ReportTemplateParams.STAGE_ID.getValue());		
		try {
			stageId = (stageIdStr != null) ? Long.parseLong(stageIdStr.trim()) : -1L;
		} catch (NumberFormatException e) {
			stageId = -1L;
		}
	}
	
	
	/**
	 * {@inheritDoc}
	 */	
	@Override
	protected void getData() throws MappingException {
		header = mapper.getHeaderData(processId);
		stages = mapper.getReportData(processId, stageId);
	}
	
	/**
	 * {@inheritDoc}
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * @throws MappingException
	 */
	@Override
	protected Element dataToXML() throws ParserConfigurationException, IllegalArgumentException, IllegalAccessException, TransformerException, MappingException {
		RolesToStageReport report = new RolesToStageReport();
		
		// getHeaders		 
		report.setHeaders(header);

		// get variables for the stage
		for (RolesToStageReportStage stage : stages) {
			stage.setRoles(mapper.getRoles(stage.getOperationId()));
		}
		report.setOperations(stages);

		// collects all data and generates XML DOM.
        return report.toXML(document, rootElement);
	}
}
