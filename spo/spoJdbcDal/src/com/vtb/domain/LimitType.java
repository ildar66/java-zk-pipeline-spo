package com.vtb.domain;
/**
 * VtbObject "типов лимитов"
 * 
 * @author IShafigullin
 * 
 */
public class LimitType extends VtbObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5096881087270930257L;
	private Integer id = null; //Код типов лимитов 
	private String name = null; //Имя типов лимитов 

	@Override
	public boolean equals(Object anObject) {
		if (anObject == null) {
			return false;
		}
		if (!(anObject instanceof LimitType)) {
			return false;
		}
		LimitType aLimitType = (LimitType) anObject;
		return aLimitType.getId().intValue() == getId().intValue();
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("LimitType: ");
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

	public LimitType(Integer aId, String aName) {
		setId(aId);
		setName(aName);
	}
	
	public LimitType(Integer aId) {
		setId(aId);
		setName("none");
	}	

}
