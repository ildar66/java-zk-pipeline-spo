package com.vtb.mapping;

import com.vtb.domain.NewTaskReport;
import com.vtb.domain.NewTaskReportRecord;
import com.vtb.exception.MappingException;

/**
 * NewTaskReportMapper
 * @author Michael Kuznetsov 
 */
public interface NewTaskReportMapper extends com.vtb.mapping.Mapper<NewTaskReport> {
	
	/**
	 * Retrieve a record for the report.
	 * @param taskId
	 * @return NewTaskReportRecord value objects.
	 * @throws MappingException
	 */
	NewTaskReportRecord getRecord (Long taskId)  throws MappingException;
}