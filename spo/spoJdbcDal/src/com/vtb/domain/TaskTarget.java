package com.vtb.domain;

import ru.masterdm.compendium.domain.crm.TargetType;

public class TaskTarget extends VtbObject{
	private static final long serialVersionUID = 1L;
	private Long id;
	private boolean flag;
	private TargetType targetType;
	
    public TaskTarget() {
        super();
    }

	public TaskTarget(Long id) {
		super();
		this.id = id;
	}
	
	public TaskTarget(Long id, boolean flag, TargetType targetType) {
        super();
        this.id = id;
        this.flag = flag;
        this.targetType = targetType;
    }
	
	public boolean isFlag() {return flag;}
	
	public void setFlag(boolean flag) {this.flag = flag;}
	
	public TargetType getTargetType() {return targetType;}
	
	public void setTargetType(TargetType targetType) {this.targetType = targetType;}
	
	public Long getId() {return id;}
	
	public void setId(Long id) {this.id = id;}
}