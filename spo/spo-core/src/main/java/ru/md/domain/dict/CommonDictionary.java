package ru.md.domain.dict;


/**
 * Cправочник.
 * @param <T> тип ключа
 * @author Sergey Valiev
 */
public class CommonDictionary<T> {

	private T id;
	private String name;

	/**
	 * Возвращает идентификатор.
	 * @return идентификатор
	 */
	public T getId() {
		return id;
	}

	/**
	 * Устанавливает идентификатор.
	 * @param id идентификатор
	 */
	public void setId(T id) {
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
