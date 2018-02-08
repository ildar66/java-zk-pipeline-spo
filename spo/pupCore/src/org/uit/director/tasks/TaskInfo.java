package org.uit.director.tasks;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.uit.director.calendar.BusinessCalendar;
import org.uit.director.contexts.WPC;
import org.uit.director.contexts.WorkflowSessionContext;
import org.uit.director.db.dbobjects.Attribute;
import org.uit.director.db.dbobjects.BasicAttribute;
import org.uit.director.db.dbobjects.Cnst;
import org.uit.director.db.dbobjects.VarsPermissions;
import org.uit.director.db.dbobjects.VarsPermissions.Permission;
import org.uit.director.db.dbobjects.WorkflowUser;

import ru.md.spo.util.Config;

import com.vtb.domain.WorkflowTaskInfo;
import com.vtb.util.Formatter;

public class TaskInfo extends ProcessInfo {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = Logger.getLogger(TaskInfo.class.getName());

	protected Long idTask;

	protected Date dateOfComming;
	protected Date dateOfMustComplete;
	protected Date dateOfTaking;
	protected Date dateOfRealComplete;
	protected Integer limitDays;
	protected boolean typeLimitDay; // true - срок расчитывается по календарным

	// дням, false - по рабочим дням

	protected Long idExecutor;
	protected String executorFIO;
	
	protected Integer idStatus;    // id статуса операции
	protected String colorTask = Config.getProperty("COLOR_NOT_EXPIRED");

	protected WorkflowTaskInfo wfTaskInfo;

	@Override
	public String execute() {
		try {
			
			wfTaskInfo = wsc.getDbManager().getDbFlexDirector().getTaskInfo(idTask);
			setIdProcess(wfTaskInfo.getIdProcess());
			setIdStatus(wfTaskInfo.getIdStatus());
			setIdExecutor(wfTaskInfo.getIdExecutor());
			setExecutorFIO(wfTaskInfo.getExecutorFIO());

			String d = wfTaskInfo.getDateOfComming();
			dateOfComming = Formatter.parseDateRobust(d);
			if (!wfTaskInfo.getDateOfTaking().equals("")) 
				dateOfTaking = Formatter.parseDateRobust(wfTaskInfo.getDateOfTaking());
			if (!wfTaskInfo.getDateOfComplation().equals("")) 
				dateOfRealComplete = Formatter.parseDateRobust(wfTaskInfo.getDateOfComplation());

			super.execute();
			addValuesSystemAttributes();

			// Определим срок выполнения этапа
			limitDays = ((Integer) WPC.getInstance().getData(Cnst.TBLS.stages, wfTaskInfo.getIdStageTo(), Cnst.TStages.limitDay));

			int typeLimitDay = ((Integer) WPC.getInstance().getData(
					Cnst.TBLS.stages, wfTaskInfo.getIdStageTo(),
					Cnst.TStages.typeLimitDay));
			this.typeLimitDay = (typeLimitDay == 0);

			if (limitDays == 0) {
				dateOfMustComplete = null;
			} else {

				if (this.typeLimitDay) {
					Calendar calAdd = Calendar.getInstance();
					calAdd.setTime(dateOfComming);
					calAdd.add(Calendar.DAY_OF_MONTH, limitDays);
					dateOfMustComplete = calAdd.getTime();
				} else {
					BusinessCalendar bCalendar = new BusinessCalendar();
					dateOfMustComplete = bCalendar.addBusinessDays(
							dateOfComming, limitDays);
				}
			}

			if (attributes.size() > 0) {

				/* определим срок выполнения процесса */
				int limitDayProcess = getCountExecute();

				String strDateOfStartProcess = getDateInitProcess();

				Date dateOfStartProcess = Formatter.parseDateRobust(strDateOfStartProcess);
				Calendar calMustEndProc = Calendar.getInstance();
				calMustEndProc.setTime(dateOfStartProcess);
				calMustEndProc.add(Calendar.DAY_OF_MONTH, limitDayProcess);

				/* определим срок окончания выполнения этапа */
				if (dateOfMustComplete != null) {
					// дата планового окончания этапа (операции)
					Calendar calMustCpl = Calendar.getInstance();
					calMustCpl.setTime(dateOfMustComplete);
					
					// дата, когда должно прийти сообщение о приближающемся окончании срока выполнения операции
					Calendar calNow = Calendar.getInstance();
					int attentionDay = ((Integer) WPC.getInstance().getData(
							Cnst.TBLS.stages, wfTaskInfo.getIdStageTo(),
							Cnst.TStages.attentionDay)).intValue();

					calNow.add(Calendar.DAY_OF_MONTH, attentionDay);

					isExpired = false;

					// плановая дата окончания операции меньше, чем сегодняшняя дата + да сколько дней информировать о
					// приближении окончания
					if (calNow.after(calMustCpl)) {
						colorTask = Config.getProperty("COLOR_ONE_DAY");
					}
					
					// плановая дата окончания операции меньше, чем сегодняшняя дата
					if (Calendar.getInstance().after(calMustCpl)) {
						colorTask = Config.getProperty("COLOR_EXPIRED");
						isExpired = true;
					}

					// плановая дата окончания процесса меньше, чем сегодняшняя дата
					if (Calendar.getInstance().after(calMustEndProc)) {
						colorTask = Config.getProperty("COLOR_EXPIRED_PROCESS");
					}
				} else {
					isExpired = false;
				}

			}
			// long t5 = System.currentTimeMillis();
			// System.out.println("@@@@@@@@@@@@@@ end=" + (t5-t4));

		} catch (Exception e) {
			//logger.severe("Error on geting task information");
			//logger.log(Level.SEVERE, e.getMessage(), e);
			//e.printStackTrace();
			
			return "Error";
		} /*
			 * finally { wsc = null; }
			 */

		timeToLife = Calendar.getInstance().getTimeInMillis();

		return "ok";
	}

	private void addValuesSystemAttributes() {
		try {
			String name = new String(Config.getProperty("COMMON_VAR7").getBytes("ISO-8859-1"));
			//logger.info("value of property 'COMMON_VAR7' is '" + name + "'");
			
			WorkflowUser wu = wsc.getCurrentUserInfo();
			//logger.info("workflow user is " + wu.getLogin());
			
			String depShortName = wu.getDepartament().getShortName();
			//logger.info("department " + depShortName + " of user " + wu.getLogin());
			
			AttributesStructList attrs = super.getAttributes();
			//logger.info("attrs is not null: " + (attrs != null));
			
			BasicAttribute bAttr = attrs.findAttributeByName(name);
			//logger.info("bAttr is not null: " + (bAttr != null));
			
			if(bAttr!=null){
			    Attribute attr = bAttr.getAttribute();
			    //logger.info("attr is not null: " + (attr != null));
				attr.setValueAttributeStr(depShortName);
			}
		} catch (UnsupportedEncodingException e) {
			logger.log(Level.SEVERE, e.getMessage());
		}

	}

	public void init(WorkflowSessionContext wsc, Long id_task, boolean isAll) {

		super.init(wsc, isAll);
		idTask = id_task;
	}

	public Long getIdTask() {
		return idTask;
	}

	public String getNameStageTo() {
		return (String) WPC.getInstance().getData(Cnst.TBLS.stages,
				wfTaskInfo.getIdStageTo(), Cnst.TStages.name);
	}

	/*
	 * public String getNameStageFrom() { return (String)
	 * WPC.getInstance().getData(Cnst.TBLS.stages, wfTaskInfo.getIdStageFrom(),
	 * Cnst.TStages.name); }
	 */

	public String getDateOfCommingStr() {
		return dateFormater.format(dateOfComming);
	}

	public String getDateOfMustCompleteStr() {
		if (dateOfMustComplete == null) {
			return "-";
		} else {
			return dateFormater.format(dateOfMustComplete);
		}
	}

	public String getDateOfRealCompleteStr() {
		if (dateOfRealComplete == null) {
			return "-";
		} else {
			return dateFormater.format(dateOfRealComplete);
		}
	}

	public String getDateOfTakingStr() {
		if (dateOfTaking == null) {
			return "-";
		} else {
			return dateFormater.format(dateOfTaking);
		}
	}

	public String getColorTask() {
		return colorTask;
	}

	public Date getDateOfComming() {
		return dateOfComming;
	}

	public Date getDateOfMustComplete() {
		return dateOfMustComplete;
	}

	public long getIdStageTo() {
		return wfTaskInfo.getIdStageTo();
	}

	public List<Long> getIdStagesFrom() {
		return wfTaskInfo.getIdStagesFrom();
	}

	@Override
	protected void getRequeredAttributesAndParams() {

		if (isAll()) {
			super.getAllAttributesAndParams();
			return;
		}

		VarsPermissions vars = WPC.getInstance().getStagesPermissions(wfTaskInfo.getIdStageTo());

		// прав никаких нет
		if (vars == null) {
			return;
		}
		List<Long> varSet = new ArrayList<Long>();
		HashMap<Long, HashSet<Permission>> varPermissions = vars
				.getVarPermissions();
		Iterator<Long> it = varPermissions.keySet().iterator();

		// добавим все дочерние атрибуты (наследование родительских прав)
		HashMap<Long, HashSet<Permission>> addVars = new HashMap<Long, HashSet<Permission>>();

		while (it.hasNext()) {
			Long idVar = it.next();
			// права родительского атрибута
			Set<Permission> setPerm = varPermissions.get(idVar);
			// список всех дочерних элементов
			List<Long> ch = WPC.getInstance().getVarNodes().get(idVar);
			if (ch != null) {
				for (Long idChVar : ch) {

					// найдем права на пересменные в основном множестве
					// (множестве родительских элементов)
					HashSet<Permission> setChPerm = varPermissions.get(idChVar);
					if (setChPerm == null) {

						// найдем права на пересменные в множестве дочерних
						// эдементов
						setChPerm = addVars.get(idChVar);
						if (setChPerm == null) {
							setChPerm = new HashSet<Permission>();
							// добавляем такие же права как у родителя
							setChPerm.addAll(setPerm);
							addVars.put(idChVar, setChPerm);
						} else {
							setChPerm.addAll(setPerm);
						}

					}
					// если в основном множестве уже есть переменные доступа
					else {
						// добавляем права такие же как у родителя
						setChPerm.addAll(setPerm);
					}
				}
			}
		}

		vars.getVarPermissions().putAll(addVars);

		// определим допустимые атрибуты для заданного этапа

		it = varPermissions.keySet().iterator();
		while (it.hasNext()) {
			Long idVar = it.next();
			HashSet<Permission> setPerm = varPermissions.get(idVar);

			if (!varSet.contains(idVar)) {
				Attribute attr = new Attribute(null, setPerm, Config
						.getProperty("DATE_TIME_DB_FORMAT"), Config
						.getProperty("DATE_TIME_FORMAT"), Config
						.getProperty("DATE_FORMAT"));

				attr.setWorkflowVariable(WPC.getInstance().findVariableById(
						idVar));
				reqAttrs.add(attr);
				varSet.add(idVar);
				Object[] o = new Object[2];
				o[0] = wfpInfo.getIdProcess();
				o[1] = idVar;
				varParams.add(o);
			}
		}
	}

	public void setIdStageTo(Long idStageTo) {
		wfTaskInfo.setIdStageTo(idStageTo);
	}

	public void setIdStagesFrom(ArrayList<Long> idStagesFrom) {
		wfTaskInfo.setIdStageFrom(idStagesFrom);
	}

	public Long getIdExecutor() {
		return idExecutor;
	}

	public void setIdExecutor(Long idExecutor) {
		this.idExecutor = idExecutor;
	}

	public Long getIdDepartament() {
		return wfTaskInfo.getIdDepartament();
	}

	public Integer getIdStatus() {
		return idStatus;
	}

	public void setIdStatus(Integer idStatus) {
		this.idStatus = idStatus;
	}

    public String getExecutorFIO() {
        return executorFIO;
    }

    public void setExecutorFIO(String executorFIO) {
        this.executorFIO = executorFIO;
    }
}
