package ru.md.compare;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Класс для отображения результатов сравнения
 * @author rislamov
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Result {
	@XmlElementWrapper(name = "results")
	@XmlElement(name = "resultObject")
	ArrayList<ResultObject> results;
	@XmlAttribute
	String blockName;
	@XmlElementWrapper(name = "headers")
	@XmlElement(name = "header")
	ArrayList<String> headers;

	/**
	 * Конструктор
	 */
	public Result() {
		headers = new ArrayList<String>();
		results = new ArrayList<ResultObject>();
	}

	/**
	 * Конструктор
	 * @param objCount количество объектов для сравнения
	 */
	public Result(int objCount) {
		this();
		// первый столбец - имена параметров, поэтому +1
		for (int i = 0; i < objCount + 1; i++)
			results.add(new ResultObject());
	}

	/**
	 * Конструктор
	 * @param ids идентификаторы объектов для сравнения
	 */
	public Result(List<Long> ids) {
		this();
		// первый столбец - имена параметров, поэтому +1
		for (int i = 0; i < ids.size() + 1; i++) {
			ResultObject subRes = new ResultObject();
			subRes.setObjectId(i > 0 ? ids.get(i - 1) : 0);
			results.add(subRes);
		}
	}

	/**
	 * Возвращает {@link String} наименование блока сравнения
	 * @return {@link String} наименование блока сравнения
	 */
	public String getBlockName() {
		return blockName;
	}

	/**
	 * Устанавливает {@link String} наименование блока сравнения
	 * @param blockName {@link String} наименование блока сравнения
	 */
	public void setBlockName(String blockName) {
		this.blockName = blockName;
	}

	/**
	 * Возвращает список заголовков
	 * @return список заголовков
	 */
	public List<String> getHeaders() {
		return headers;
	}

	/**
	 * Результаты
	 * @return результаты : первый столбец - имена атрибутов, второй - основная версия объекта (с
	 * которой сравниваются остальные), последующие - оставшиеся версии для сравнения
	 */
	public ArrayList<ResultObject> getResultObjects() {
		return results;
	}

	/**
	 * Добавление результата к текущему
	 * @param res добавляемый результат
	 * @return текущий объект
	 */
	public Result add(Result res) {
		for (int i = 0; i < results.size(); i++) {
			if (res.getResultObjects().size() > i)
				for (ResultElement r : res.getResultObjects().get(i).getResults())
					results.get(i).getResults().add(r);
		}
		return this;
	}

}