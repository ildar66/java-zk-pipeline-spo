package ru.md.compare;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Одно поле результата сравнения
 * @author rislamov
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ResultElement {
	@XmlAttribute
	boolean wrong;
	@XmlAttribute
	String list;
	String value;
	String key;
	int level;
	String htmlName;

	public ResultElement() {
		super();
	}

	public ResultElement(boolean wrong, String value, int level, String htmlName, String listName) {
		this();
		this.wrong = wrong;
		this.value = value;
		this.level = level;
		this.htmlName = htmlName;
		this.list = listName;
	}

	public boolean isWrong() {
		return wrong;
	}

	public void setWrong(boolean wrong) {
		this.wrong = wrong;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	/**
	 * Возвращает {@link String} имя компонента списка
	 * @return {@link String} имя компонента списка
	 */
	public String getList() {
		return list;
	}

	/**
	 * Устанавливает {@link String} имя компонента списка
	 * @param list {@link String} имя компонента списка
	 */
	public void setList(String list) {
		this.list = list;
	}

}
