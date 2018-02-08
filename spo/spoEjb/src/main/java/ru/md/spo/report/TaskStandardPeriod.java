package ru.md.spo.report;

import java.io.Serializable;

import ru.md.spo.dbobjects.TaskJPA;

import com.vtb.domain.StandardPeriod;

public class TaskStandardPeriod implements Serializable {

	private static final long serialVersionUID = 6932913526976393287L;

	private TaskJPA task;
	private StandardPeriod standardPeriod;

	public TaskJPA getTask() {
		return task;
	}

	public void setTask(TaskJPA task) {
		this.task = task;
	}

	public StandardPeriod getStandardPeriod() {
		return standardPeriod;
	}

	public void setStandardPeriod(StandardPeriod standardPeriod) {
		this.standardPeriod = standardPeriod;
	}

	public TaskStandardPeriod(TaskJPA task, StandardPeriod standardPeriod) {
		super();
		this.task = task;
		this.standardPeriod = standardPeriod;
	}
	
}
