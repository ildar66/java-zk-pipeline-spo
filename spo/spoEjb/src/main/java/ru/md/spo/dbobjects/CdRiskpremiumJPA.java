package ru.md.spo.dbobjects;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "cd_riskpremium")
public class CdRiskpremiumJPA implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id @Column(name = "id")
    private Long id;
    private String description;
    private String value;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getDescription() {
    	if(description==null)
    		return "";
        return description.replaceAll("\"", "&#22;").replaceAll("'", "&#27;").replaceAll("\n", " ");
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
   
    
}
