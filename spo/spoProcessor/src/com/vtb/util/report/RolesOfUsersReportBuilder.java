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

import com.vtb.domain.RolesOfUsersReport;
import com.vtb.domain.RolesOfUsersReportDepartment;
import com.vtb.domain.RolesOfUsersReportHeader;
import com.vtb.domain.RolesOfUsersReportUser;
import com.vtb.exception.MappingException;
import com.vtb.mapping.ReportTemplateMapper;
import com.vtb.mapping.RolesOfUsersReportMapper;
import com.vtb.model.ReportBuilderActionProcessor.ReportTemplateParams;

/**
 * Builds an 'Roles Of Users'  report (user_by_role.rptdesign)
 * @author Michael Kuznetsov
 */
public class RolesOfUsersReportBuilder extends AbstractReportBuilder {
 
	private final Long TEMPLATE_ID = 8L;
	
	private Long processId, departmentId, roleId;  // report parameters
	private RolesOfUsersReportMapper mapper;   // mapper for database access.
	private String[] defaultDepartments;   // list of departments for the report

	public RolesOfUsersReportBuilder(String reportName, RolesOfUsersReportMapper mapper, ReportTemplateMapper reportMapper) throws Exception {
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
		String departmentIdStr = getParameter(parameters, ReportTemplateParams.DEPARTMENT_ID.getValue());
		try {
			departmentId = (departmentIdStr != null) ? Long.parseLong(departmentIdStr.trim()) : -1L;
		} catch (NumberFormatException e) {
			departmentId = -1L;
		}
		defaultDepartments = getNumeratedParams(parameters, ReportTemplateParams.REPORT_FILTER_DEPARMENTS_STRing.getValue());
		String roleIdStr = getParameter(parameters, ReportTemplateParams.ROLE_ID.getValue());		
		try {
			roleId = (roleIdStr != null) ? Long.parseLong(roleIdStr.trim()) : -1L;
		} catch (NumberFormatException e) {
			roleId = -1L;
		}
		
		String processIdStr = getParameter(parameters, ReportTemplateParams.PROCESS_ID.getValue());
		try {
			processId = (processIdStr != null) ? Long.parseLong(processIdStr.trim()) : 1L;
		} catch (NumberFormatException e) {
			processId = 1L;
		}
	}
	
	/**
	 * {@inheritDoc}
	 */	
	@Override
	protected void getData() throws MappingException {
	}
	
	/**
	 * {@inheritDoc}
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * @throws MappingException
	 */
	@Override
	protected Element dataToXML() throws ParserConfigurationException, IllegalArgumentException, IllegalAccessException, TransformerException, MappingException {
		RolesOfUsersReport report = new RolesOfUsersReport();
		
		// getHeaders
		RolesOfUsersReportHeader headers = mapper.getHeaderData(processId, departmentId, roleId); 
		report.setHeaders(headers);

		// get departments for the chosen process
		List<RolesOfUsersReportDepartment> departments = null;
		if (departmentId.equals(-1L)) {
            // a list of the departments should be considered.
            if (departmentId.equals(-1L) && (defaultDepartments != null)) departments = mapper.getDepartmentsList(defaultDepartments);
            else departments = mapper.getDepartments(departmentId);
		} else {
            // only one department was chosen from the list.
            departments = mapper.getDepartments(departmentId);
		}
		List<RolesOfUsersReportDepartment> departmentsWithData = new ArrayList<RolesOfUsersReportDepartment>();
		for (RolesOfUsersReportDepartment department : departments) {
			// get users for the department
			List<RolesOfUsersReportUser> users = mapper.getUsersOfDepartment(department.getDepartmentId(), department.getDepartment_name());
			List<RolesOfUsersReportUser> usersWithRoles = new ArrayList<RolesOfUsersReportUser>();
			for(RolesOfUsersReportUser user : users) {
				// getRoles for the user in the process.
				List<String> roles = mapper.getRoles (processId, user.getUserId(), roleId); 
				// Adds user only when roles are found
				if (roles.size() > 0) {
					user.setRoles(roles);
					usersWithRoles.add(user);	
				}				
			}
			// Adds department only when there are users with roles for this process
			if (usersWithRoles.size() > 0) { 
			    department.setUsers(usersWithRoles);
			    departmentsWithData.add(department);
			    try {
			    // find full department name
			    CompendiumActionProcessor compenduim = 
			        (CompendiumActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("Compendium");
			    String fullDepartmentName = compenduim.findDepartmentFullPath(department.getDepartmentId().intValue(), true);
			    department.setDepartment_name(fullDepartmentName);
			    } catch (Exception e) {
			        // надо бы в лог записать
			    }
			}
		}
		// отсортируем по имени департамента.
		Collections.sort(departmentsWithData, new Comparator<RolesOfUsersReportDepartment>() {
		    public int compare(RolesOfUsersReportDepartment o1, RolesOfUsersReportDepartment o2) {
		        if (o2.getDepartment_name() == null) return -1;
		        if (o1.getDepartment_name() == null) return 1;
		        return o1.getDepartment_name().compareTo(o2.getDepartment_name());
		    }
		});
		report.setDepartments(departmentsWithData);

		// collects all data and generates XML DOM.
        return report.toXML(document, rootElement);
	}
}
