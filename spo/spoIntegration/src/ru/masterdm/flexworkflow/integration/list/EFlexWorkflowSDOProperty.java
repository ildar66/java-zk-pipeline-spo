package ru.masterdm.flexworkflow.integration.list;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import commonj.sdo.DataObject;

/**
 * Список xPath к свойствам {@link DataObject SDO} допустимых
 * {@link EFlexWorkflowSDOType типов}
 * 
 * @author imatushak@masterdm.ru
 */
public enum EFlexWorkflowSDOProperty {

	/**
	 * Свойство <b>exceptionClass</b> {@link DataObject SDO}
	 * {@link ESDOType#FAULT} типа {@link String}
	 */
	FAULT_EXCEPTION_CLASS("exceptionClass", EFlexWorkflowSDOType.FAULT),

	/**
	 * Свойство <b>message</b> {@link DataObject SDO} {@link ESDOType#FAULT}
	 * типа {@link String}
	 */
	FAULT_MESSAGE("message", EFlexWorkflowSDOType.FAULT),

	/**
	 * Свойство <b>stackTrace</b> {@link DataObject SDO} {@link ESDOType#FAULT}
	 * типа {@link String}
	 */
	FAULT_STACK_TRACE("stackTrace", EFlexWorkflowSDOType.FAULT),

	/**
	 * Свойство <b>mdTaskId</b> {@link DataObject SDO}
	 * {@link EFlexWokflowSDOType#MD_TASK_ID_FILTER} типа {@link long}
	 */
	MD_TASK_ID("mdTaskId", EFlexWorkflowSDOType.MD_TASK_ID_FILTER),

	/**
	 * Свойство <b>organizationId</b> {@link DataObject SDO}
	 * {@link EFlexWokflowSDOType#ORGANIZATION_ID_FILTER} типа {@link String}
	 */
	ORGANIZATION_ID("organizationId",
			EFlexWorkflowSDOType.ORGANIZATION_ID_FILTER),

	/**
	 * Свойство <b>idMdTask</b> {@link DataObject SDO}
	 * {@link EFlexWokflowSDOType#TASK_4_RATING} типа {@link long}
	 */
	TASK_4_RATING_ID_MD_TASK("idMdTask", EFlexWorkflowSDOType.TASK_4_RATING),

	/**
	 * Свойство <b>numberDisplay</b> {@link DataObject SDO}
	 * {@link EFlexWokflowSDOType#TASK_4_RATING} типа {@link String}
	 */
	TASK_4_RATING_NUMBER_DISPLAY("numberDisplay",
			EFlexWorkflowSDOType.TASK_4_RATING),

	/**
	 * Свойство <b>sum</b> {@link DataObject SDO}
	 * {@link EFlexWokflowSDOType#TASK_4_RATING} типа {@link BigDecimal}
	 */
	TASK_4_RATING_SUM("sum", EFlexWorkflowSDOType.TASK_4_RATING),

	/**
	 * Свойство <b>period</b> {@link DataObject SDO}
	 * {@link EFlexWokflowSDOType#TASK_4_RATING} типа {@link Integer}
	 */
	TASK_4_RATING_PERIOD("period", EFlexWorkflowSDOType.TASK_4_RATING),

	/**
	 * Свойство <b>rateType</b> {@link DataObject SDO}
	 * {@link EFlexWokflowSDOType#TASK_4_RATING} типа {@link boolean}
	 */
	TASK_4_RATING_RATE_TYPE("rateType", EFlexWorkflowSDOType.TASK_4_RATING),

	/**
	 * Свойство <b>operationTypeCode</b> {@link DataObject SDO}
	 * {@link EFlexWokflowSDOType#TASK_4_RATING} типа {@link Long}
	 */
	TASK_4_RATING_OPERATION_TYPE_CODE("operationTypeCode",
			EFlexWorkflowSDOType.TASK_4_RATING),

	/**
	 * Свойство <b>supplyList</b> {@link DataObject SDO}
	 * {@link EFlexWokflowSDOType#TASK_4_RATING} типа {@link List}
	 */
	TASK_4_RATING_SUPPLY_LIST("supplyList", EFlexWorkflowSDOType.TASK_4_RATING),

	/**
	 * Свойство <b>typeCode</b> {@link DataObject SDO}
	 * {@link EFlexWokflowSDOType#SUPPLY_4_RATING} типа {@link String}
	 */
	SUPPLY_4_RATING_TYPE_CODE("typeCode", EFlexWorkflowSDOType.SUPPLY_4_RATING),

	/**
	 * Свойство <b>sum</b> {@link DataObject SDO}
	 * {@link EFlexWokflowSDOType#SUPPLY_4_RATING} типа {@link BigDecimal}
	 */
	SUPPLY_4_RATING_SUM("sum", EFlexWorkflowSDOType.SUPPLY_4_RATING),

	/**
	 * Свойство <b>depositorFinStatusCode</b> {@link DataObject SDO}
	 * {@link EFlexWokflowSDOType#SUPPLY_4_RATING} типа {@link Long}
	 */
	SUPPLY_4_RATING_DEPOSITOR_FIN_STATUS_CODE("depositorFinStatusCode",
			EFlexWorkflowSDOType.SUPPLY_4_RATING),

	/**
	 * Свойство <b>liquidityLevelCode</b> {@link DataObject SDO}
	 * {@link EFlexWokflowSDOType#SUPPLY_4_RATING} типа {@link Long}
	 */
	SUPPLY_4_RATING_LIQUIDITY_LEVEL_CODE("liquidityLevelCode",
			EFlexWorkflowSDOType.SUPPLY_4_RATING),

	/**
	 * Свойство <b>supplyTypeCode</b> {@link DataObject SDO}
	 * {@link EFlexWokflowSDOType#SUPPLY_4_RATING} типа {@link Long}
	 */
	SUPPLY_4_RATING_SUPPLY_TYPE_CODE("supplyTypeCode",
			EFlexWorkflowSDOType.SUPPLY_4_RATING),

	/**
	 * Свойство <b>task4RatingList</b> {@link DataObject SDO}
	 * {@link EFlexWorkflowSDOType#TASK_4_RATING_LIST} типа {@link List}
	 */
	TASK_4_RATING_LIST("task4RatingList",
			EFlexWorkflowSDOType.TASK_4_RATING_LIST),

	/**
	 * Свойство <b>"mdTaskId"</b> {@link DataObject SDO}
	 * {@link EFlexWokflowSDOType#CC_STATUS} типа {@link Long}
	 */
	CC_STATUS_MD_TASK_ID("mdTaskId", EFlexWorkflowSDOType.CC_STATUS),

	/**
	 * Свойство <b>"ccResolutionStatusId"</b> {@link DataObject SDO}
	 * {@link EFlexWokflowSDOType#CC_STATUS} типа {@link Long}
	 */
	CC_STATUS_RESOLUTION_STATUS_ID("ccResolutionStatusId",
			EFlexWorkflowSDOType.CC_STATUS),

	/**
	 * Свойство <b>"meetingDate"</b> {@link DataObject SDO}
	 * {@link EFlexWokflowSDOType#CC_STATUS} типа {@link Date}
	 */
	CC_STATUS_MEETING_DATE("meetingDate", EFlexWorkflowSDOType.CC_STATUS),

	/**
	 * Свойство <b>"protocolId"</b> {@link DataObject SDO}
	 * {@link EFlexWokflowSDOType#CC_STATUS} типа {@link String}
	 */
	CC_STATUS_PROTOCOL_ID("protocolId", EFlexWorkflowSDOType.CC_STATUS),

	/**
	 * Свойство <b>"reportId"</b> {@link DataObject SDO}
	 * {@link EFlexWokflowSDOType#CC_STATUS} типа {@link Long}
	 */
	CC_STATUS_REPORT_ID("reportId", EFlexWorkflowSDOType.CC_STATUS);

	private String xPath;

	private EFlexWorkflowSDOType type;

	/**
	 * Конструктор
	 * 
	 * @param xPath
	 *            xPath к свойству {@link DataObject SDO}
	 * @param type
	 *            допустимый {@link EFlexWorkflowSDOType тип} {@link DataObject
	 *            SDO}
	 */
	EFlexWorkflowSDOProperty(String xPath, EFlexWorkflowSDOType type) {
		this.xPath = xPath;
		this.type = type;
	}

	/**
	 * Возвращает xPath к свойству {@link DataObject SDO}
	 * 
	 * @return xPath к свойству {@link DataObject SDO}
	 */
	public String getXPath() {
		return xPath;
	}

	/**
	 * Возвращает допустимый {@link EFlexWorkflowSDOType тип} {@link DataObject
	 * SDO}
	 * 
	 * @return допустимый {@link EFlexWorkflowSDOType тип} {@link DataObject
	 *         SDO}
	 */
	public EFlexWorkflowSDOType getType() {
		return type;
	}
}