package ru.md.dict.dbobjects;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Таблица модулей системы.
 * @author Andrey Pavlenko плагиат
 */
@Entity
@Table(name="CD_SYSTEM_MODULE")
public class SystemModuleJPA implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id_system_module")
	private Long id;
	
	@Column(name = "key")
	private String key;

	@Column(name = "name")
	private String name;
	
	@Column(name = "SHORT_NAME")
	private String shortName;

	@Column(name = "CURRENT_VERSION")
	private String currentVersion;
	/**
	 * Конструктор
	 */
	public SystemModuleJPA() {
		super();
	}

	/**
	 * Id of JPA
	 * @return
	 */
    public Long getId() {
        return id;
    }

    /**
     * Id of JPA
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getCurrentVersion() {
		return currentVersion;
	}

	public void setCurrentVersion(String currentVersion) {
		this.currentVersion = currentVersion;
	}
	
}
