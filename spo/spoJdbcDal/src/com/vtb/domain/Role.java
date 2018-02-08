package com.vtb.domain;
/**
 * VtbObject "роли оператора(пользователя)"
 * 
 * @author IShafigullin
 * 
 */
public class Role extends VtbObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4207380680459798774L;
	private Integer id = null; //Код "роли оператора(пользователя)"
	private String name = null; //Имя "роли оператора(пользователя)"
	private Integer processTypeID = null; //id процесса 

	@Override
	public boolean equals(Object anObject) {
		if (anObject == null) {
			return false;
		}
		if (!(anObject instanceof Role)) {
			return false;
		}
		Role aRole = (Role) anObject;
		return aRole.getId().intValue() == getId().intValue();
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Role: ");
		sb.append(getId() + "(" + getName() + ")");
		//sb.append('\n');
		//sb.append(" IsActive: ");
		//sb.append(getIsActive());

		return sb.toString();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Role(Integer aId, String aName) {
		setId(aId);
		setName(aName);
	}
	
	public Role(Integer aId) {
		setId(aId);
		setName("none");
	}

	public Integer getProcessTypeID() {
		return processTypeID;
	}

	public void setProcessTypeID(Integer processTypeID) {
		this.processTypeID = processTypeID;
	}	

}
