package com.vtb.model;

import java.util.Date;

public interface SchedulerMQProcessor {

	/**
	 * @param timerInfo
	 */
	public void cancelTimer(Object timerInfo);

	/**
	 * @param firstDate
	 * @param timeout
	 * @param timerInfo
	 */
	public void initializeTimer(Date firstDate, long timeout, Object timerInfo);
}
