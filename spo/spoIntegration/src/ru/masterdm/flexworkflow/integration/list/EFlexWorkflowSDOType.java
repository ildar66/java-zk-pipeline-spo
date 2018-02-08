package ru.masterdm.flexworkflow.integration.list;

import commonj.sdo.DataObject;

/**
 * Список допустимых типов {@link DataObject SDO}
 * 
 * @author imatushak@masterdm.ru
 */
public enum EFlexWorkflowSDOType {

	/**
	 * Тип <b>faultType</b>. Описание ошибки
	 */
	FAULT("faultType", EFlexWorkflowXSDResource.FAULT_TYPE_XSD),

	/**
	 * Тип <b>task4RatingType</b>. Сделка
	 */
	TASK_4_RATING("task4RatingType", EFlexWorkflowXSDResource.OUTPUT_TYPE_XSD),

	/**
	 * Тип <b>supply4RatingType</b>. Сделка
	 */
	SUPPLY_4_RATING("supply4RatingType",
			EFlexWorkflowXSDResource.OUTPUT_TYPE_XSD),

	/**
	 * Тип <b>task4RatingListType</b>. Список сделок
	 */
	TASK_4_RATING_LIST("task4RatingListType",
			EFlexWorkflowXSDResource.OUTPUT_TYPE_XSD),

	/**
	 * Тип <b>mdTaskIdFilter</b>. Номер сделки расчета рейтинга
	 */
	MD_TASK_ID_FILTER("mdTaskIdFilterType",
			EFlexWorkflowXSDResource.INPUT_TYPE_XSD),

	/**
	 * Тип <b>organizationIdFilterType</b>. Номер организации расчета рейтинга
	 */
	ORGANIZATION_ID_FILTER("organizationIdFilterType",
			EFlexWorkflowXSDResource.INPUT_TYPE_XSD),

	/**
	 * Тип <b>CCStatusType</b>. Статус заявки в КК
	 */
	CC_STATUS("CCStatusType", EFlexWorkflowXSDResource.INPUT_TYPE_XSD),

	/**
	 * Тип <b>voidType</b>. Пустой тип данных
	 */
	VOID("voidType", EFlexWorkflowXSDResource.OUTPUT_TYPE_XSD);

	private String value;

	private EFlexWorkflowXSDResource xsd;

	/**
	 * Конструктор
	 * 
	 * @param value
	 *            значение типа
	 * @param xsd
	 *            {@link EFlexWorkflowXSDResource ресурс}
	 */
	EFlexWorkflowSDOType(String value, EFlexWorkflowXSDResource xsd) {
		this.value = value;
		this.xsd = xsd;
	}

	/**
	 * Возвращает значение типа
	 * 
	 * @return значение типа
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Возвращает {@link EFlexWorkflowXSDResource ресурс}
	 * 
	 * @return {@link EFlexWorkflowXSDResource ресурс}
	 */
	public EFlexWorkflowXSDResource getXSD() {
		return xsd;
	}
}