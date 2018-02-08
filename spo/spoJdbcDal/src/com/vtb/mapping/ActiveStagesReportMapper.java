package com.vtb.mapping;

import java.util.List;

import com.vtb.domain.ActiveStagesReportHeader;
import com.vtb.domain.ActiveStagesReport;
import com.vtb.domain.ActiveStagesReportOperation;
import com.vtb.exception.MappingException;

/**
 * Active Stages ReportMapper
 * @author Michael Kuznetsov 
 */
public interface ActiveStagesReportMapper extends Mapper<ActiveStagesReport>{
	
	
	/**
	 * Retrieve a list of report rows data. 
	 * @return list of ActiveStagesReport value objects.
	 * @throws MappingException
	 */
	List<ActiveStagesReportOperation> getReportData(Long idTypeProcess, String idClaim, Long idDepartment, String correspondingDeps, 
			Long idUser, Long isDelinquency, Long mdtaskId) throws MappingException;

	/**
	 * Retrieve a list of report header data. 
	 * @return report header data.
	 * @throws MappingException
	 */
	public ActiveStagesReportHeader getHeaderData(Long p_idTypeProcess, String p_idClaim, Long p_idDepartment, String correspondingDeps, 
			Long p_idUser, Long isDelinquency, Long mdtaskId) throws MappingException;

	/**
	 * Retrieve a list of available users for the operation. 
	 * @param operation
	 * @return list of String.
	 * @throws MappingException
	 */
	List<String> getAvailableUsers(ActiveStagesReportOperation operation) throws MappingException;
	
	/**
	 * Retrieve a list of available users for the operation. 
	 * @param processId process Id
	 * @param statusId status Id
	 * @param stageId stage Id
	 * @return list of String.
	 * @throws MappingException
	 */
	List<String> getAssignedUsers(Long processId, Long statusId, Long stageId) throws MappingException;
	
	/**
	 * Helper method. Retrieve a process type id from claimId. 
	 * @param claimId claim Id
	 * @return Process Type Id.
	 * @throws MappingException
	 */
	Long getProcessTypeId(String claimId) throws MappingException;

	/**
	 * Helper method. Retrieve a process type id from mdtaskId. 
	 * @param mdtaskId mdtask Id
	 * @return Process Type Id.
	 * @throws MappingException
	 */
	Long getProcessTypeIdByMdtaskId(Long mdtaskId) throws MappingException;

}