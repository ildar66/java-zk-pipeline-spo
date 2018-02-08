package ru.md.domain;

import java.io.Serializable;

/**
 * Справочник Надбавка к процентной ставке
 * 
 * @author akirilchev@masterdm.ru
 */
public class ExtraChargeRate implements Serializable {

	/**
	 * @serial
	 */
	private static final long serialVersionUID = 1L;

	private Long id;
	private String name;

	/**
	 * Возвращает {@link Long первичный ключ}
	 * 
	 * @return {@link Long первичный ключ}
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Устанавливает {@link Long первичный ключ}
	 * 
	 * @param id {@link Long первичный ключ}
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Возвращает наименование
	 * 
	 * @return наименование
	 */
	public String getName() {
		return name;
	}

	/**
	 * Устанавливает наименование
	 * 
	 * @param name наименование
	 */
	public void setName(String name) {
		this.name = name;
	}

}
