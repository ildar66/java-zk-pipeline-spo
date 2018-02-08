package org.uit.director.db.ejb;

import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import org.uit.director.db.dbobjects.ProcessControlType;

import ru.masterdm.compendium.exception.ModelException;

import com.vtb.domain.ProcessSearchParam;
import com.vtb.domain.Task;
import com.vtb.domain.WorkflowTaskInfo;
import com.vtb.exception.MappingException;
import com.vtb.exception.NoSuchOrganizationException;

public abstract interface DBFlexWorkflowCommon {
	
	/**
	 * пїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ
	 */
	public Long createTask(Task task, Long taskid,
			Long idUser, Integer idTypeProcess,
			Long idStageBegin, ArrayList<Object[]> param,ProcessControlType controlType)
			throws RemoteException, NoSuchOrganizationException,
			MappingException;
	/**
	 * acceptWork
	 */
	public java.lang.String acceptWork(
		long id_task,
		java.lang.Long idUser,
		java.lang.String sign) throws java.rmi.RemoteException;

	/**
	 * acceptWorks
	 */
	public java.lang.String acceptWorks(ArrayList params)
		throws java.rmi.RemoteException;

	/**
	 * goBackWork
	 */
	public java.lang.String goBackWork(long idTask)
		throws java.rmi.RemoteException;

	/**
	 * updateAttribute
	 */
	public java.lang.String updateAttribute(
		long idProcess,
		java.lang.String nameVar,
		java.lang.String valueVar) throws java.rmi.RemoteException;

	/**
	 * getStaticWorkflowData
	 */
	public HashMap getStaticWorkflowData()
		throws java.rmi.RemoteException;

	/**
	 * getTaskInfo
	 */
	public WorkflowTaskInfo getTaskInfo(
		long id_task) throws java.rmi.RemoteException;

	/**
	 * updateAttributes
	 */
	public java.lang.String updateAttributes(
		ArrayList<Object[]> param,
		org.uit.director.db.dbobjects.ProcessControlType controlType)
		throws java.rmi.RemoteException;

	/**
	 * getComments
	 */
	public ArrayList getComments(long idProcess)
		throws java.rmi.RemoteException;

	/**
	 * completeWorks
	 */
	public ArrayList completeWorks(ArrayList params)
		throws java.rmi.RemoteException;

	/**
	 * execQuery
	 */
	public ArrayList execQuery(java.lang.String sql)
		throws java.sql.SQLException,
		java.rmi.RemoteException;

	/**
	 * execUpdate
	 */
	public int execUpdate(java.lang.String sql)
		throws java.sql.SQLException,
		java.rmi.RemoteException;

	/**
	 * deleteMessage
	 */
	public java.lang.String deleteMessage(java.lang.String idMessageToDelete)
		throws java.rmi.RemoteException;

	/**
	 * deleteAttributeByName
	 */
	public void deleteAttributeByName(
		long id_process,
		java.lang.String attributName) throws java.rmi.RemoteException;

	/**
	 * refuseProcess
	 */
	public java.lang.String refuseProcess(java.lang.String idTask)
		throws java.sql.SQLException,
		java.rmi.RemoteException;

	/**
	 * initProprties
	 */
	public void initProprties(Properties properties)
		throws javax.ejb.CreateException,
		java.rmi.RemoteException;

	/**
	 * loadProcessPacket
	 */
	public Object[] loadProcessPacket(
		org.uit.director.db.dbobjects.ProcessPacketBean packetBean)
		throws java.sql.SQLException,
		java.rmi.RemoteException;

	/**
	 * addSpetialReport
	 */
	public void addSpetialReport(
		java.lang.String idTypeProcess,
		java.lang.String nameReport,
		java.lang.String classReport,
		java.lang.String descriptionReport) throws java.rmi.RemoteException;

	/**
	 * getSignum
	 */
	public java.lang.String getSignum(long idTask, boolean take1_Complete0)
		throws java.rmi.RemoteException;

	/**
	 * getAttributes
	 */
	public HashMap<Long, ArrayList<String>> getAttributes(ArrayList<Object[]> params)
		throws java.rmi.RemoteException;

	/**
	 * deleteSchema
	 */
	public java.lang.String deleteSchema(java.lang.String idTypeProcess)
		throws java.rmi.RemoteException;

	/**
	 * addSchedulerInfo
	 */
	public java.lang.String addSchedulerInfo(
		java.lang.String nameTask,
		ArrayList parameters,
		java.lang.String className,
		java.lang.String status,
		java.lang.String message) throws java.rmi.RemoteException;

	/**
	 * loadSubProcessData
	 */
	public java.lang.String loadSubProcessData(
		ArrayList<Object[]> paramsForLoadInVars,
		ArrayList<Object[]> paramsForLoadOutVars,
		ArrayList<Object[]> paramsForLoadMapRoles,
		java.lang.Object[] par,
		java.lang.Long idTransaction)
		throws java.sql.SQLException,java.io.IOException,
		java.rmi.RemoteException;

	/**
	 * getProcessInfo
	 */
	public org.uit.director.db.dbobjects.WorkflowProcessInfo getProcessInfo(java.lang.Long long1) throws java.rmi.RemoteException;

	/**
	 * getUsersInStage
	 */
	public ArrayList getUsersInStage(long idStage)
		throws java.rmi.RemoteException;

	/**
	 * callQuery
	 */
	public ArrayList<HashMap<String, Object>> callQuery(
		ArrayList<HashMap<String, Object>> params,
		java.lang.String nameProcedure,
		java.lang.String[] fields) throws java.sql.SQLException,java.rmi.RemoteException;

	/**
	 * backWorkToStage
	 */
	public java.lang.String backWorkToStage(long idTask, long idStage)
		throws java.rmi.RemoteException;

	/**
	 * getVersionDB
	 */
	public int getVersionDB() throws java.rmi.RemoteException;

	/**
	 * setExternalResourcesData
	 */
	public void setExternalResourcesData(
		HashMap<Long, ArrayList<String>> attrs,
		HashMap<Long, Object[]> varData)
		throws java.sql.SQLException,
		javax.naming.NamingException,
		java.rmi.RemoteException;

	/**
	 * getAssignInfo
	 */
	public org.uit.director.db.dbobjects.WorkflowAssignInfo getAssignInfo(
		java.lang.Long idAssign) throws java.rmi.RemoteException;

	/**
	 * getUsersInfo
	 */
	public HashMap getUsersInfo() throws java.rmi.RemoteException;

	/**
	 * acceptWorksControl
	 */
	public void acceptWorksControl(
		ArrayList params,
		java.lang.Long idUser,
		java.lang.String ipAddress) throws java.rmi.RemoteException;

	/**
	 * assignToUser
	 */
	public java.lang.String[] assignToUser(
		java.lang.Long idUser,
		java.lang.String ipAddress,
		ArrayList<Object[]> param) throws java.rmi.RemoteException;

	/**
	 * addWorkflowRole
	 */
	public int addWorkflowRole(
		java.lang.Long idUser,
		java.lang.String idRole,
		java.lang.Long remoteUser,
		java.lang.String remoteHost) throws java.rmi.RemoteException;

	/**
	 * addWorkflowUser
	 */
	public java.lang.String addWorkflowUser(
		java.lang.Long idUser,
		java.lang.String addRoleUser,
		java.lang.Long user,
		java.lang.String ip) throws java.rmi.RemoteException;

	/**
	 * deleteAssign
	 */
	public java.lang.String deleteAssign(
		java.lang.Long idAssign,
		java.lang.Long idUser,
		java.lang.String ipAddress, String sign) throws java.rmi.RemoteException;

	/**
	 * deleteProcess
	 */
	public java.lang.String deleteProcess(
		Long idProcess,
		java.lang.Long idUser,
		java.lang.String ipAddress, String sign) throws java.rmi.RemoteException;

	/**
	 * deleteWorkflowUser
	 */
	public int deleteWorkflowUser(
		java.lang.Long idUser,
		java.lang.Long remoteUser,
		java.lang.String remoteHost) throws java.rmi.RemoteException;

	/**
	 * deleteWorkflowRole
	 */
	public int deleteWorkflowRole(
		java.lang.Long idUser,
		java.lang.String idRole,
		java.lang.Long remoteUser,
		java.lang.String remoteHost) throws java.rmi.RemoteException;

	/**
	 * redirectWork
	 * @param depart 
	 */
	public java.lang.String redirectWork(
		java.lang.String idTask,
		java.lang.String idStageRedirect,
		java.lang.Long idUser,
		Long depart, java.lang.String ipAddress) throws java.rmi.RemoteException;

	/**
	 * updateAttributesControl
	 */
	public java.lang.String updateAttributesControl(
		ArrayList<Object[]> param,
		java.lang.Long remoteUser,
		java.lang.String remoteHost,
		java.lang.String nameAction,
		org.uit.director.db.dbobjects.ProcessControlType processControlType)
		throws java.rmi.RemoteException;

	/**
	 * redirectWorks
	 */
	public java.lang.String redirectWorks(
		java.lang.Long idUserFrom,
		java.lang.Long idUserTo,
		java.lang.String idTypeProcess,
		java.lang.Long idUser,
		java.lang.String ipAddress) throws java.rmi.RemoteException;

	/**
	 * getAssignToUsersTasksList
	 */
	public ArrayList getAssignToUsersTasksList(java.lang.Long id_user)
		throws java.rmi.RemoteException;

	/**
	 * getWorkList
	 */
	public ArrayList getWorkList(
		java.lang.Long id_user,
		java.lang.Integer typeList, ProcessSearchParam processSearchParam) throws java.rmi.RemoteException;

	/**
	 * setComment
	 */
	public void setComment(
		long idProcess,
		long idStageFrom,
		java.lang.Long userFrom,
		java.lang.String comment) throws java.rmi.RemoteException;

	/**
	 * setMessage
	 */
	public void setMessage(
		int idStageFrom,
		int idStageTo,
		java.lang.Long userName,
		java.lang.String message) throws java.rmi.RemoteException;

	/**
	 * getMessages
	 */
	public ArrayList getMessages(java.lang.Long idUserTo)
		throws java.rmi.RemoteException;

	/**
	 * getNotifyData
	 */
	public ArrayList getNotifyData(java.lang.Long idUserParam)
		throws java.rmi.RemoteException;

	/**
	 * refuseWork
	 */
	public void refuseWork(long id_task, java.lang.Long idUser)
		throws java.rmi.RemoteException;

	/**
	 * createProcess
	 */
	public java.lang.Long createProcess(
		java.lang.Integer id_type_process,
		java.lang.Long idParProcess,
		java.lang.Long idUser) throws java.rmi.RemoteException;

	/**
	 * createProcessAndSetAttributes
	 */
	public java.lang.Long createProcessAndSetAttributes(
		java.lang.Integer idTypeProcess,
		java.lang.Long idParProcess,
		java.lang.Long user,
		ArrayList<Object[]> params,
		org.uit.director.db.dbobjects.ProcessControlType controlType)
		throws java.rmi.RemoteException;

	/**
	 * addUserNotify
	 */
	public int addUserNotify(
		java.lang.Long idUser,
		java.lang.String mail,
		java.lang.String ip,
		java.lang.Long remoteUser,
		java.lang.String remoteHost) throws java.rmi.RemoteException;

	/**
	 * deleteWorkflowUser
	 */
	public int deleteWorkflowUser(
		java.lang.Long idUser,
		ArrayList<Long> idRoleList,
		java.lang.Long remoteUser,
		java.lang.String remoteHost) throws java.rmi.RemoteException;

	/**
	 * reassignUser
	 */
	public java.lang.String reassignUser(
		java.lang.Long idUserIsp,
		java.lang.Integer mayReassign,
		java.lang.Long idAssign,
		java.lang.Long idUser,
		java.lang.String ipAddress,
		java.lang.String signature) throws java.rmi.RemoteException;	

	public Map<String, String> getAttributesByProcess(long processId) throws SQLException, java.rmi.RemoteException;
	public ArrayList<Long> getSubordinateUsersInDepartment(Long idUserBoss) throws java.rmi.RemoteException;
	
	/**
	 * Returns mdTask CRM Number, if present
	 * @param mdTaskNumber
	 * @return mdTask CRM Number, null if error took place.
	 * @throws java.rmi.RemoteException
	 */
	public String findCRMClaimName(String mdTaskNumber) throws java.rmi.RemoteException;

    /**
     * Returns TransitionDepartments by stage ids and process type id. If not found, returns null.
     * Process type id is required (for efficiency reasons)
     * @param ProcessTypeId process type identifier
     * @param stageFromId stage from which we make transition
     * @param stageToId stage to which we make transition
     * @param depFromId department from which to make transition
     * @throws ModelException
     */
	public LinkedHashMap<Long, String> findDepartmentsForTransition(Long stageFromId, Long stageToId, Long depFromId) throws java.rmi.RemoteException;
	
    /**
     * Find a map of departments, where task has been processed, ordered by name  
     * @param idProcess
     * @param stageToId
     */
	public LinkedHashMap<Long, String> getVisitedDepartments(Long idProcess, Long stageToId) throws java.rmi.RemoteException;
}
