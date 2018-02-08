package ru.md.domain;

public class StatusReturn  {
	private String id;//Уникальный идентификатор справочника
	private String type;//Признак типа принятого окончательного решения
	private String description;//Значение статуса возврата заявки
	public String toStringAlternative(){
		return "id="+id+" "+description;
	}
	/**
	 * @return Уникальный идентификатор справочника
	 */
	public String getId() {
		return id;
	}
	public StatusReturn(String id, String type, String description) {
		super();
		this.id = id;
		this.type = type;
		this.description = description;
	}
	
	public StatusReturn() {
		super();
	}
	/**
	 * @param Уникальный идентификатор справочника
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return Признак типа принятого окончательного решения.
	 * 0 - отказ. 1 - принято.
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param Признак типа принятого окончательного решения
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return Значение статуса возврата заявки
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param Значение статуса возврата заявки
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
}
