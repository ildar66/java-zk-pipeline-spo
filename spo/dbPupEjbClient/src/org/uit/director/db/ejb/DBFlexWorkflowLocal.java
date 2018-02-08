package org.uit.director.db.ejb;

import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import javax.naming.NamingException;

import org.uit.director.db.dbobjects.ProcessControlType;
import org.uit.director.db.dbobjects.ProcessPacketBean;
import org.uit.director.db.dbobjects.WorkflowAssignInfo;
import org.uit.director.db.dbobjects.WorkflowMessage;
import org.uit.director.db.dbobjects.WorkflowProcessInfo;
import org.uit.director.db.dbobjects.WorkflowUser;

import com.vtb.domain.ProcessSearchParam;
import com.vtb.domain.Task;
import com.vtb.domain.WorkflowTaskInfo;
import com.vtb.exception.MappingException;
import com.vtb.exception.NoSuchOrganizationException;

/**
 * Local interface for Enterprise Bean: DBFlexWorkflow
 */
public interface DBFlexWorkflowLocal extends javax.ejb.EJBLocalObject,DBFlexWorkflowCommon {

	/**
	 * пїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ
	 */
	public Long createTask(Task task, Long taskid,
			Long idUser, Integer idTypeProcess,
			Long idStageBegin, ArrayList<Object[]> param,ProcessControlType controlType)
			throws RemoteException, NoSuchOrganizationException,
			MappingException;
	/**
	 * @param id_task
	 * @param idUser
	 * @param sign
	 * @return
	 */
	public String acceptWork(long id_task, Long idUser, String sign);

	/**
	 * @param params
	 * @return
	 */
	public String acceptWorks(ArrayList params);

	/**
	 * @param params
	 * @param idUser
	 * @param ipAddress
	 */
	public void acceptWorksControl(ArrayList params, Long idUser, String ipAddress);

	/**
	 * @param nameTask
	 * @param parameters
	 * @param className
	 * @param status
	 * @param message
	 * @return
	 */
	public String addSchedulerInfo(String nameTask, ArrayList parameters, String className, String status, String message);

	/**
	 * @param idTypeProcess
	 * @param nameReport
	 * @param classReport
	 * @param descriptionReport
	 */
	public void addSpetialReport(String idTypeProcess, String nameReport, String classReport, String descriptionReport);

	/**
	 * @param idUser
	 * @param idRole
	 * @param remoteUser
	 * @param remoteHost
	 * @return
	 */
	public int addWorkflowRole(Long idUser, String idRole, Long remoteUser, String remoteHost);

	/**
	 * @param idUser
	 * @param addRoleUser
	 * @param user
	 * @param ip
	 * @return
	 */
	public String addWorkflowUser(Long idUser, String addRoleUser, Long user, String ip);

	/**
	 * @param idUser
	 * @param ipAddress
	 * @param param
	 * @return
	 */
	public String[] assignToUser(Long idUser, String ipAddress, ArrayList<Object[]> param);

	/**
	 * @param idTask
	 * @param idStageToGo
	 * @return
	 */
	public String backWorkToStage(long idTask, long idStageToGo);

	/**
	 * @param params
	 * @param nameProcedure
	 * @param fields
	 * @return
	 * @throws SQLException
	 */
	public ArrayList<HashMap<String, Object>> callQuery(ArrayList<HashMap<String, Object>> params, String nameProcedure, String[] fields) throws SQLException;

	/**
	 * @param params
	 * @return
	 */
	public ArrayList<Integer> completeWorks(ArrayList params);

	/**
	 * @param id_type_process
	 * @param idParProcess
	 * @param idUser
	 * @return
	 */
	public Long createProcess(Integer id_type_process, Long idParProcess, Long idUser);

	/**
	 * @param idTypeProcess
	 * @param idParProcess
	 * @param user
	 * @param params
	 * @param controlType
	 * @return
	 */
	public Long createProcessAndSetAttributes(Integer idTypeProcess, Long idParProcess, Long user, ArrayList<Object[]> params, ProcessControlType controlType);

	/**
	 * @param idAssign
	 * @param idUser
	 * @param ipAddress
	 * @param sign
	 * @return
	 */
	public String deleteAssign(Long idAssign, Long idUser, String ipAddress, String sign);

	/**
	 * @param id_process
	 * @param attributName
	 * @throws Exception
	 */
	public void deleteAttributeByName(long id_process, String attributName) ;

	/**
	 * @param idMessageToDelete
	 * @return
	 */
	public String deleteMessage(String idMessageToDelete);

	/**
	 * @param idProcess
	 * @param idUser
	 * @param ipAddress
	 * @param sign
	 * @return
	 */
	public String deleteProcess(Long idProcess, Long idUser, String ipAddress, String sign);

	/**
	 * @param idTypeProcess
	 * @return
	 */
	public String deleteSchema(String idTypeProcess);

	/**
	 * @param idUser
	 * @param idRole
	 * @param remoteUser
	 * @param remoteHost
	 * @return
	 */
	public int deleteWorkflowRole(Long idUser, String idRole, Long remoteUser, String remoteHost);

	/**
	 * @param idUser
	 * @param idRoleList
	 * @param remoteUser
	 * @param remoteHost
	 * @return
	 */
	public int deleteWorkflowUser(Long idUser, ArrayList<Long> idRoleList, Long remoteUser, String remoteHost);

	/**
	 * @param idUser
	 * @param remoteUser
	 * @param remoteHost
	 * @return
	 */
	public int deleteWorkflowUser(Long idUser, Long remoteUser, String remoteHost);

	/**
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	public ArrayList execQuery(String sql) throws SQLException;

	/**
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	public int execUpdate(String sql) throws SQLException;

	/**
	 * @param idAssign
	 * @return
	 */
	public WorkflowAssignInfo getAssignInfo(Long idAssign);

	/**
	 * @param id_user
	 * @return
	 */
	public ArrayList<Long> getAssignToUsersTasksList(Long id_user);

	/**
	 * @param params
	 * @return
	 */
	public HashMap<Long, ArrayList<String>> getAttributes(ArrayList<Object[]> params);

	/**
	 * @param idProcess
	 * @return
	 */
	public ArrayList<WorkflowMessage> getComments(long idProcess);

	/**
	 * @param idUserTo
	 * @return
	 */
	public ArrayList getMessages(Long idUserTo);

	/**
	 * @param idUserParam
	 * @return
	 */
	public ArrayList getNotifyData(Long idUserParam);

	/**
	 * @param idProcess
	 * @return
	 */
	public WorkflowProcessInfo getProcessInfo(Long idProcess);

	/**
	 * @param idTask
	 * @param take1_Complete0
	 * @return
	 */
	public String getSignum(long idTask, boolean take1_Complete0);

	/**
	 * @return
	 */
	public HashMap getStaticWorkflowData();

	/**
	 * @param id_task
	 * @return
	 */
	public WorkflowTaskInfo getTaskInfo(long id_task);

	/**
	 * @return
	 */
	public HashMap<Long, WorkflowUser> getUsersInfo();

	/**
	 * @param idStage
	 * @return
	 */
	public ArrayList getUsersInStage(long idStage);

	/**
	 * @return
	 */
	public int getVersionDB();

	/**
	 * @param id_user
	 * @param typeList
	 * @param isOwnDep
	 * @param filterNameProc
	 * @param filterNameStage
	 * @param filterAtribute
	 * @return
	 */
	public ArrayList<Long> getWorkList(Long id_user, Integer typeList, ProcessSearchParam processSearchParam);

	/**
	 * @param idTask
	 * @return
	 */
	public String goBackWork(long idTask);

	/**
	 * @param properties
	 * @throws javax.ejb.CreateException
	 */
	public void initProprties(Properties properties) throws javax.ejb.CreateException;

	/**
	 * @param packetBean
	 * @return
	 * @throws SQLException
	 */
	public Object[] loadProcessPacket(ProcessPacketBean packetBean) throws SQLException;

	/**
	 * @param paramsForLoadInVars
	 * @param paramsForLoadOutVars
	 * @param paramsForLoadMapRoles
	 * @param paramForLoadSupProcesses
	 * @param idTransaction
	 * @return
	 * @throws SQLException
	 * @throws IOException
	 */
	public String loadSubProcessData(ArrayList<Object[]> paramsForLoadInVars, ArrayList<Object[]> paramsForLoadOutVars, ArrayList<Object[]> paramsForLoadMapRoles, Object[] paramForLoadSupProcesses, Long idTransaction) throws SQLException, IOException;

	/**
	 * @param idUserIsp
	 * @param mayReassign
	 * @param idAssign
	 * @param idUser
	 * @param ipAddress
	 * @param signature
	 * @return
	 */
	public String reassignUser(Long idUserIsp, Integer mayReassign, Long idAssign, Long idUser, String ipAddress, String signature);

	/**
	 * @param idTask
	 * @param idStageRedirect
	 * @param idUser
	 * @param ipAddress
	 * @return
	 */
	public String redirectWork(String idTask, String idStageRedirect, Long idUser,
			Long depart, String ipAddress);

	/**
	 * @param idUserFrom
	 * @param idUserTo
	 * @param idTypeProcess
	 * @param idUser
	 * @param ipAddress
	 * @return
	 */
	public String redirectWorks(Long idUserFrom, Long idUserTo, String idTypeProcess, Long idUser, String ipAddress);

	/**
	 * @param idTask
	 * @return
	 * @throws SQLException
	 */
	public String refuseProcess(String idTask) throws SQLException;

	/**
	 * @param id_task
	 * @param idUser
	 */
	public void refuseWork(long id_task, Long idUser);

	/**
	 * @param idProcess
	 * @param idStageFrom
	 * @param userFrom
	 * @param comment
	 */
	public void setComment(long idProcess, long idStageFrom, Long userFrom, String comment);

	/**
	 * @param attrs
	 * @param varData
	 * @throws SQLException
	 * @throws NamingException
	 */
	public void setExternalResourcesData(HashMap<Long, ArrayList<String>> attrs, HashMap<Long, Object[]> varData) throws SQLException, NamingException;

	/**
	 * @param idStageFrom
	 * @param idStageTo
	 * @param userName
	 * @param message
	 */
	public void setMessage(int idStageFrom, int idStageTo, Long userName, String message);

	/**
	 * @param idProcess
	 * @param nameVar
	 * @param valueVar
	 * @return
	 */
	public String updateAttribute(long idProcess, String nameVar, String valueVar);

	/**
	 * @param param
	 * @param controlType
	 * @return
	 */
	public String updateAttributes(ArrayList<Object[]> param, ProcessControlType controlType);

	/**
	 * @param param
	 * @param remoteUser
	 * @param remoteHost
	 * @param nameAction
	 * @param processControlType
	 * @return
	 */
	public String updateAttributesControl(ArrayList<Object[]> param, Long remoteUser, String remoteHost, String nameAction, ProcessControlType processControlType);

	/**
	 * @param idUser
	 * @param mail
	 * @param ip
	 * @param remoteUser
	 * @param remoteHost
	 * @return
	 */
	public int addUserNotify(Long idUser, String mail, String ip, Long remoteUser, String remoteHost);
	}
