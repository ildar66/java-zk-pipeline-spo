package ru.md.domain.dict;


/**
 * Cправочник.
 */
public class CrossSell {

	private Long id;
	private String name;

	/**
	 * Возвращает идентификатор.
	 * @return идентификатор
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Устанавливает идентификатор.
	 * @param id идентификатор
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Возвращает наименование.
	 * @return наименование
	 */
	public String getName() {
		return name;
	}

	/**
	 * Устанавливает наименование.
	 * @param name наименование
	 */
	public void setName(String name) {
		this.name = name;
	}
}
