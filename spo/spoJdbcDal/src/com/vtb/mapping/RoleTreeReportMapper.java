package com.vtb.mapping;

import java.util.List;

import com.vtb.domain.RoleTreeReport;
import com.vtb.domain.RoleTreeReportDepartment;
import com.vtb.domain.RoleTreeReportOperation;
import com.vtb.domain.RoleTreeReportHeader;
import com.vtb.exception.MappingException;

/**
 * RoleTreeReport
 * @author Michael Kuznetsov 
 */
public interface RoleTreeReportMapper extends com.vtb.mapping.Mapper<RoleTreeReport> {

	/**
	 * Retrieve a header for the report.
	 * @param processId
	 * @param departmentId
	 * @param showUnactive
	 * @return RoleTreeReportHeader value object
	 * @throws MappingException
	 */
	RoleTreeReportHeader getHeaderData(Long processId, Long departmentId, String showUnactive)
	throws MappingException;

	/**
	 * Retrive a list of operations for the report
	 * @param processId
	 * @param showUnactive
	 * @return RoleTreeReportOperation operations list
	 */
	List<RoleTreeReportOperation> getOperations(Long processId, String showUnactive) throws MappingException;

	/**
	 * Rertrieve list of users of a department for the chosen role.
	 * @param roleId
	 * @param departmentId
	 * @return List<String> list of users of a department for the chosen role
	 * @throws MappingException
	 */
	List<String> getUsersForRoles(Long roleId, Long departmentId) throws MappingException;

    /**
     * Retrive a list of departments for the report
     * @param departmentId
     * @return departments list
     */
	List<RoleTreeReportDepartment> getDepartments(Long departmentId) throws MappingException;
	
	/**
     * Retrive a list of departments for the report (without a -1 department ('All departments'))
     * @param departmentsList list of departments
     * @return departments list
     */
	List<RoleTreeReportDepartment> getDepartmentsList(String[] departmentsList) throws MappingException;
}