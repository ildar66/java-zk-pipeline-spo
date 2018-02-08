package com.vtb.mapping;

import java.util.List;

import com.vtb.domain.NewDocumentsByOrgsReport;
import com.vtb.domain.NewDocumentsByOrgsReportHeader;
import com.vtb.domain.NewDocumentsByOrgsReportOperation;
import com.vtb.domain.NewDocumentsByOrgsReportOrg;
import com.vtb.exception.MappingException;

/**
 * NewDocumentsByOrgsMapper
 * @author Michael Kuznetsov 
 */
public interface NewDocumentsByOrgsReportMapper extends com.vtb.mapping.Mapper<NewDocumentsByOrgsReport> {

	/**
	 * Retrieve header data (only one now).
	 * @param departmentId department id  
	 * @return department name
	 * @throws MappingException
	 */
	String getHeaderData(Long departmentId)	throws MappingException;

	/**
	 * Retrieve organizations list for the report.
	 * @param header parameters for retrieving data
	 * @param departmentId department id  
	 * @param NewDocumentsByOrgsReportHeader header parameters
	 * @return NewDocumentsByOrgsReportOrg organization list
	 * @throws MappingException
	 */
	List<NewDocumentsByOrgsReportOrg> getOrganizations(NewDocumentsByOrgsReportHeader header, Long departmentId) 
	throws MappingException;
	
	
	/**
	 * Retrieve a list of operations for the report.
	 * @param NewDocumentsByOrgsReportHeader parameters 
	 * @param departmentId
	 * @param org_name
	 * @return NewDocumentsByOrgsReportOperation operation value objects.
	 * @throws MappingException
	 */
	List<NewDocumentsByOrgsReportOperation> getOperationsForOrganization(
			NewDocumentsByOrgsReportHeader header, Long departmentId, String org_name) throws MappingException;
}