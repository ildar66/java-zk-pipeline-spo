package com.vtb.custom;

import java.io.Serializable;

import com.vtb.domain.Okved;

/**
 * Объект транспорт (TO) "ОКВЭД" для взаимодействия с системой CRM
 * 
 * @author ildar
 * 
 */
public class OkvedTO implements Serializable {
	private Integer spoID = null; // id SPO
	private String crmID = null; // id CRM
	private String okved = null; // код ОКВЭД
	private String name = null; // название
	private String description = null;// описание.
	private Okved parent = null; // родитель SPO
	private String parentCRM = null;//родитель CRM

	public String getCrmID() {
		return crmID;
	}

	public void setCrmID(String crmID) {
		this.crmID = crmID;
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

	public OkvedTO(Integer spoID, String crmID) {
		super();
		this.spoID = spoID;
		this.crmID = crmID;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getOkved() {
		return okved;
	}

	public void setOkved(String okved) {
		this.okved = okved;
	}

	public Okved getParent() {
		if(parent == null){
			parent = new Okved(null);
		}
		return parent;
	}

	public void setParent(Okved parent) {
		this.parent = parent;
	}

	public String getParentCRM() {
		return parentCRM;
	}

	public void setParentCRM(String parentCRM) {
		this.parentCRM = parentCRM;
	}
}
