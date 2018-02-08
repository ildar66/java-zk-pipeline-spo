package com.vtb.domain;

/**
 * VtbObject "тип процесса"
 * 
 * @author IShafigullin
 * 
 */
public class ProcessType extends VtbObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id = null; // id "тип процесса"
	private String description = null;// описание

	@Override
	public boolean equals(Object anObject) {
		if (anObject == null) {
			return false;
		}
		if (!(anObject instanceof ProcessType)) {
			return false;
		}
		ProcessType aProcessType = (ProcessType) anObject;
		return aProcessType.getId().intValue() == getId().intValue();
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("ProcessType: ");
		sb.append(getId() + "(description=" + getDescription()+ ")");

		//sb.append('\n');
		// sb.append(" IsActive: ");
		// sb.append(getIsActive());

		return sb.toString();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public ProcessType(Integer aId, String aDescription) {
		setId(aId);
		setDescription(aDescription);
	}

	public ProcessType(Integer aId) {
		setId(aId);
		setDescription("none");
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
