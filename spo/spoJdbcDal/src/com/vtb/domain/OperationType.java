package com.vtb.domain;

public class OperationType extends VtbObject {
	private static final long serialVersionUID = 1L;
	private Long id = null; //Код
	private OperationTypeGroup group;// Группа видов сделок
	private String name = null; //Имя
	
    public OperationType(Long id, String name) {
        super();
        this.id = id;
        this.name = name;
    }

    public OperationType(Long id, String name, OperationTypeGroup group) {
        super();
        this.id = id;
        this.name = name;
        this.group = group;
    }
    
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name==null?"не выбрано":name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return the group
	 */
	public OperationTypeGroup getGroup() {
		return group;
	}
	/**
	 * @param group the group to set
	 */
	public void setGroup(OperationTypeGroup group) {
		this.group = group;
	}
	
}
