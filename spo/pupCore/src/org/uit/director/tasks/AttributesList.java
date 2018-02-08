package org.uit.director.tasks;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.uit.director.db.dbobjects.AttributeStruct;

public class AttributesList implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<AttributeStruct> attrStructures;

	/*
	 * public AttributesList(List attrStructures) { this.attributes =
	 * attrStructures; }
	 */

	public AttributesList() {

		attrStructures = new ArrayList<AttributeStruct>();

	}

	public AttributesList(List<AttributeStruct> _attributes) {

		attrStructures = _attributes;

	}

	private class AttributesStructComparator implements Comparator {		
		
		public int compare(Object o1, Object o2) {
			AttributeStruct a1 = (AttributeStruct) o1;
			AttributeStruct a2 = (AttributeStruct) o2;

			return a1.getAttribute().getOrderVar() > a2.getAttribute()
					.getOrderVar() ? 1 : 0;

		}

	}

	/**
	 * Получить список атрибутов в определенном порядке по имени атрибута ,
	 * например: name,insmbr,type_pension
	 * 
	 * @param orderByName
	 * @return res
	 */
	@SuppressWarnings("unchecked")
	public List<AttributeStruct> getAttributesOrder() {

		List<AttributeStruct> res = new ArrayList<AttributeStruct>();
		res.addAll(attrStructures);
		
		Collections.sort(res, new AttributesStructComparator());
		return res;

	}

	public int getIndexForName(String name) {

		int count = attrStructures.size();
		for (int i = 0; i < count; i++) {
			if (attrStructures.get(i).getAttribute().getNameVariable()
					.equalsIgnoreCase(name)) {
				return i;
			}
		}

		return -1;
	}

	public Object getValueByName(String name) {

		 for (AttributeStruct attribute : attrStructures) {				
			if (attribute.getAttribute().getNameVariable().equalsIgnoreCase(name)) {
				return attribute.getAttribute().getValueAttributeString();
			}
		}
		return null;
	}

	public String getStringValueByName(String name) {

		Object res = getValueByName(name);
		if (res instanceof String) {
			return (String) res;
		}

		return "";
	}

	public String getAdditionByName(String name) {

		 for (AttributeStruct attribute : attrStructures) {		
			if (attribute.getAttribute().getNameVariable().equalsIgnoreCase(name)) {
				return attribute.getAttribute().getAddition();
			}
		}
		return null;
	}

	/**
	 * 
	 * @param attrStructures
	 * @param isAllAttributes -
	 *            true - все атрибуты (с общими), false - атрибуты, определенные
	 *            в схеме
	 */
	/*public void setAttributes(List attributes, boolean isAllAttributes) {
		this.attrStructures = attributes;
		String nameType = findAttributeByName("Тип процесса")
				.getrValueAttributeString();
		String idType = WPC.getInstance().getIdTypeProcessByDescription(
				nameType);

		order = WPC.getInstance().getProcessParameter(idType, "ORDER");
		if (!isAllAttributes) {
			List dv = WPC.getInstance().directVars;
			for (int i = 0; i < dv.size(); i++) {
				order = order.replaceFirst((String) dv.get(i), "");
			}

		}
		Collections.sort(this.attrStructures, new AttributesStructComparator(order));

	}
*/
	public int size() {
		return attrStructures.size();
	}

	/*public Attribute getAttribut(int idx) {
		return attrStructures.get(idx).getAttribute();
	}
*/
	/*public String getTypeByIdx(int i) {	
		return  attrStructures.get(i).getAttribute().getTypeVar();

	}
*/
	/**
	 * Установить значение переменной с именем nameVar на valueVar
	 * 
	 * @param nameVar
	 * @param valueVar
	 */
	public void setValue(String nameVar, ArrayList<String> valueVar) {

		 for (AttributeStruct attr : attrStructures) {

			if (attr.getAttribute().getNameVariable().equalsIgnoreCase(nameVar)) {
				attr.getAttribute().setValueAttribute(valueVar);
				return;
			}

		}

	}

	public AttributeStruct findAttributeByName(String name) {

		 for (AttributeStruct attribute : attrStructures) {
			if (attribute.getAttribute().getNameVariable().equalsIgnoreCase(name)) {
				return attribute;
			}
		}

		return null;
	}

	/**
	 * Получить главные атрибуты процесса 
	 */
	@SuppressWarnings("unchecked")
	public List<AttributeStruct> getMainAttributes() {

		List<AttributeStruct> res = new ArrayList<AttributeStruct>();

		for (AttributeStruct as:  attrStructures) {
			
			if (as.getAttribute().isMain()) {
				res.add(as);
			}
		}
		Collections.sort(res, new AttributesStructComparator());
		return res;
	}
	
	/**
	 * Получить идентификационные атрибуты процесса 
	 */
	@SuppressWarnings("unchecked")
	public List<AttributeStruct> getIdAttributes() {

		List<AttributeStruct> res = new ArrayList<AttributeStruct>();

		for (AttributeStruct as:  attrStructures) {
			
			if (as.getAttribute().isId()) {
				res.add(as);
			}
		}
		Collections.sort(res, new AttributesStructComparator());
		return res;
	}

	public int getCountAttributes() {
		return attrStructures.size();
	}

	public List<AttributeStruct> getAttrStructures() {
		return attrStructures;
	}

	public void setAttrStructures(List<AttributeStruct> attrStructures) {
		this.attrStructures = attrStructures;
	}
	
	public Iterator<AttributeStruct> getIterator() {
		return attrStructures.iterator(); 
	}
	
	
}
