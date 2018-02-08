package com.vtb.domain;

/**
 * {@link VtbObject Доменный объект}, представляющий информационное сообщение с системе
 * 
 * @author svaliev
 * @email svaliev@masterdm.ru
 */
public class InfoMessage extends VtbObject {

	/**
	 * @serial
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer type = null;
	private String body = null;
	
	/**
	 * Конструктор
	 */
	public InfoMessage() {}
	
	/**
	 * Конструктор
	 * 
	 * @param type тип сообщения
	 * @param body сообщение
	 */
	public InfoMessage(Integer type, String body) {
		this.type = type;
		this.body = body;
	}

	/**
	 * Возвращает тип сообщения:<br>
	 * - значение <b>0</b> - ошибка<br>
	 * - значение <b>1</b> - предупреждение<br>
	 * - значение <b>2</b> - информация<br>
	 * 
	 * @return тип сообщения
	 */
	public Integer getType() {
		return type;
	}

	/**
	 * Устанавливает тип сообщения
	 * 
	 * @param type тип сообщения
	 */
	public void setType(Integer type) {
		this.type = type;
	}

	/**
	 * Возвращает сообщение
	 * 
	 * @return сообщение
	 */
	public String getBody() {
		return body;
	}

	/**
	 * Устанавливает сообщение
	 * 
	 * @param body сообщение
	 */
	public void setBody(String body) {
		this.body = body;
	}
}
