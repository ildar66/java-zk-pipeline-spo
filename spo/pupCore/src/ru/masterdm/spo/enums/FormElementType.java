package ru.masterdm.spo.enums;

/**
 * Типы элементов формы, которые считываются из поля "дополнительно" из атрибутов ПУП 
 * 
 * @author Sergey
 */
public enum FormElementType {
	/**
	 * Кнопка
	 */
	BUTTON(0),
	
	/**
	 * Чекбокс
	 */
	CHECK(1);
	
	private int type;
	
	/**
	 * Конструктор
	 * 
	 * @param type значение типа элемента формы
	 */
	FormElementType(int type) {
		this.type = type;
	}
	
	/**
	 * Возвращает значение типа элемента формы
	 * 
	 * @return значение типа элемента формы
	 */
	public int getType() {
		return type;
	}

	/**
	 * Устанавливает значение типа элемента формы
	 * 
	 * @param type значение типа элемента формы
	 */
	private void setType(int type) {
		this.type = type;
	}
	
	/**
	 * Проверяет тождество значения типа элемента формы 
	 * 
	 * @param type значения типа элемента формы 
	 * @return <code>true</code> - если тождество подтверждено
	 */
	public boolean equals(String type) {
		return (this.type == Integer.parseInt(type)); 
	}
}
