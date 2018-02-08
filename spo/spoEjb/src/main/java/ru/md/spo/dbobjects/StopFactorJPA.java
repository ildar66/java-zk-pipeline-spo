package ru.md.spo.dbobjects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "STOPFACTOR")
public class StopFactorJPA {
    private static final long serialVersionUID = 1L;    
    
    @Id
    @Column(name = "CODE")  
    private String idStopFactor;
    
    @Column(name = "DESCRIPTION")
    private String stopFactor;

    @ManyToOne
    @JoinColumn(name = "AUTHOR", referencedColumnName = "ID_TYPE")
    private StopFactorTypeJPA type;

    /**
     * Возвращает {@link String первичный ключ}
     * 
     * @return {@link String первичный ключ}
     */
    public String getIdStopFactor() {
        return this.idStopFactor;
    }

    /**
     * Устанавливает {@link String первичный ключ}
     * 
     * @param idStopFactor {@link String первичный ключ}
     */
    public void setIdStopFactor(String idStopFactor) {
        this.idStopFactor = idStopFactor;
    }

    /**
     * Возвращает {@link String описание} причин отказа
     * 
     * @return {@link String Описание} причин отказа
     */
    public String getStopFactor() {
        return this.stopFactor;
    }

    /**
     * Устанавливает {@link String описание} причин отказа
     * 
     * @param stopFactor {@link String описание} причин отказа
     */
    public void setStopFactor(String stopFactor) {
        this.stopFactor = stopFactor;
    }

    /**
     * Возвращает {@link StopFactorTypeJPA автора} стоп-фактора
     * 
     * @return {@link StopFactorTypeJPA автора} стоп-фактора
     */
    public StopFactorTypeJPA getType() {
        return this.type;
    }

    /**
     * Устанавливает {@link StopFactorTypeJPA автора} стоп-фактора
     * 
     * @param type {@link StopFactorTypeJPA автор} стоп-фактора
     */
    public void setType(StopFactorTypeJPA type) {
        this.type = type;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return " StopFactor ID: " + getIdStopFactor() + " StopFactor name:"  + getStopFactor() + " StopFactor type:" + getIdStopFactor();
    }
}
