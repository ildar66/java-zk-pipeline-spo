package ru.md.spo.dbobjects;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Фондирование по пониженной ставке.
 * @author Andrey Pavlenko
 *
 */
@Entity
@Table(name = "crm_fund_down")
public class FundDownJPA implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id @Column(name = "ITEMN_ID")
    private String id;
    private String text;
    private Long is_active;
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
    public Long getIs_active() {
        return is_active;
    }
    public void setIs_active(Long is_active) {
        this.is_active = is_active;
    }
    @Override
    public String toString() {
        return "FundDownJPA [id=" + id + ", text=" + text + "]";
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
        FundDownJPA other = (FundDownJPA) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
    
}
