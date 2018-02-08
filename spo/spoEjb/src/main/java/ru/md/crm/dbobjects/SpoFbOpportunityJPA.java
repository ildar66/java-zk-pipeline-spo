package ru.md.crm.dbobjects;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.vtb.util.Formatter;

/**
 * Представление содержит дополнительные атрибуты сделки.
 * @author Andrey Pavlenko
 */
@Entity
@Table(name="v_spo_fb_opportunity",schema="sysdba")
public class SpoFbOpportunityJPA implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name="OPPORTUNITYID")
    private String id;

    private String NUM;//Номер сделки
    
    @OneToMany(mappedBy = "opportunity", fetch = FetchType.LAZY)
    private Set<SpoOpportunityProductJPA> spoOpportunityProductSet;

    public String getProductName(){
        String res = "";
        for (SpoOpportunityProductJPA oppProd: spoOpportunityProductSet) {
            res += oppProd.getProduct().getNAME();
        }
        return res;
    }
    public String getSumAndCurrency(){
        for (SpoOpportunityProductJPA oppProd: spoOpportunityProductSet) {
            return Formatter.format(oppProd.getQUANTITY()) + " " + oppProd.getUNIT();
        }
        return "сумма не задана";
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNUM() {
        return NUM;
    }

    public void setNUM(String nUM) {
        NUM = nUM;
    }

    public Set<SpoOpportunityProductJPA> getSpoOpportunityProductSet() {
        return spoOpportunityProductSet;
    }

    public void setSpoOpportunityProductSet(
            Set<SpoOpportunityProductJPA> spoOpportunityProductSet) {
        this.spoOpportunityProductSet = spoOpportunityProductSet;
    }
    
    
}
