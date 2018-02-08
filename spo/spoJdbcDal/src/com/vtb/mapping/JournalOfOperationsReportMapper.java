package com.vtb.mapping;

import java.util.List;

import com.vtb.domain.JournalOfOperationsReport;
import com.vtb.domain.JournalOfOperationsReportHeader;
import com.vtb.domain.JournalOfOperationsReportOperation;
import com.vtb.exception.MappingException;

/**
 * JournalOfOperationsMapper
 * @author Michael Kuznetsov 
 */
public interface JournalOfOperationsReportMapper extends com.vtb.mapping.Mapper<JournalOfOperationsReport> {

	/**
	 * Retrieve a header for the report.
	 * @param mdtaskNumber
	 * @param operationStatus
	 * @param isDelinquency
	 * @return JournalOfOperationsReportHeader value objects.
	 * @throws MappingException
	 */
	JournalOfOperationsReportHeader getHeaderData(String mdtaskNumber, Long operationStatus,	String isDelinquency) 
				throws MappingException;

	/**
	 * Retrieve a list of mdtasks for the report.
	 * @param mdtaskNumber
	 * @param operationStatus
	 * @param isDelinquency
	 * @return JournalOfOperationsReportHeader value objects.
	 * @throws MappingException
	 */
	List<JournalOfOperationsReportOperation> getReportData(String mdtaskNumber, Long operationStatus, String isDelinquency) 
				throws MappingException;
}