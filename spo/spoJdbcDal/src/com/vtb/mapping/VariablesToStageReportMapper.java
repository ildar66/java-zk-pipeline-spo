package com.vtb.mapping;

import java.util.List;

import com.vtb.domain.VariablesToStageReport;
import com.vtb.domain.VariablesToStageReportHeader;
import com.vtb.domain.VariablesToStageReportStage;
import com.vtb.exception.MappingException;

/**
 * VariablesToStageReport
 * @author Michael Kuznetsov 
 */
public interface VariablesToStageReportMapper extends com.vtb.mapping.Mapper<VariablesToStageReport> {

	/**
	 * Retrieve a header for the report.
	 * @param processId
	 * @return VariablesToStageReportHeader value objects.
	 * @throws MappingException
	 */
	VariablesToStageReportHeader getHeaderData(Long processId) throws MappingException;

	/**
	 * Retrieve a list of mdtasks for the report.
	 * @param processId
	 * @param stageId
	 * @return VariablesToStageReportStage value objects.
	 * @throws MappingException
	 */
	List<VariablesToStageReportStage> getReportData(Long processId, Long stageId)  throws MappingException;
	
	/**
	 * Retrieve a list of mdtasks for the report.
	 * @param stageId
	 * @return List<String> list of variables
	 * @throws MappingException
	 */
	List<String> getVariables(Long stageId) throws MappingException;
}