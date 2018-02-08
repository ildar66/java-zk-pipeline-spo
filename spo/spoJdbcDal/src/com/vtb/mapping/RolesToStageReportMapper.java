package com.vtb.mapping;

import java.util.List;

import com.vtb.domain.RolesToStageReport;
import com.vtb.domain.RolesToStageReportHeader;
import com.vtb.domain.RolesToStageReportStage;
import com.vtb.exception.MappingException;

/**
 * RolesToStageReport
 * @author Michael Kuznetsov 
 */
public interface RolesToStageReportMapper extends com.vtb.mapping.Mapper<RolesToStageReport> {

	/**
	 * Retrieve a header for the report.
	 * @param processId
	 * @return VariablesToStageReportHeader value objects.
	 * @throws MappingException
	 */
	RolesToStageReportHeader getHeaderData(Long processId) throws MappingException;

	/**
	 * Retrieve a list of mdtasks for the report.
	 * @param processId
	 * @param stageId
	 * @return VariablesToStageReportStage value objects.
	 * @throws MappingException
	 */
	List<RolesToStageReportStage> getReportData(Long processId, Long stageId)  throws MappingException;
	
	/**
	 * Retrieve a list of roles for the report.
	 * @param stageId
	 * @return List<String> list of variables
	 * @throws MappingException
	 */
	List<String> getRoles(Long stageId) throws MappingException;
}