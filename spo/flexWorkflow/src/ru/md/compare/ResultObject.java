package ru.md.compare;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Класс для отображения результатов сравнения по 1 объекту
 * @author rislamov
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ResultObject {
	@XmlAttribute(name = "id")
	Long objectId;
	@XmlElement(name = "resultElement")
	ArrayList<ResultElement> results;

	/**
	 * Конструктор
	 */
	public ResultObject() {
		results = new ArrayList<ResultElement>();
	}

	/**
	 * Возвращает идентификатор исходного объекта
	 * @return идентификатор исходного объекта
	 */
	public Long getObjectId() {
		return objectId;
	}

	/**
	 * Устанавливает идентификатор исходного объекта
	 * @param objectId идентификатор исходного объекта
	 */
	public void setObjectId(Long objectId) {
		this.objectId = objectId;
	}

	/**
	 * Возвращает результаты сравнения по 1 объекту
	 * @return результаты сравнения по 1 объекту
	 */
	public ArrayList<ResultElement> getResults() {
		return results;
	}

}