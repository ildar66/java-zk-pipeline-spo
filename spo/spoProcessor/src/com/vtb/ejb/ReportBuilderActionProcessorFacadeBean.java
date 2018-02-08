package com.vtb.ejb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import com.vtb.domain.ActiveStagesReport;
import com.vtb.domain.AppointedTaskReport;
import com.vtb.domain.AttributeTreeReport;
import com.vtb.domain.CRMClaimsReport;
import com.vtb.domain.DurationStagesReport;
import com.vtb.domain.JournalOfOperationsReport;
import com.vtb.domain.NewDocumentsByClaimsReport;
import com.vtb.domain.NewDocumentsByOrgsReport;
import com.vtb.domain.NewTaskReport;
import com.vtb.domain.ReportTemplate;
import com.vtb.domain.ReportTemplate.ReportTemplateTypeEnum;
import com.vtb.domain.RoleTreeReport;
import com.vtb.domain.RolesOfUsersReport;
import com.vtb.domain.RolesToStageReport;
import com.vtb.domain.TaskInWorkReport;
import com.vtb.domain.VariablesToStageReport;
import com.vtb.exception.MappingException;
import com.vtb.exception.NoSuchObjectException;
import com.vtb.mapping.ActiveStagesReportMapper;
import com.vtb.mapping.AppointedTaskReportMapper;
import com.vtb.mapping.AttributeTreeReportMapper;
import com.vtb.mapping.CRMClaimsReportMapper;
import com.vtb.mapping.DurationStagesReportMapper;
import com.vtb.mapping.JournalOfOperationsReportMapper;
import com.vtb.mapping.MapperFactory;
import com.vtb.mapping.NewDocumentsByClaimsReportMapper;
import com.vtb.mapping.NewDocumentsByOrgsReportMapper;
import com.vtb.mapping.NewTaskReportMapper;
import com.vtb.mapping.ReportTemplateMapper;
import com.vtb.mapping.RoleTreeReportMapper;
import com.vtb.mapping.RolesOfUsersReportMapper;
import com.vtb.mapping.RolesToStageReportMapper;
import com.vtb.mapping.TaskInWorkReportMapper;
import com.vtb.mapping.VariablesToStageReportMapper;
import com.vtb.report.renderer.ReportRenderer;
import com.vtb.util.report.AbstractReportBuilder;
import com.vtb.util.report.ActiveStagesReportBuilder;
import com.vtb.util.report.AppointedTaskReportBuilder;
import com.vtb.util.report.AttributeTreeReportBuilder;
import com.vtb.util.report.AuditDurationStagesReportBuilder;
import com.vtb.util.report.BasePrintReportBuilder;
import com.vtb.util.report.CRMClaimsReportBuilder;
import com.vtb.util.report.DurationExpertiseReportBuilder;
import com.vtb.util.report.DurationStagesReportBuilder;
import com.vtb.util.report.JournalOfOperationsReportBuilder;
import com.vtb.util.report.LimitDecisionReportBuilder;
import com.vtb.util.report.NewDocumentsByClaimsReportBuilder;
import com.vtb.util.report.NewDocumentsByOrgsReportBuilder;
import com.vtb.util.report.NewTaskReportBuilder;
import com.vtb.util.report.RoleTreeReportBuilder;
import com.vtb.util.report.RolesOfUsersReportBuilder;
import com.vtb.util.report.RolesToStageReportBuilder;
import com.vtb.util.report.TaskBasedJoinDocumentsBuilder;
import com.vtb.util.report.TaskBasedReportWordBuilder;
import com.vtb.util.report.TaskInWorkReportBuilder;
import com.vtb.util.report.VariablesToStageReportBuilder;


/**
 * Main report generation bean.
 * @author Michael Kuznetsov 
 */
// Now use a deployment descriptor manually  - MK 
//@Stateless
@PersistenceContext(name="flexWorkflowJPA", unitName = "flexWorkflowJPA", type = PersistenceContextType.TRANSACTION)
public class ReportBuilderActionProcessorFacadeBean implements ReportBuilderActionProcessorLocal, ReportBuilderActionProcessorRemote {

    static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ReportBuilderActionProcessorFacadeBean.class.getName());

	   /**
     * Report builder factory.
     * @param reportName  name of the report
     * @param reportType  type of the report
     * @return concrete class of a report builder
     * @throws Exception 
     */
    private AbstractReportBuilder getBuilder(String reportName, String reportType) throws Exception {   
        MapperFactory f = MapperFactory.getReserveMapperFactory();
        ReportTemplateMapper mapper = (ReportTemplateMapper) f.getMapper(ReportTemplate.class);
        if (reportName != null) reportName = reportName.trim(); 

        if (reportName.indexOf(ReportTemplateReportName.NEW_TASKS_REPORT.getValue())!= -1) 
            return new NewTaskReportBuilder(ReportTemplateReportName.NEW_TASKS_REPORT.getValue(), 
                (NewTaskReportMapper)f.getMapper(NewTaskReport.class), mapper);
                            
        if (reportName.indexOf(ReportTemplateReportName.TASK_IN_WORK_REPORT.getValue())!= -1) 
            return new TaskInWorkReportBuilder(ReportTemplateReportName.TASK_IN_WORK_REPORT.getValue(),
                (TaskInWorkReportMapper)f.getMapper(TaskInWorkReport.class), mapper);

        if (reportName.indexOf(ReportTemplateReportName.APPOINTED_TASK_REPORT.getValue())!= -1) 
            return new AppointedTaskReportBuilder(ReportTemplateReportName.APPOINTED_TASK_REPORT.getValue(),
                (AppointedTaskReportMapper)f.getMapper(AppointedTaskReport.class), mapper);
        
        if (reportName.indexOf(ReportTemplateReportName.ACTIVE_OPERATION_REPORT.getValue())!= -1) 
            return new ActiveStagesReportBuilder(ReportTemplateReportName.ACTIVE_OPERATION_REPORT.getValue(),
                (ActiveStagesReportMapper)f.getMapper(ActiveStagesReport.class), mapper);
        
        if (reportName.indexOf(ReportTemplateReportName.OPERATION_JOURNAL_REPORT.getValue())!= -1) 
            return new JournalOfOperationsReportBuilder(ReportTemplateReportName.OPERATION_JOURNAL_REPORT.getValue(),
                (JournalOfOperationsReportMapper)f.getMapper(JournalOfOperationsReport.class), mapper);

        if (reportName.indexOf(ReportTemplateReportName.DURATION_STAGES_REPORT.getValue())!= -1) 
        	return new DurationStagesReportBuilder(ReportTemplateReportName.DURATION_STAGES_REPORT.getValue(),
        			(DurationStagesReportMapper)f.getMapper(DurationStagesReport.class), mapper);

        if (reportName.indexOf(ReportTemplateReportName.AUDIT_DUR_STAGES_REPORT.getValue())!= -1) 
            return new AuditDurationStagesReportBuilder(ReportTemplateReportName.AUDIT_DUR_STAGES_REPORT.getValue(),
                null, mapper);

        if (reportName.indexOf(ReportTemplateReportName.DURATION_EXPERTISE_REPORT.getValue())!= -1) 
            return new DurationExpertiseReportBuilder(ReportTemplateReportName.DURATION_EXPERTISE_REPORT.getValue());
        
        if (reportName.indexOf(ReportTemplateReportName.VARIABLES_TO_STAGE_REPORT.getValue())!= -1) 
            return new VariablesToStageReportBuilder(ReportTemplateReportName.VARIABLES_TO_STAGE_REPORT.getValue(),
                (VariablesToStageReportMapper)f.getMapper(VariablesToStageReport.class), mapper);
        
        if (reportName.indexOf(ReportTemplateReportName.ROLES_TO_STAGE_REPORT.getValue())!= -1) 
            return new RolesToStageReportBuilder(ReportTemplateReportName.ROLES_TO_STAGE_REPORT.getValue(),
                (RolesToStageReportMapper)f.getMapper(RolesToStageReport.class), mapper);
        
        if (reportName.indexOf(ReportTemplateReportName.ROLES_OF_USERS_REPORT.getValue())!= -1) 
            return new RolesOfUsersReportBuilder(ReportTemplateReportName.ROLES_OF_USERS_REPORT.getValue(),
                (RolesOfUsersReportMapper)f.getMapper(RolesOfUsersReport.class), mapper);
        
        if (reportName.indexOf(ReportTemplateReportName.ROLE_TREE_REPORT.getValue())!= -1) 
            return new RoleTreeReportBuilder(ReportTemplateReportName.ROLE_TREE_REPORT.getValue(),
                (RoleTreeReportMapper)f.getMapper(RoleTreeReport.class), mapper);

        if (reportName.indexOf(ReportTemplateReportName.ATTRIBUTE_TREE_REPORT.getValue())!= -1) 
            return new AttributeTreeReportBuilder(ReportTemplateReportName.ATTRIBUTE_TREE_REPORT.getValue(),
                (AttributeTreeReportMapper)f.getMapper(AttributeTreeReport.class), mapper);
        
        if (reportName.indexOf(ReportTemplateReportName.CRM_CLAIMS_REPORT.getValue())!= -1) 
            return new CRMClaimsReportBuilder(ReportTemplateReportName.CRM_CLAIMS_REPORT.getValue(),
                (CRMClaimsReportMapper)f.getMapper(CRMClaimsReport.class), mapper);
        
        if (reportName.indexOf(ReportTemplateReportName.NEW_DOCUMENTS_BY_CLAIMS_REPORT.getValue())!= -1) 
            return new NewDocumentsByClaimsReportBuilder(ReportTemplateReportName.NEW_DOCUMENTS_BY_CLAIMS_REPORT.getValue(),
                (NewDocumentsByClaimsReportMapper) f.getMapper(NewDocumentsByClaimsReport.class), mapper);
        
        if (reportName.indexOf(ReportTemplateReportName.NEW_DOCUMENTS_BY_ORGANIZATIONS_REPORT.getValue())!= -1) 
            return new NewDocumentsByOrgsReportBuilder(ReportTemplateReportName.NEW_DOCUMENTS_BY_ORGANIZATIONS_REPORT.getValue(),
                (NewDocumentsByOrgsReportMapper) f.getMapper(NewDocumentsByOrgsReport.class), mapper);
        
        // Has type 'STYLE' (to hide from viewing in the list of all reports)
        if (ReportTemplateReportName.LIMIT_DECISION.getValue().equalsIgnoreCase(reportName))
                return new LimitDecisionReportBuilder(mapper, false);

        /***********************************************************************************************/
        /*                                   Print form reports                                        */
        /***********************************************************************************************/
        if ((reportType != null) && (reportType.equalsIgnoreCase(ReportTemplateTypeEnum.PRINT_FORM_TYPE.getValue()))) {
            return new BasePrintReportBuilder(reportName, mapper, false);
        }           

        /***********************************************************************************************/
        /*          MS Word print form reports (in the form DOC). We don't generate XML here            */
        /***********************************************************************************************/

        if ((reportType != null) && (reportType.equalsIgnoreCase(ReportTemplateTypeEnum.PRINT_DOC_REPORT_TYPE.getValue()))) {
            return getMSWordReportBuilder(reportName, mapper);
        }

        // builder not found.
        mapper = null; f = null;
        throw new IllegalArgumentException ("getBuilder: the appropriate builder not found for the report type: " + reportName);         
    }
    
    /**
     * Finds appropriate builder for MSWord report  
     */
    private AbstractReportBuilder getMSWordReportBuilder(String reportName, ReportTemplateMapper mapper) throws Exception {
        return new TaskBasedReportWordBuilder(reportName, mapper, false);
    }
    
    
    private ReportTemplateMapper getReportTemplateMapper() {
        MapperFactory f = MapperFactory.getReserveMapperFactory();
        return (ReportTemplateMapper) f.getMapper(ReportTemplate.class);
    }


    
    
    /**
     * @throws Exception ***************************************************************************************************/
    
    
    @SuppressWarnings({ "rawtypes" })
    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public ReportRenderer getReport (String reportType, Map parameters) throws Exception {
        // get type of the report
        String type = null;
        String[] reportTypes = (String[])parameters.get("__reportType");
        if (reportTypes != null) type = reportTypes[0];
        if(type == null) type = "excel";
        LOGGER.info("reportType=" + reportType);
        if(reportType == null) reportType = "file:///C:/Temp/reports/audit_dur_stages";
        return getBuilder(reportType, type).buildReport(parameters);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public ReportRenderer getPrintFormReport(String reportName, String mdTaskId, boolean dynamicEncoding) {
        return getReportInternal(reportName, mdTaskId, ReportTemplateTypeEnum.PRINT_FORM_TYPE.getValue());
    }      

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public ReportRenderer getPrintFormWordReport(String reportName, String mdTaskId) {
        return getReportInternal(reportName, mdTaskId, ReportTemplateTypeEnum.PRINT_DOC_REPORT_TYPE.getValue());
    }

    //@Override TODO: try to compile
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public ReportRenderer getPrintFormExcelReport(String reportName, String mdTaskId) {
        return getReportInternal(reportName, mdTaskId, ReportTemplateTypeEnum.PRINT_EXCEL_REPORT_TYPE.getValue());
    }

    
	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public ReportRenderer getLimitDecisionReport (String mdTaskId, boolean dynamicEncoding) {
        return getReportInternal(ReportTemplateReportName.LIMIT_DECISION.getValue(), mdTaskId, ReportTemplateTypeEnum.PRINT_FORM_TYPE.getValue());
    }

   @Override
   @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public ReportRenderer getLimitDecisionReportAsWord(String mdTaskId, String templateId, boolean dynamicEncoding) {
       return getReportInternal(templateId, mdTaskId, ReportTemplateTypeEnum.PRINT_DOC_REPORT_TYPE.getValue()); 
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public ReportTemplate findByFilename(String filename) throws MappingException {
        try {
            return getReportTemplateMapper().findByFilename(filename);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
            throw new MappingException(e, "Exception caught in ReportBuilderActionProcessorFacadeBean.findByFilename");
        }
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<ReportTemplate> findByType(String type) throws NoSuchObjectException, MappingException {
        try {
            return getReportTemplateMapper().findByType(type);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
            throw new MappingException(e, "Exception caught in ReportBuilderActionProcessorFacadeBean.findByType");
        }   
    }

    /**
     * Implementation of report building mechanism.
     */
    private ReportRenderer getReportInternal(String reportName, String mdTaskId, String reportType) {
        try {
            String[] reportTypeList = {reportName};
            String[] mdTaskList = {mdTaskId};
            HashMap<String,String[]> parameters = new HashMap<String,String[]>();
            parameters.put(ReportTemplateParams.MDTASK_ID.getValue(), mdTaskList);
            parameters.put(ReportTemplateParams.REPORT_NAME.getValue(), reportTypeList);
            return getBuilder(reportName, reportType).buildReport(parameters);
        } catch (Exception e) {
            Logger LOGGER = Logger.getLogger(ReportBuilderActionProcessorFacadeBean.class.getName());
            LOGGER.log(Level.SEVERE, "Building the report " + reportName + " Exception: " + e.getMessage(), e);
            return null;
        }
    }
    
	@Override
	public ReportRenderer getPrintFormExcelReport(String reportName, String mdTaskId, boolean dynamicEncoding) throws Exception {
		// TODO Реализовать метод
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public ReportRenderer getTaskBasedJoinDocumentReport(String mdTaskId, String templateId, boolean dynamicEncoding, 
                                                         Map<String, String> extraParameters, byte[] sourceDoc) throws Exception {
        try {
            LOGGER.info("getTaskBasedJoinDocumentReport templateId="+templateId);
            String[] reportTypeList = {templateId};
            String[] mdTaskList = {mdTaskId};
            HashMap<String,String[]> parameters = new HashMap<String,String[]>();
            parameters.put(ReportTemplateParams.MDTASK_ID.getValue(), mdTaskList);
            parameters.put(ReportTemplateParams.REPORT_NAME.getValue(), reportTypeList);
            if (extraParameters != null) {
                for (Entry<String, String> entry : extraParameters.entrySet()) {
                    String[] paramValue = new String[1];
                    paramValue[0] = entry.getValue().toString();
                    parameters.put(entry.getKey(), paramValue);        
                }
            }

            MapperFactory f = MapperFactory.getReserveMapperFactory();
            ReportTemplateMapper mapper = (ReportTemplateMapper) f.getMapper(ReportTemplate.class);
            TaskBasedJoinDocumentsBuilder builder = new TaskBasedJoinDocumentsBuilder(templateId, mapper, true);
            return builder.buildReport(parameters, sourceDoc);
        } catch (Exception e) {
            Logger LOGGER = Logger.getLogger(ReportBuilderActionProcessorFacadeBean.class.getName());
            LOGGER.log(Level.SEVERE, "Building the report " + templateId + " Exception: " + e.getMessage(), e);
            return null;
        }
	}
}
