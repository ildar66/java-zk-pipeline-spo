package ru.md.spo.dbobjects;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
/**
 * Настройки. 
 * @author Andrey Pavlenko
 */
@Entity
@Table(name = "global_settings")
public class GlobalSettingsJPA implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id @Column(name = "MNEMO")
    private String id;
    private String value;
    private String description;
    private String system;
    @Override
    public String toString() {
        return "GlobalSettingsJPA [id=" + id + ", value=" + value + "]";
    }
    /**
     * @return текстовый id
     */
    public String getId() {
        return id;
    }
    /**
     * @param id текстовый id
     */
    public void setId(String id) {
        this.id = id;
    }
    /**
     * @return Значение в виде строки
     */
    public String getValue() {
        return value;
    }
    /**
     * @param value Значение в виде строки
     */
    public void setValue(String value) {
        this.value = value;
    }
    /**
     * @return Описание поля
     */
    public String getDescription() {
        return description;
    }
    /**
     * @param description Описание поля
     */
    public void setDescription(String description) {
        this.description = description;
    }
    /**
     * @return Принадлежность к системе (СКО, СПО, МАК). Нужна для разделения прав видимости
     */
    public String getSystem() {
        return system;
    }
    /**
     * @param system Принадлежность к системе (СКО, СПО, МАК). Нужна для разделения прав видимости
     */
    public void setSystem(String system) {
        this.system = system;
    }
    
    
}
