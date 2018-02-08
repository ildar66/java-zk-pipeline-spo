package ru.md.spo.dbobjects;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "v_companygroup")
public class OrgGroupJPA implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "CRMID")
    private String id;
    private String name;
    private String ogrn;
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

	public String getOgrn() {
		return ogrn==null?"":ogrn;
	}

	public void setOgrn(String ogrn) {
		this.ogrn = ogrn;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
