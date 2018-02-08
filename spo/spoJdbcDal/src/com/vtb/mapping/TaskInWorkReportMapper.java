package com.vtb.mapping;

import com.vtb.domain.TaskInWorkReport;
import com.vtb.domain.TaskInWorkReportRecord;
import com.vtb.exception.MappingException;

/**
 * TaskInWorkReportMapper
 * @author Michael Kuznetsov 
 */
public interface TaskInWorkReportMapper extends com.vtb.mapping.Mapper<TaskInWorkReport> {

	/**
	 * Retrieve a record for the report.
	 * @param taskId
	 * @return NewTaskReportRecord value objects.
	 * @throws MappingException
	 */
	TaskInWorkReportRecord getRecord (Long taskId)  throws MappingException;
}