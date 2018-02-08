package com.vtb.custom;

import java.io.Serializable;

/**
 * Объект транспорт (TO) "Групп Oрганизаций" для взаимодействия с системой CRM
 * 
 * @author ildar
 * 
 */
public class OrganizationGroupTO implements Serializable {
	private String crmID = null; // id CRM
	private String name = null; // Наименование
	private String description = null; // описание
	private String type = null; // тип группы

	public String getCrmID() {
		return crmID;
	}

	public void setCrmID(String crmID) {
		this.crmID = crmID;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String fullName) {
		this.description = fullName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public OrganizationGroupTO(String crmID) {
		super();
		this.crmID = crmID;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
