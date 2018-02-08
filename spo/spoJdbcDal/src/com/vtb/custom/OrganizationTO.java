package com.vtb.custom;

import java.io.Serializable;

/**
 * Объект транспорт (TO) "Oрганизаций" для взаимодействия с системой CRM
 * 
 * @author ildar
 * 
 */
public class OrganizationTO implements Serializable {
	private Integer spoID = null; // id SPO
	private String crmID = null; // id CRM
	private String name = null; // Наименование
	private String fullName = null; // полное юридическое Наименование
	private String inn = null; // ИНН

	public String getCrmID() {
		return crmID;
	}

	public void setCrmID(String crmID) {
		this.crmID = crmID;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getSpoID() {
		return spoID;
	}

	public void setSpoID(Integer spoID) {
		this.spoID = spoID;
	}

	public OrganizationTO(Integer spoID, String crmID) {
		super();
		this.spoID = spoID;
		this.crmID = crmID;
	}

	public String getINN() {
		return inn;
	}

	public void setINN(String aINN) {
		this.inn = aINN;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Organization spoID: ");
		sb.append(getSpoID() + "; crmID: " + getCrmID() + " (name=" + getName() + ", INN=" + getINN() + ")");
		// + ", ОГРН=" + getOGRN()
		// sb.append('\n');
		// sb.append(" IsActive: ");
		// sb.append(getIsActive());

		return sb.toString();
	}

	@Override
	public boolean equals(Object anObject) {
		if (anObject == null) {
			return false;
		}
		if (!(anObject instanceof OrganizationTO)) {
			return false;
		}
		OrganizationTO org = (OrganizationTO) anObject;

		if (org.getSpoID() != null) {
			return org.getSpoID().intValue() == getSpoID().intValue();
		}
		return org.getCrmID().equals(getCrmID());
	}
}
