package ru.md.spo.dbobjects;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

/**
 * Вид продукта.
 * @author Andrey Pavlenko
 *
 */
@Entity
@Table(name = "V_SPO_PRODUCT")
public class ProductTypeJPA implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id @Column(name = "PRODUCTID")
    private String id;
    private String name;
    private String family;//Кредитование, Документарные операции, Банковские гарантии
    private Long is_active;
    private Long spo_enable;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "r_limitt_prodt", joinColumns = @JoinColumn(name = "ID_PROD_TYPE"), inverseJoinColumns = @JoinColumn(name = "ID_LIMIT_TYPE"))
    private List<LimitTypeJPA> limitList;
    
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
    public String getFamily() {
        return family;
    }
    public void setFamily(String family) {
        this.family = family;
    }
    public Long getIs_active() {
        return is_active;
    }
    public void setIs_active(Long is_active) {
        this.is_active = is_active;
    }
    public List<LimitTypeJPA> getLimitList() {
        return limitList;
    }
    public void setLimitList(List<LimitTypeJPA> limitList) {
        this.limitList = limitList;
    }
	public Long getSpo_enable() {
		return spo_enable;
	}
	public void setSpo_enable(Long spo_enable) {
		this.spo_enable = spo_enable;
	}
    
}
