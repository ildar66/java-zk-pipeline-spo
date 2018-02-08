package ru.masterdm.flexworkflow.integration.list;

/**
 * Список XSD ресурсов
 * 
 * @author imatushak@masterdm.ru
 */
public enum EFlexWorkflowXSDResource {

	/**
	 * Ресурс <b>flexWorkflowFault.xsd</b>
	 */
	FAULT_TYPE_XSD("flexWorkflowFault.xsd"),

	/**
	 * Ресурс <b>flexWorkflowInput.xsd</b>
	 */
	INPUT_TYPE_XSD("flexWorkflowInput.xsd"),

	/**
	 * Ресурс <b>flexWorkflowOutput.xsd</b>
	 */
	OUTPUT_TYPE_XSD("flexWorkflowOutput.xsd");

	private String resource;

	/**
	 * Конструктор
	 * 
	 * @param resource
	 *            ресурс
	 */
	EFlexWorkflowXSDResource(String resource) {
		this.resource = resource;
	}

	/**
	 * Возвращает ресурс
	 * 
	 * @return ресурс
	 */
	public String getResource() {
		return resource;
	}
}