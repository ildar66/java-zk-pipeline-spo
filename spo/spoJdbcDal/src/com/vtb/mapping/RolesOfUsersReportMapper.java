package com.vtb.mapping;

import java.util.List;

import com.vtb.domain.RolesOfUsersReport;
import com.vtb.domain.RolesOfUsersReportDepartment;
import com.vtb.domain.RolesOfUsersReportHeader;
import com.vtb.domain.RolesOfUsersReportUser;
import com.vtb.exception.MappingException;

/**
 * RolesOfUsersReport
 * @author Michael Kuznetsov 
 */
public interface RolesOfUsersReportMapper extends com.vtb.mapping.Mapper<RolesOfUsersReport> {

	/**
	 * Retrieve a header for the report.
	 * @param processId
	 * @param processId
	 * @param roleId
	 * @return RolesOfUsersReportHeader value object
	 * @throws MappingException
	 */
	RolesOfUsersReportHeader getHeaderData(Long processId, Long departmentId, Long roleId) throws MappingException;

	/**
	 * Retrive a list of departments for the report
	 * @param departmentId
	 * @return departments list
	 */
	List<RolesOfUsersReportDepartment> getDepartments(Long departmentId) throws MappingException;

   /**
     * Retrive a list of departments for the report (without a -1 department ('All departments'))
     * @param departmentsList list of departments
     * @return departments list
     */
    List<RolesOfUsersReportDepartment> getDepartmentsList(String[] departmentsList) throws MappingException;

	/**
	 * Retrieve a list of department users.
	 * @param departmentId
	 * @param departmentName
	 * @return list of users of a department
	 * @throws MappingException
	 */
	List<RolesOfUsersReportUser> getUsersOfDepartment(Long departmentId, String departmentName) 
				throws MappingException;

	/**
	 * Retrieve a list of roles for the chosen user and chosen process.
	 * @param processId
	 * @param userId
	 * @param roleId if we look for a definite role.
	 * @return List<String> list of roles
	 * @throws MappingException
	 */
	List<String> getRoles(Long processId, Long userId, Long roleId) throws MappingException;		
}