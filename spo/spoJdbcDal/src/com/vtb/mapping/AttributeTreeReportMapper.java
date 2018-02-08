package com.vtb.mapping;

import java.util.List;

import com.vtb.domain.AttributeTreeReport;
import com.vtb.domain.AttributeTreeReportHeader;
import com.vtb.domain.AttributeTreeReportOperation;
import com.vtb.exception.MappingException;

/**
 * AttributeTreeMapper
 * @author Michael Kuznetsov 
 */
public interface AttributeTreeReportMapper extends com.vtb.mapping.Mapper<AttributeTreeReport> {

	/**
	 * Retrieve a header for the report.
	 * @param processId
	 * @return AttributeTreeReportHeader value objects.
	 * @throws MappingException
	 */
	AttributeTreeReportHeader getHeaderData(Long processId) throws MappingException;

	/**
	 * Retrieve a list of operations for the report.
	 * @param processId
	 * @return AttributeTreeReportOperation value objects.
	 * @throws MappingException
	 */
	List<AttributeTreeReportOperation> getOperations(Long processId) throws MappingException;
}