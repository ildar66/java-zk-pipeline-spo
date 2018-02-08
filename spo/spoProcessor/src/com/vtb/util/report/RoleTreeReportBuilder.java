package com.vtb.util.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Element;

import ru.masterdm.compendium.model.CompendiumActionProcessor;

import com.vtb.domain.RoleTreeReport;
import com.vtb.domain.RoleTreeReportDepartment;
import com.vtb.domain.RoleTreeReportHeader;
import com.vtb.domain.RoleTreeReportOperation;
import com.vtb.exception.MappingException;
import com.vtb.mapping.ReportTemplateMapper;
import com.vtb.mapping.RoleTreeReportMapper;
import com.vtb.model.ReportBuilderActionProcessor.ReportTemplateParams;

/**
 * Builds an 'Role Tree'  (active operations) report 
 * @author Michael Kuznetsov
 */
public class RoleTreeReportBuilder extends AbstractReportBuilder {
 
	private final Long TEMPLATE_ID = 9L;

	private Long processId, departmentId;
	private String showUnactive;
	
	private List<RoleTreeReportOperation> operations;      // data for report
	private RoleTreeReportHeader header;   // header for a report
	private RoleTreeReportMapper mapper;   // mapper for database access.
	private String[] defaultDepartments;   // list of departments for the report
	
	public RoleTreeReportBuilder(String reportName, RoleTreeReportMapper mapper, ReportTemplateMapper reportMapper) throws Exception {
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

		String departmentIdStr = getParameter(parameters, ReportTemplateParams.ID_DEPARTMENT.getValue());
		try {
			departmentId = (departmentIdStr != null) ? Long.parseLong(departmentIdStr.trim()) : 1L;
		} catch (NumberFormatException e) {
			departmentId = 1L;
		}
	
		defaultDepartments = getNumeratedParams(parameters, ReportTemplateParams.REPORT_FILTER_DEPARMENTS_STRing.getValue());

		showUnactive = getParameter(parameters, ReportTemplateParams.SHOW_UNACTIVE.getValue());
		if (showUnactive == null) showUnactive = "0"; // default value
	}	
	
	/**
	 * {@inheritDoc}
	 * @throws MappingException 
	 */	
	@Override
	protected void getData() throws MappingException {	}
	
	/**
	 * {@inheritDoc}
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * @throws MappingException 
	 */
	@Override
	protected Element dataToXML() throws ParserConfigurationException, IllegalArgumentException, IllegalAccessException, TransformerException, MappingException {
		RoleTreeReport report = new RoleTreeReport();

		CompendiumActionProcessor compenduim = 
            (CompendiumActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("Compendium");
		
		// sets headers of the report		 
		header = mapper.getHeaderData(processId, departmentId, showUnactive);
		report.setHeaders(header);
		
        // get departments for the chosen process
        List<RoleTreeReportDepartment> departments = null;
        if (departmentId.equals(-1L)) {
            // a list of the departments should be considered.
            if (departmentId.equals(-1L) && (defaultDepartments != null)) departments = mapper.getDepartmentsList(defaultDepartments);
            else departments = mapper.getDepartments(departmentId);
        } else {
            // only one department was chosen from the list.
            departments = mapper.getDepartments(departmentId);
        }
        List<RoleTreeReportDepartment> departmentsWithUsers = new ArrayList<RoleTreeReportDepartment>(); 

        for (RoleTreeReportDepartment department : departments) {
            operations = mapper.getOperations(processId, showUnactive);
            boolean foundUser = false;
            // add users of the operation
            for (RoleTreeReportOperation operation : operations) {
                // get the users for the department
                List<String> users = mapper.getUsersForRoles(operation.getRoleId(), department.getDepartmentId());
                operation.setUsers(users);
                if (users != null && (users.size() > 0)) foundUser = true;
            }

            // show department if only users for operations are found or MANY departments are chosen by the user
            // or if we show ALL ROLES, including unactive 
            if (foundUser || !(departmentId.equals(-1L)) || showUnactive.equals("1")) {
                // sets operations of the report.
                department.setOperations(operations);
                try {
                    // find full department name
                    String fullDepartmentName = compenduim.findDepartmentFullPath(department.getDepartmentId().intValue(), true);
                    department.setDepartment_name(fullDepartmentName);
                } catch (Exception e) {
                    // надо бы в лог записать
                }
                departmentsWithUsers.add(department);
            }
        }
        // отсортируем по имени департамента.
        Collections.sort(departmentsWithUsers, new Comparator<RoleTreeReportDepartment>() {
            public int compare(RoleTreeReportDepartment o1, RoleTreeReportDepartment o2) {
                if (o2.getDepartment_name() == null) return -1;
                if (o1.getDepartment_name() == null) return 1;
                return o1.getDepartment_name().compareTo(o2.getDepartment_name());
            }
        });
        report.setDepartments(departmentsWithUsers);
        
        // пустой отчет
        if (departmentsWithUsers.size() == 0) {
            header.setInformation("Не найдены подразделения, в которых роли процесса " + header.getProcess_name() +" назначены пользователям подразделения. "
                    + "Назначьте пользователям роли. ");
        }

		// collects all data and generates XML DOM.
        return report.toXML(document, rootElement);
	}
}
