package com.vtb.mapping;

import com.vtb.domain.AppointedTaskReport;
import com.vtb.domain.AppointedTaskReportRecord;
import com.vtb.exception.MappingException;

/**
 * AppointedTaskReportMapper
 * @author Michael Kuznetsov 
 */
public interface AppointedTaskReportMapper extends com.vtb.mapping.Mapper<AppointedTaskReport> {
	
	/**
	 * Retrieve a record for the report.
	 * @param taskId
	 * @return NewTaskReportRecord value objects.
	 * @throws MappingException
	 */
	AppointedTaskReportRecord getRecord (Long taskId)  throws MappingException;

}