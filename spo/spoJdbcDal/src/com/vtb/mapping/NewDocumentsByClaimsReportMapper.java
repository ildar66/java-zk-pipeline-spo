package com.vtb.mapping;

import java.util.List;

import com.vtb.domain.NewDocumentsByClaimsReport;
import com.vtb.domain.NewDocumentsByClaimsReportHeader;
import com.vtb.domain.NewDocumentsByClaimsReportOperation;
import com.vtb.domain.NewDocumentsByClaimsReportClaim;
import com.vtb.exception.MappingException;

/**
 * NewDocumentsByClaimsMapper
 * @author Michael Kuznetsov 
 */
public interface NewDocumentsByClaimsReportMapper extends com.vtb.mapping.Mapper<NewDocumentsByClaimsReport> {

	/**
	 * Retrieve header data (only one now).
	 * @param departmentId department id  
	 * @return department name
	 * @throws MappingException
	 */
	String getHeaderData(Long departmentId)	throws MappingException;

	/**
	 * Retrieve claims list for the report.
	 * @param header parameters for retrieving data
	 * @param departmentId department id  
	 * @return NewDocumentsByClaimsReportClaim claim list
	 * @throws MappingException
	 */
	List<NewDocumentsByClaimsReportClaim> getClaims(NewDocumentsByClaimsReportHeader header, Long departmentId) 
	throws MappingException;
	
	
	/**
	 * Retrieve a list of operations for the report.
	 * @param NewDocumentsByClaimsReportHeader parameters 
	 * @param departmentId
	 * @param claimId
	 * @return NewDocumentsByClaimsReportOperation operation value objects.
	 * @throws MappingException
	 */
	List<NewDocumentsByClaimsReportOperation> getOperationsForClaim(
		NewDocumentsByClaimsReportHeader header, Long departmentId, Long claimId) throws MappingException;
}