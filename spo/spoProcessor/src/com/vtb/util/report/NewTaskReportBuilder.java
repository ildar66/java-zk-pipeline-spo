package com.vtb.util.report;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.vtb.domain.TaskListType;
import org.uit.director.db.ejb.DBFlexWorkflowCommon;
import org.uit.director.managers.DBMgr;
import org.w3c.dom.Element;

import com.vtb.domain.NewTaskReport;
import com.vtb.domain.NewTaskReportRecord;
import com.vtb.exception.MappingException;
import com.vtb.mapping.NewTaskReportMapper;
import com.vtb.mapping.ReportTemplateMapper;
import com.vtb.model.ReportBuilderActionProcessor.ReportTemplateParams;
import ru.md.spo.ejb.PupFacadeLocal;

/**
 * Builds an 'New Task'  (waiting for processing for the chosen user) report 
 * @author Michael Kuznetsov
 */
public class NewTaskReportBuilder extends AbstractReportBuilder {
 
	private final Long TEMPLATE_ID = 1L;
	private Long userId = null;   // parameter of the report.
	private NewTaskReportMapper mapper;   // mapper for database access.
	private List<Long> idTaskList; 
	
	public NewTaskReportBuilder(String reportName, NewTaskReportMapper mapper, ReportTemplateMapper reportMapper) throws Exception {
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
		String userIdStr = getParameter(parameters, ReportTemplateParams.USER_ID.getValue());
		userId = null;
		if (userIdStr != null) userId = Long.parseLong(userIdStr.trim());
	}	
	
	/**
	 * {@inheritDoc}
	 * @throws MappingException 
	 */	
	@SuppressWarnings("unchecked")
	@Override
	protected void getData() throws MappingException {
		/* typeList == 0 - входящие 1 -
	     * обработка, 2 - на исполнение (назначенные)*/
		try {
			PupFacadeLocal pupFacade = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
			idTaskList = pupFacade.getWorkList(this.userId, TaskListType.NOT_ACCEPT, null, 2000L, 0L);
		} catch (Exception e) {
			throw new MappingException (e, "NewTaskReportBuilder.getData()");
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */	
	@Override
	protected Element dataToXML() throws IllegalArgumentException, IllegalAccessException {
		NewTaskReport report = new NewTaskReport();
        
        // sets headers of the report.
        report.setHeaders("these are headers"); // dummy implementation here.

        // find data
        List<NewTaskReportRecord> records = new ArrayList<NewTaskReportRecord>();  
        for (Long idTask : idTaskList) {
    		try {
    			NewTaskReportRecord record = mapper.getRecord(idTask);
    			records.add(record);
    		} catch (NumberFormatException e) { } 
    		  catch (NullPointerException e) { }
    		  catch (MappingException e) {}
        }

        // sort the result by mdtask ASC, processDescription and operationDescription
        /*Collections.sort(records, new Comparator<NewTaskReportRecord>() {
        	public int compare(NewTaskReportRecord o1, NewTaskReportRecord o2) {
        		String str1 = o1.getMdTaskNumber() != null ?  o1.getMdTaskNumber() : "" 
        				+ o1.getDescriptionProcess() != null ? o1.getDescriptionProcess() : "" 
        				+ o1.getOperationDescription() != null ? o1.getOperationDescription() : ""; 
        		String str2 = o2.getMdTaskNumber() != null ?  o2.getMdTaskNumber() : "" 
            				+ o2.getDescriptionProcess() != null ? o2.getDescriptionProcess() : "" 
            				+ o2.getOperationDescription() != null ? o2.getOperationDescription() : "";
        		return str1.compareTo(str2);
        	}
        });
        */
        //  sets found records of the report.		
		report.setRecords(records);
		// collects all data and generates XML DOM.
        return report.toXML(document, rootElement);
	}
}
