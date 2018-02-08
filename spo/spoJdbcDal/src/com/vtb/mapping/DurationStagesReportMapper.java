package com.vtb.mapping;

import java.util.List;

import com.vtb.domain.DurationStagesReport;
import com.vtb.domain.StandardPeriod;
import com.vtb.exception.MappingException;

/**
 * DurationStagesReportMapper
 * @author Michael Kuznetsov 
 */
public interface DurationStagesReportMapper extends com.vtb.mapping.Mapper<DurationStagesReport> {

//	/**
//	 * Retrieve a header for the report.
//	 * @param mdtaskNumber
//	 * @param operationStatus
//	 * @param isDelinquency
//	 * @return JournalOfOperationsReportHeader value objects.
//	 * @throws MappingException
//	 */
//	DurationStagesReportHeader getHeaderData(String mdtaskNumber, Long operationStatus,	String isDelinquency) 
//				throws MappingException;

	/**
	 * Retrieve a list of operations
	 * @param mdtaskNumber
	 * @throws MappingException
	 */
	List<StandardPeriod> getReportData(String mdtaskNumber) throws MappingException;
}