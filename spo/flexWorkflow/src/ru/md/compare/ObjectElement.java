package ru.md.compare;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

import com.vtb.util.Formatter;

/**
 * Сущность для сравнения
 * @author rislamov
 */
public class ObjectElement {

	String htmlList = null;
	String value = null;
	String htmlName = null;
	LinkedHashMap<String, ObjectElement> structure = new LinkedHashMap<String, ObjectElement>();

	/**
	 * Конструктор
	 */
	public ObjectElement() {}

	/**
	 * Конструктор со значением
	 * @param val значение
	 */
	public ObjectElement(String val) {
		setValue(val);
	}

	/**
	 * Конструктор со значением и компонентом
	 * @param val значение
	 * @param htmlName имя html-компонента
	 */
	public ObjectElement(String val, String htmlName) {
		setValue(val);
		setHtmlName(htmlName);
	}

	/**
	 * Конструктор со значением и компонентом
	 * @param val значение
	 * @param htmlName имя html-компонента
	 * @param list родительский список
	 */
	public ObjectElement(String val, String htmlName, String list) {
		setValue(val);
		setHtmlName(htmlName);
		setHtmlList(list);
	}

	/**
	 * Проверка наличия значения
	 * @return есть значение
	 */
	public boolean hasValue() {
		return value != null;
	}

	/**
	 * Возвращает значение
	 * @return значение
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Устанавливает значение
	 * @param value значение
	 */
	public void setValue(String value) {
		this.value = Formatter.str(value).trim();
	}

	/**
	 * Возвращает дочернюю структуру
	 * @return дочерняя структура
	 */
	public LinkedHashMap<String, ObjectElement> getStructure() {
		return structure;
	}

	/**
	 * Возвращает {@link String} имя html-компонента
	 * @return {@link String} имя html-компонента
	 */
	public String getHtmlName() {
		return htmlName;
	}

	/**
	 * Устанавливает {@link String} имя html-компонента
	 * @param htmlName {@link String} имя html-компонента
	 */
	public void setHtmlName(String htmlName) {
		this.htmlName = htmlName;
	}

	/**
	 * Возвращает {@link String} имя компонента для вывода родительского списка
	 * @return {@link String} имя компонента для вывода родительского списка
	 */
	public String getHtmlList() {
		return htmlList;
	}

	/**
	 * Устанавливает {@link String} имя компонента для вывода родительского списка
	 * @param htmlList {@link String} имя компонента для вывода родительского списка
	 */
	public void setHtmlList(String htmlList) {
		this.htmlList = htmlList;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return toString("");
	}

	private String toString(String prefix) {
		String res = prefix + "value=" + value + ",  structure={\n";
		if (structure != null && structure.size() > 0)
			for (Entry<String, ObjectElement> e : structure.entrySet())
				res += prefix + "key=" + e.getKey() + ", value=\n" + e.getValue().toString(prefix + "\t");
		res += prefix + "}\n";
		return res;
	}

}
