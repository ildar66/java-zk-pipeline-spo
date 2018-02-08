package com.vtb.domain;

/**
 * VtbObject "Управление для организаций"
 * 
 * @author IShafigullin
 * 
 */
public class Govern extends VtbObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id = null; // Код
	private String name = null; // название органа
	private String docType = null; // тип документа
	private String activityPeriod = null; // Периодичность действия
	private Boolean isWithInAccount = null; // Признак "Присутствие"(ДА,НЕТ)
	private Integer organizationID = null; // внешний ключ на организацию

	@Override
	public boolean equals(Object anObject) {
		if (anObject == null) {
			return false;
		}
		if (!(anObject instanceof Govern)) {
			return false;
		}
		Govern aGovern = (Govern) anObject;
		return aGovern.getId().intValue() == getId().intValue();
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Govern: ");
		sb.append(getId() + "(" + getName() + ")");
		// sb.append('\n');
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Govern(Integer aId, Integer aOrganizationID) {
		setId(aId);
		setOrganizationID(aOrganizationID);
		setName("");
	}	

	public Govern(Integer aId, String aName) {
		setId(aId);
		setOrganizationID(null);
		setName(aName);
	}

	public Govern(Integer aId) {
		setId(aId);
		setOrganizationID(null);
		setName("");
	}

	public Boolean getIsWithInAccount() {
		return isWithInAccount;
	}

	public void setIsWithInAccount(Boolean isColegial) {
		this.isWithInAccount = isColegial;
	}

	public String getActivityPeriod() {
		return activityPeriod;
	}

	public void setActivityPeriod(String activityPeriod) {
		this.activityPeriod = activityPeriod;
	}

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	public Integer getOrganizationID() {
		return organizationID;
	}

	public void setOrganizationID(Integer organizationID) {
		this.organizationID = organizationID;
	}

}
