package com.vtb.domain;

import java.util.ArrayList;

import ru.masterdm.compendium.domain.Department;

/**
 * @author Andrey Pavlenko
 * Другие инициирующие подразделения
 */
public class TaskDepartment extends VtbObject{
	private static final long serialVersionUID = 1L;
	private Long id;
	private Department dep;
	private ArrayList<TaskManager> managers;
	
	public TaskDepartment() {
        super();
    }

	
	public TaskDepartment(Long id, Department dep) {
        super();
        this.id = id;
        this.dep = dep;
        managers=new ArrayList<TaskManager>();
    }

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Department getDep() {
		return dep;
	}
	public void setDep(Department dep) {
		this.dep = dep;
	}
	public ArrayList<TaskManager> getManagers() {
		return managers;
	}
	public void setManagers(ArrayList<TaskManager> managers) {
		this.managers = managers;
	}
}
