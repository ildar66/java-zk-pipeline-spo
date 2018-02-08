package org.uit.director.tasks;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.uit.director.db.dbobjects.AttributeStruct;
import org.uit.director.db.dbobjects.BasicAttribute;

public class AttributesStructList implements Serializable {

	private static final Logger LOGGER = Logger.getLogger(AttributesStructList.class.getName());
	private static final long serialVersionUID = 1L;

	private List<BasicAttribute> attrStructures;	

	/*
	 * public AttributesList(List attrStructures) { this.attributes =
	 * attrStructures; }
	 */

	public AttributesStructList() {

		attrStructures = new ArrayList<BasicAttribute>();

	}

	public AttributesStructList(List<BasicAttribute> _attributes) {

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
	public List<BasicAttribute> getAttributesOrder() {

		List<BasicAttribute> res = new ArrayList<BasicAttribute>();
		res.addAll(attrStructures);
		
//		Collections.sort(res, new AttributesStructComparator());
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

		 for (BasicAttribute attribute : attrStructures) {				
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

		 for (BasicAttribute attribute : attrStructures) {		
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

		 for (BasicAttribute attr : attrStructures) {

			if (attr.getAttribute().getNameVariable().equalsIgnoreCase(nameVar)) {
				attr.getAttribute().setValueAttribute(valueVar);
				return;
			}

		}

	}
	
	/**
	 * Установить значение переменной с именем nameVar на valueVar
	 * 
	 * @param nameVar
	 * @param valueVar
	 */
	public void setValue(String nameVar, String valueVar) {

		 for (BasicAttribute attr : attrStructures) {

			if (attr.getAttribute().getNameVariable().equalsIgnoreCase(nameVar)) {
				attr.getAttribute().setValueAttributeStr(valueVar);
				return;
			}

		}

	}

	public BasicAttribute findAttributeByName(String name) {
		 for (BasicAttribute attribute : attrStructures) {
			 String attrname=attribute.getAttribute().getNameVariable();
			 //LOGGER.info(attrname);
			if (attrname.equalsIgnoreCase(name)) {
				return attribute;
			}
		}

		return null;
	}

	/**
	 * Получить главные атрибуты процесса 
	 */
	@SuppressWarnings("unchecked")
	public List<BasicAttribute> getMainAttributes() {

		List<BasicAttribute> res = new ArrayList<BasicAttribute>();

		for (BasicAttribute as:  attrStructures) {
			
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
	public List<BasicAttribute> getIdenticalAttributes() {

		List<BasicAttribute> res = new ArrayList<BasicAttribute>();

		for (BasicAttribute as:  attrStructures) {
			
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

	public List<BasicAttribute> getAttrStructures() {
		return attrStructures;
	}

	public void setAttrStructures(List<BasicAttribute> attrStructures) {
		this.attrStructures = attrStructures;
	}
	
	public Iterator<BasicAttribute> getIterator() {
		return attrStructures.iterator(); 
	}

	public BasicAttribute findAttributeById(Long idVarPar) {
		BasicAttribute res = null;
		
		for (BasicAttribute atStr: attrStructures) {
			if (atStr.getAttribute().getIdVariable().longValue() == idVarPar.longValue()) {
				return atStr;
			}
		}
		
		return res;
		
	}
	
	@Override
	public String toString() {
        return attrStructures == null ? null : attrStructures.toString();
    }
	
	
}
