package ru.md.spo.dbobjects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "STOPFACTOR_TYPES")
public class StopFactorTypeJPA {
    
    /**
     * @serial
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ID_TYPE")
    private String idStopFactorType;
    @Column(name = "DESCRIPTION")
    private String stopFactorType;

    /**
     * Возвращает {@link String первичный ключ}
     * 
     * @return {@link String первичный ключ}
     */
    public String getIdStopFactorType() {
        return this.idStopFactorType;
    }

    /**
     * Устанавливает {@link String первичный ключ}
     * 
     * @param idStopFactorType {@link String первичный ключ}
     */
    public void setIdStopFactorType(String idStopFactorType) {
        this.idStopFactorType = idStopFactorType;
    }

    /**
     * Возвращает {@link String описание} автора стоп-фактора
     * 
     * @return {@link String Описание} автора стоп-фактора
     */
    public String getStopFactorType() {
        return this.stopFactorType;
    }

    /**
     * Устанавливает {@link String описание} автора стоп-фактора
     * 
     * @param stopFactorType {@link String описание} автора стоп-фактора
     */
    public void setStopFactorType(String stopFactorType) {
        this.stopFactorType = stopFactorType;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return " StopFactorType ID: " + getIdStopFactorType() + " StopFactorType name:"  + getStopFactorType();
    }

}
