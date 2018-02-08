package com.vtb.domain;

/**
 * VtbObject "Список стоп-факторов по которым может быть осуществлен отказ"
 * 
 * @author IShafigullin
 * 
 */
public class SpoStop extends VtbObject {

	private static final long serialVersionUID = 1L;

	private String id = null; // "Идентификатор"
	/**
	 * Справочник стоп-факторов (из системы СПО)
	 */
	private String name = null; // Имя стоп фактора

	@Override
	public boolean equals(Object anObject) {
		if (anObject == null) {
			return false;
		}
		if (!(anObject instanceof SpoStop)) {
			return false;
		}
		SpoStop aSpoStop = (SpoStop) anObject;
		return aSpoStop.getId().equals(getId());
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("SpoStop: ");
		sb.append(getId() + "(name=" + getName() + ")");

		// sb.append('\n');
		// sb.append(" IsActive: ");
		// sb.append(getIsActive());

		return sb.toString();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public SpoStop(String aId, String aName) {
		setId(aId);
		setName(aName);
	}

	public SpoStop(String aId) {
		setId(aId);
		setName("none");
	}

}
