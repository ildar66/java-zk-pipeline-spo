package org.uit.director.tasks;

import java.util.Calendar;

import org.uit.director.contexts.WorkflowSessionContext;

import ru.md.spo.util.Config;

/**
 * Created by IntelliJ IDEA. User: pd190390 Date: 25.02.2005 Time: 9:31:12 To
 * change this template use File | Settings | File Templates.
 */
public class TaskCachInfo extends TaskInfo {

	// private TaskInfo taskInfo = null;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * * время жизни кешируемых данных
	 */
	// private final long timeToDelete = 60000 * Long.parseLong((String)
	// Config.getProperty("CACHE_TIMELIFE"));
	/**
	 * Максимальное число записей в кеше
	 */
	private final int maxCachSize = Integer.parseInt(Config
			.getProperty("MAX_CACHE_SIZE"));

	@Override
	public void init(WorkflowSessionContext wsc, Long id_task, boolean isAll) {

		super.init(wsc, id_task, isAll);

	}
	
	@Override
	public String execute() {
		String res = "ok";
		try {

			TaskInfo taskInfo = wsc.getCacheManager().getCacheObj(
					idTask);

			if (taskInfo == null || (!taskInfo.isAll && isAll)) {

				String ress = super.execute();
				if (!ress.equalsIgnoreCase("ok")) {
					return "error";
				}

				/*
				 * taskInfo = new TaskInfo(); taskInfo.init(wsc, id_task,
				 * isAll); String ress = taskInfo.execute(); if
				 * (ress.equalsIgnoreCase("ok")) { TODO } else { return "error"; }
				 */
				// int activeStages = Integer.parseInt(
				// taskInfo.getAttributes().getStringValueByName("Число активных
				// этапов"));
				/*
				 * Если у данного процесса нет расчщиплений, то задание можно
				 * занести в кеш
				 */
				// if (activeStages == 1)
				setTimeToLife(Calendar.getInstance().getTimeInMillis());
				if (wsc.getCacheManager().getCacheSize() < maxCachSize) {
					wsc.getCacheManager().addCache(idTask, this);
					// wsc.startClearingCache();
				}

				// } else {

				

			} else {
				createCopy(taskInfo);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return "Error";

		}
		return res;

	}

	private void createCopy(TaskInfo taskInfo) {
		attributes = taskInfo.attributes;
		colorTask = taskInfo.colorTask;
		dateOfComming = taskInfo.dateOfComming;
		dateOfMustComplete = taskInfo.dateOfMustComplete;
		dateOfRealComplete = taskInfo.dateOfRealComplete;
		dateOfTaking = taskInfo.dateOfTaking;
		wfTaskInfo = taskInfo.wfTaskInfo;
		wfpInfo = taskInfo.wfpInfo;
	/*	setIdProcess( taskInfo.getIdProcess());
		setIdStageFrom ( taskInfo.getIdStageFrom());
		setIdStageTo ( taskInfo.getIdStageTo());
		setIdTypeProcess ( taskInfo.getIdTypeProcess());*/
		isExpired = taskInfo.isExpired;
		limitDays = taskInfo.limitDays;
		timeToLife = taskInfo.timeToLife;
		typeLimitDay = taskInfo.typeLimitDay;
		idExecutor = taskInfo.idExecutor;
	}

}
