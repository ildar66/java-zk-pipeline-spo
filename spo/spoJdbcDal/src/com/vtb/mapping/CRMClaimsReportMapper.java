package com.vtb.mapping;

import java.util.List;

import com.vtb.domain.CRMClaimsReport;
import com.vtb.domain.CRMClaimsReportHeader;
import com.vtb.domain.CRMClaimsReportOperation;
import com.vtb.exception.MappingException;

/**
 * CRMClaimsMapper
 * @author Michael Kuznetsov 
 */
public interface CRMClaimsReportMapper extends com.vtb.mapping.Mapper<CRMClaimsReport> {

	/**
	 * Retrieve a list of operations for the report.
	 * @param CRMClaimsReportHeader parameters 
	 * @return CRMClaimsReportOperation value objects.
	 * @throws MappingException
	 */
	List<CRMClaimsReportOperation> getOperations(CRMClaimsReportHeader parameters) throws MappingException;
}