package com.vtb.domain;

/**
 * VtbObject "Группа клиентов"
 * 
 * @author IShafigullin
 * 
 */
public class OrganizationGroup extends VtbObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id = null; // id
	private String name = null; // Имя
	private String description = null;// описание
	private String type = null; // тип группы
	private String inn = null; // ИНН
	private String ogrn = null; // ОГРН
	private String okpo = null;// код ОКПО
	private Integer industryID = null; // Отрасль
	private Integer regionID = null; // Регион
	private String crmID = null; // код в системе CRM

	@Override
	public boolean equals(Object anObject) {
		if (anObject == null) {
			return false;
		}
		if (!(anObject instanceof OrganizationGroup)) {
			return false;
		}
		OrganizationGroup aOrganizationGroup = (OrganizationGroup) anObject;
		return aOrganizationGroup.getId().intValue() == getId().intValue();
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("OrganizationGroup: ");
		sb.append(getId() + "(name=" + getName() + ", info=" + getDescription() + ")");

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
	
	public OrganizationGroup(Integer aId, String crmID, String aName) {
		setId(aId);
		setCrmID(crmID);
		setName(aName);
	}	

	public OrganizationGroup(Integer aId, String aName) {
		setId(aId);
		setCrmID(null);
		setName(aName);
	}

	public OrganizationGroup(Integer aId) {
		setId(aId);
		setCrmID(null);
		setName("none");
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCrmID() {
		return crmID;
	}

	public void setCrmID(String crmID) {
		this.crmID = crmID;
	}

	public Integer getIndustryID() {
		return industryID;
	}

	public void setIndustryID(Integer industryID) {
		this.industryID = industryID;
	}

	public String getInn() {
		return inn;
	}

	public void setInn(String inn) {
		this.inn = inn;
	}

	public String getOgrn() {
		return ogrn;
	}

	public void setOgrn(String ogrn) {
		this.ogrn = ogrn;
	}

	public String getOkpo() {
		return okpo;
	}

	public void setOkpo(String okpo) {
		this.okpo = okpo;
	}

	public Integer getRegionID() {
		return regionID;
	}

	public void setRegionID(Integer regionID) {
		this.regionID = regionID;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
