package ru.md.spo.dbobjects;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *  Типы вознаграждений
 * @author Andrey Pavlenko
 *
 */
@Entity
@Table(name = "cd_premium_type")
public class CdPremiumTypeJPA implements Serializable {
    private static final long serialVersionUID = 1L;
    public enum Type {
        PRODUCT("Сделка"),SUBLIMIT("Сублимит"),COMMON("Общее");
        String name;
        private Type(String typename){
            name = typename;
        }
        public String getName(){
            return name;
        }
    }

    @Id @Column(name = "ID_PREMIUM")
    private Long id;
    private String premium_name;//Наименование вознаграждений
    private String trade_type;//к какому типу относится Сублимит/Сделка/Общее
    private String value;//«Валюта/ %», «Формула», «Валюта», или произвольный текст
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public String getPremium_name() {
        return premium_name;
    }
    public void setPremium_name(String premium_name) {
        this.premium_name = premium_name;
    }
    public String getTrade_type() {
        return trade_type;
    }
    public void setTrade_type(String trade_type) {
        this.trade_type = trade_type;
    }
    @Override
    public String toString() {
        return "CdPremiumTypeJPA [id=" + id + ", premium_name=" + premium_name
                + ", trade_type=" + trade_type + "]";
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CdPremiumTypeJPA other = (CdPremiumTypeJPA) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
   
    
}
