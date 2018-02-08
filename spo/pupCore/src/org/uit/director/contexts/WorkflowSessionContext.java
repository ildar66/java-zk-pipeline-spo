package org.uit.director.contexts;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import com.vtb.util.Formatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uit.director.db.dbobjects.Cnst;
import org.uit.director.db.dbobjects.WorkflowRoles;
import org.uit.director.db.dbobjects.WorkflowUser;
import org.uit.director.db.ejb.DBFlexWorkflowCommon;
import org.uit.director.managers.ControlActionsManager;
import org.uit.director.managers.DBMgr;
import org.uit.director.managers.StagesDirectionManager;
import org.uit.director.managers.TaskCacheManager;
import org.uit.director.report.WorkflowReport;
import org.uit.director.tasks.AssignTasksList;
import org.uit.director.tasks.ProcessInfo;
import org.uit.director.tasks.ProcessList;
import org.uit.director.tasks.TaskInfo;
import org.uit.director.tasks.TaskList;
import org.uit.director.threads.CacheAutoDelete;

import ru.md.pup.dbobjects.UserJPA;
import ru.md.spo.ejb.PupFacadeLocal;

import com.vtb.exception.FactoryException;
import com.vtb.exception.VtbException;

/**
 * Объект класса WorkflowSessionContext хранит состояние клиента относящееся к
 * взаимодействию с сервером потоков работ.
 */
public class WorkflowSessionContext implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(WorkflowSessionContext.class.getName());

	private Long idUser = null;

	private DBMgr dbManager;
	private TaskCacheManager cacheManager;
	private String errorMessage;
	private String warningMessage;
	private TaskList taskList;
	private String sortState = "";
	private long idCurrTask;
	private ProcessInfo currEditProcessInfo;
	private String pageData; // HTML код какой-либо информации, выводимой на странице textPage.jsp
	private ControlActionsManager controlActionsManager;;
	private CacheAutoDelete autoDelete; // фоновая нить периодического удаления старого кеша из памяти
	private WorkflowReport report;
	private StagesDirectionManager stagesDirectionManager;
	private String signum;
	private Long taskid=null;

	/**
	 * Переменная показывает является ли полученный объект вновь созданным.
	 * Данная информация необходима в случае уничтожения сессии.
	 */
	private boolean isNewContext;
	private UserTransaction userTransaction;
	private ProcessList processList;
	private AssignTasksList assignList;

	/**
	 * @param userName
	 *            login пользователя
	 * @throws VtbException 
	 */
	public WorkflowSessionContext(final String userName)  {
		UserJPA user = null;
		try {
			PupFacadeLocal pupFacade = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
			user = pupFacade.getUserByLogin(userName);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
        if (user == null) {
            String msg = "Пользователь с логином '" + userName + "' не зарегистрирован в системе";
            LOGGER.warn(msg);
            throw new RuntimeException(msg);
        }
        if (!user.isActive()) {
            String msg = "Пользователь с логином '" + userName + "' неактивен";
            LOGGER.warn(msg);
            throw new RuntimeException(msg);
        }

		dbManager = new DBMgr();
		DBFlexWorkflowCommon dbWorkflow = dbManager.getDbFlexDirector();
		String resInitWPC = WPC.init(dbWorkflow);
		if (!resInitWPC.equalsIgnoreCase("ok")) {
			setErrorMessage(resInitWPC);
			setNewContext(true);
			return;
		}
		
		idUser = WPC.getInstance().getUsersMgr().getActiveIdUserByLogin(userName);
        if (idUser == null) {
            String msg = "Пользователь " + userName + " не существует";
            LOGGER.warn(msg);
            throw new RuntimeException(msg);
        }

		cacheManager = new TaskCacheManager();
		autoDelete = new CacheAutoDelete(cacheManager);
		autoDelete.start();
		isNewContext = true;
		controlActionsManager = new ControlActionsManager();
	}

	public WorkflowSessionContext() {
		dbManager = new DBMgr();
	}

	public Long getIdUser() {
		return idUser;
	}

	public String getFullUserName() {
		return WPC.getInstance().getUsersMgr().getFullNameWorkflowUser(idUser);
	}

	public TaskCacheManager getCacheManager() {
		return cacheManager;
	}

	public void setCacheManager(TaskCacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	public DBMgr getDbManager() {
		return dbManager;
	}

	public void setErrorMessage(String s) {
		errorMessage = s;
		taskid = null;
	}
	public void setErrorMessage(String s, Long idTask) {
		errorMessage = s;
		taskid = idTask;
	}

	public String getErrorMessage() {
		if (errorMessage == null) {
			return "";
		}
		String mess = new String(errorMessage);
		errorMessage = "";
		return mess;
	}

	public void setPageTask(TaskList taskList) {
		this.taskList = taskList;
	}

	public TaskList getTaskList() {
		return taskList;
	}

	public void setNewContext(boolean newContext) {
		isNewContext = newContext;
	}

	public boolean isNewContext() {
		return isNewContext;
	}

	public long getIdCurrTask() {
		return idCurrTask;
	}

	public void setIdCurrTask(long idCurrTask) {
		this.idCurrTask = idCurrTask;
	}

	public String getPageData() {
		return pageData;
	}

	public void setPageData(String pageData) {
		this.pageData = pageData;
	}

    /**
     * Проверяет входит ли пользователь в группу администраторов.
     * @return true, если пользователь является администратором
     * @author Andrey Pavlenko
     */
	public boolean isAdmin() {
		try {
			PupFacadeLocal pup = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
			return pup.isAdmin(getIdUser());
		} catch (FactoryException e) {
			return false;
		}
	}

	public void release() throws Throwable {

		autoDelete.stopLoop();
		autoDelete = null;
		LOGGER.info("Stoping clearing cache thread...");
		LOGGER.info("Stoping checking messages thread...");

		dbManager.closeAllDb();
		dbManager = null;
		LOGGER.info("Closing all connecions...");

		cacheManager = null;
		taskList = null;
		report = null;
		stagesDirectionManager = null;
		processList = null;
		assignList = null;

	}

	public void startClearingCache() {

		// если очистка кеша была остановлена, то запускаем ее
		if (autoDelete.isFlag()) {
			autoDelete.setFlag(false);
			autoDelete.start();
			LOGGER.info("Starting clearing threads...");
		}

	}

	public WorkflowReport getReport() {
		return report;
	}

	public void setReport(WorkflowReport report) {
		this.report = report;
	}

	public StagesDirectionManager getStagesDirectionManager() {
		return stagesDirectionManager;
	}

	public void setStagesDirectionManager(
			StagesDirectionManager stagesDirectionManager) {
		this.stagesDirectionManager = stagesDirectionManager;
	}

	public CacheAutoDelete getAutoDelete() {
		return autoDelete;
	}

	public String getSortState() {
		return sortState;
	}

	public void setSortState(String sortState) {
		this.sortState = sortState;
	}

	public boolean isCorrectContext() {

		return dbManager != null && cacheManager != null && autoDelete != null;
	}

	/**
	 * @return Returns the signum.
	 */
	public String getSignum() {
		return signum;
	}

	/**
	 * @param signum
	 *            The signum to set.
	 */
	public void setSignum(String signum) {
		this.signum = signum;
	}

	/**
	 * Является ли пользователь администратором процесса
	 * 
	 * @param typeProc
	 * @return
	 */
	public boolean isUserAdmin(int typeProc) {

		if (isAdmin()) {
			return true;
		}
		List<Long> roles = WPC.getInstance().getIDRolesForUser(idUser);

		for (int i = 0; i < roles.size(); i++) {
			Long idRole = roles.get(i);
			WorkflowRoles role = WPC.getInstance().findRole(idRole);

			if (role.getIdTypeProcess() == typeProc
					&& role.getNameRole().startsWith("Администратор процесса ")) {
				return true;

			}

		}

		return false;
	}

	/**
	 * Проверить является ли пользователь администратором для операций над
	 * заданным пользователем
	 * 
	 * @param idUser
	 * @return
	 */
	public boolean isUserAdmin(Long idUserCheck) {

		if (isAdmin()) {
			return true;
		}

		Map<Integer, List<Long>> usersInTp = WPC.getInstance().getUsersInTypeProcess();

		for (Map.Entry<Integer, List<Long>> en : usersInTp.entrySet()){
			Integer idTp = en.getKey();
			List<Long> users = en.getValue();

			if (users.contains(idUserCheck)) {
				if (isUserAdmin(idTp.intValue())) {
					return true;
				}
			}

		}

		return false;
	}

	/**
	 * @return the controlActionsManager
	 */
	public ControlActionsManager getControlActionsManager() {
		return controlActionsManager;
	}

	/**
	 * @param controlActionsManager
	 *            the controlActionsManager to set
	 */
	public void setControlActionsManager(
			ControlActionsManager controlActionsManager) {
		this.controlActionsManager = controlActionsManager;
	}

	public UserTransaction getUserTransaction() {

		InitialContext initialContext;
		try {
			if (userTransaction == null) {
				initialContext = new InitialContext();
				userTransaction = (UserTransaction) initialContext
						.lookup("java:comp/UserTransaction");
			}
		} catch (NamingException e) {
			e.printStackTrace();
		}

		return userTransaction;
	}

	/**
	 * @return the warningMessage
	 */
	public String getWarningMessage() {

		if (warningMessage == null) {
			return "";
		}
		String mess = new String(warningMessage);
		warningMessage = "";
		return mess;
	}

	/**
	 * @param warningMessage
	 *            the warningMessage to set
	 */
	public void setWarningMessage(String warningMessage) {
		this.warningMessage = warningMessage;
	}

	public void beginUserTransaction() {
		userTransaction = getUserTransaction();
		try {
			userTransaction.begin();
		} catch (NotSupportedException e) {
			e.printStackTrace();
		} catch (SystemException e) {
			e.printStackTrace();
		}

	}

	public void commitUserTransaction() {
		try {
			userTransaction.commit();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (RollbackException e) {
			e.printStackTrace();
		} catch (HeuristicMixedException e) {
			e.printStackTrace();
		} catch (HeuristicRollbackException e) {
			e.printStackTrace();
		} catch (SystemException e) {
			e.printStackTrace();
		}
	}

	public void rollBackUserTransaction() {
		try {
			userTransaction.rollback();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (SystemException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Возращает предыдущие этапы, на которые возможен откат текущего задания :
	 * Вернуть список этапов для отката. Выбирает список предыдущих
	 * этапов(относительно того, с которого откатываем) Исключаем из него список
	 * активных этапов текущего процесса
	 */
	public Map<String, Long> getStagesToGoBack() {
		Map<String, Long> stages =  new HashMap<String, Long>();
		// имя текущего этапа
		TaskInfo ti; // информация по текущему заданию
		WPC wpc = WPC.getInstance();
		// получаем предыдущие этапы относительно текущего
		ti = getTaskList().findTaskInfo(getIdCurrTask());
		
		List<Long> stagesFrom = ti.getIdStagesFrom();
		for (Long idStage : stagesFrom) {
			
			String stageName = (String) wpc.getData(Cnst.TBLS.stages, idStage,
					Cnst.TStages.name);
			stages.put(stageName, idStage);
			
		}

		/*int idTypeProcess = ti.getIdTypeProcess();
		stageName = (String) wpc.getData(Cnst.TBLS.stages, ti.getIdStageTo(),
				Cnst.TStages.name);
		stageNames = BusinessProcessDecider.getPrecedingStages(idTypeProcess,
				stageName);
		List<Long> activeStages = ti.getActiveStages();*/
		// нелья исключать из списка для отката этапы, на которых имеется
		// активное задание
		// List<WorkflowStages> activeStages =
		// wpc.getActiveStagesOfProcess(ti.getIdProcess());
		// //исключение активных этапов
		// for(int j=0;j<activeStages.size();j++){
		// stageNames.remove(activeStages.get(j).getNameStage());
		// }

		/*
		 * List qRes; try { qRes = dbManager.getDbFlexDirector().execQuery(
		 * "select ID_STAGE_TO " + "from db2admin.tasks " + "where
		 * id_type_process=" + idTypeProcess + " and id_process=" +
		 * ti.getIdProcess() + " and type_transaction=0" + " and
		 * dateofcomplation is not null"); if (qRes == null) throw new
		 * Exception("Завершенных этапов не найдено"); } catch (SQLException e) {
		 * e.printStackTrace(); return null; } catch (Exception e) {
		 * e.printStackTrace(); return null; }
		 */
		/*stages = new HashMap<String, Long>();// map этапов для отката задания

		for (String stName : stageNames) {
			Long idSt = wpc.getIdStageByDescription(stName, idTypeProcess);
			if (!activeStages.contains(idSt)) {
				stages.put(stName, idSt);
			}

		}*/
		/*
		 * for (int i = 0; i < qRes.size(); i++) { Integer id =
		 * Integer.parseInt((String) ((Map) qRes.get(i)) .get("ID_STAGE_TO"));
		 * String name = wpc.getStageNameById(id, idTypeProcess); if
		 * (stageNames.contains(name)) stages.put(name, id); }
		 */
		return stages;
	}

	/**
	 * Получить данные по текущему заданию. isFullData=false указывает на
	 * получение данных по заданному разграничению, isFullData=true - получение
	 * всех данных по заданию
	 * 
	 * @param isFullData
	 * @return
	 */
	public ProcessInfo getCurrTaskInfo(boolean isFullData) throws Exception {

		TaskInfo info = null;

		// если требуется загрузить все данные по заданию/процессу
		if (!isFullData) {
			if(getTaskList()==null){
				//long tstart = System.currentTimeMillis();
				info = new TaskInfo();
				info.init(this, idCurrTask, isFullData);
				info.execute();
				//LOGGER.warn("WorkflowSessionContext.getCurrTaskInfo time " + Formatter.format(Double.valueOf(System.currentTimeMillis() - tstart) / 1000));
				return info;
			}
				//throw new Exception("Сессия устарела. Снова откройте список заявок.");
			info = getTaskList().findTaskInfo(idCurrTask);
		}
		if(isFullData || info==null){
			info = new TaskInfo();
			info.init(this, idCurrTask, isFullData);
			info.execute();
		}
		return info;
	}

	public ProcessList getProcessList() {
		return processList;
	}

	public void setProcessList(ProcessList processList) {
		this.processList = processList;
	}

	public List<WorkflowUser> getUsersToAssign() {
	    try {
	        ProcessInfo currTaskInfo = getCurrTaskInfo(false);
	        if (currTaskInfo instanceof TaskInfo) {
	            TaskInfo ti = (TaskInfo) currTaskInfo;
	            return WPC.getInstance().getUsersToAssign(getIdUser(),
	                    ti.getIdStageTo());
	        }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
		return null;
	}

	public List<WorkflowUser> getAssignableForStageUsersByDepartment() {
	    try {
	        ProcessInfo currTaskInfo = getCurrTaskInfo(false);
	        if (currTaskInfo instanceof TaskInfo) {
	            TaskInfo ti = (TaskInfo) currTaskInfo;
	            return WPC.getInstance().getAssignableForStageUsersByDepartment(getIdUser(), ti.getIdStageTo());
	        }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
		return null;
	}

	public List<Long> getRolesToAssign(long idStage, Long idUser) {
		return WPC.getInstance().getRolesToAssign(idStage, idUser);
	}

	public AssignTasksList getAssignList() {
		return assignList;
	}

	public void setAssignList(AssignTasksList assignList) {
		this.assignList = assignList;
	}

	public ProcessInfo getCurrEditProcessInfo() {
		return currEditProcessInfo;
	}

	public void setCurrEditProcessInfo(ProcessInfo currEditProcessInfo) {
		this.currEditProcessInfo = currEditProcessInfo;
	}
	
	/**
	 * Возвращает {@link org.uit.director.db.dbobjects.WorkflowUser объект} текущего пользователя 
	 * 
	 * @return {@link org.uit.director.db.dbobjects.WorkflowUser объект} текущего пользователя
	 */
	public WorkflowUser getCurrentUserInfo() {
		return WPC.getInstance().getUsersMgr().getInfoUserByIdUser(idUser);
	}
	public UserJPA getUser(){
		try {
			PupFacadeLocal pup = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
			return pup.getUser(getIdUser());
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}
	public boolean emptyRoles(){
		return getUser().getRoles().isEmpty();
	}

	public Long getTaskid() {
		return taskid;
	}
	
}
