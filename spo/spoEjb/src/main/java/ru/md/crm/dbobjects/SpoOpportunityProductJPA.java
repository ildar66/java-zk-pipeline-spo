package ru.md.crm.dbobjects;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Представление содержит связи сделок с продуктами.
 * @author Andrey Pavlenko
 */
@Entity
@Table(name="V_SPO_OPPORTUNITY_PRODUCT",schema="sysdba")
public class SpoOpportunityProductJPA implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name="OPPPRODUCTID")
    private String id;
    @ManyToOne
    @JoinColumn(name="OPPORTUNITYID")
    private SpoFbOpportunityJPA opportunity;//ID сделки
    @ManyToOne
    @JoinColumn(name="PRODUCTID")
    private SpoProductJPA product;//ID продукта в сделке
    private BigDecimal QUANTITY;//Объем
    private String UNIT;//Код валюты
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public SpoFbOpportunityJPA getOpportunity() {
        return opportunity;
    }
    public void setOpportunity(SpoFbOpportunityJPA opportunity) {
        this.opportunity = opportunity;
    }
    public SpoProductJPA getProduct() {
        return product;
    }
    public void setProduct(SpoProductJPA product) {
        this.product = product;
    }
    public BigDecimal getQUANTITY() {
        return QUANTITY;
    }
    public void setQUANTITY(BigDecimal qUANTITY) {
        QUANTITY = qUANTITY;
    }
    public String getUNIT() {
        return UNIT;
    }
    public void setUNIT(String uNIT) {
        UNIT = uNIT;
    }
    
    
}
