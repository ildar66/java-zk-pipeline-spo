package com.vtb.domain;

/**
 * VtbObject "печатных форм"
 * 
 * @author IShafigullin
 * 
 */
public class Report extends VtbObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id = null; // id "печатных форм"
	private String type = null; // Имя
	private String description = null;// описание
	private String design = null; // design
	private String config = null; // config
	private Integer[] processIDs = null; //связь с процессами.
	private String processNames = null;//имена процессов.

	public Integer[] getProcessIDs() {
		return processIDs;
	}

	public void setProcessIDs(Integer[] processIDs) {
		this.processIDs = processIDs;
	}

	public Report(Integer aId, String aType) {
		setId(aId);
		setType(aType);
	}

	public Report(Integer aId) {
		setId(aId);
		setType("none");
	}

	public String getConfig() {
		return config;
	}

	public void setConfig(String config) {
		this.config = config;
	}

	public String getDesign() {
		return design;
	}

	public void setDesign(String design) {
		this.design = design;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public boolean equals(Object anObject) {
		if (anObject == null) {
			return false;
		}
		if (!(anObject instanceof Report)) {
			return false;
		}
		Report aReport = (Report) anObject;
		return aReport.getId().intValue() == getId().intValue();
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Report: ");
		sb.append(getId() + "(Type=" + getType() + ")");

		// sb.append('\n');
		// sb.append(" IsActive: ");
		// sb.append(getIsActive());

		return sb.toString();
	}

	public String getProcessNames() {
		return processNames;
	}

	public void setProcessNames(String processNames) {
		this.processNames = processNames;
	}
}
