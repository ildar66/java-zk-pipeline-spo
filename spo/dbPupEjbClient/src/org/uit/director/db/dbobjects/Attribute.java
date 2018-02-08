package org.uit.director.db.dbobjects;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.uit.director.db.dbobjects.VarsPermissions.Permission;

public class Attribute extends WorkflowVariables {
    private static final Logger LOGGER = Logger.getLogger(WorkflowVariables.class.getName());
	private static final long serialVersionUID = 1L;

	ArrayList<String> valueAttribute;

	HashSet<VarsPermissions.Permission> permisions;

	String date_time_db_format;

	String date_time_format;

	String date_format;

	
	
	public Attribute() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * @param nameAttribute
	 * @param valueAttribute
	 * @param typeAttribute
	 * @param addition
	 * @param date_time_db_format_
	 * @param date_time_format_
	 * @param date_format_
	 * @param is_id_
	 * @param is_main_
	 */
	public Attribute(ArrayList<String> valueAttribute,
			HashSet<VarsPermissions.Permission> permisions,
			String date_time_db_format_, String date_time_format_,
			String date_format_) {

		super();

		date_time_db_format = date_time_db_format_;
		date_time_format = date_time_format_;
		date_format = date_format_;
		this.permisions = permisions;

		/*
		 * if (this.typeVar == VariablesType.DATE) { SimpleDateFormat dateFormat =
		 * new SimpleDateFormat( date_time_db_format); try { Date date =
		 * dateFormat.parse((String) valueAttribute); SimpleDateFormat
		 * dateFormatCorrect = new SimpleDateFormat( date_time_format);
		 * this.valueAttribute = dateFormatCorrect.format(date); } catch
		 * (ParseException e) { this.valueAttribute = valueAttribute; } } else
		 */
		this.valueAttribute = valueAttribute;

	}

	public String getValueAttributeString() {
		if (valueAttribute != null) {
			return getStringViewForList();
		}
		return null;
	}

	private String getStringViewForList() {
		StringBuilder str = new StringBuilder(); 
		Iterator<String> it = valueAttribute.iterator();
		while(it.hasNext()) {
			String singleValue = it.next();
			singleValue = singleValue != null ? singleValue.trim() : "";
			str.append(singleValue);
			if (it.hasNext() && !singleValue.equals("")) {
				str.append(", ");
			}
		}
		return str.toString();
	}
	
	public Integer getValueAttributeInteger() {
		if (valueAttribute != null && valueAttribute.size() == 1) {

			try {
				return Integer.valueOf(valueAttribute.get(0));
			} catch (NumberFormatException e) {

			}
		}
		return null;
	}

	public Long getValueAttributeLong() {
		if (valueAttribute != null && valueAttribute.size() == 1) {

			try {
				return Long.valueOf(valueAttribute.get(0));
			} catch (NumberFormatException e) {

			}
		}
		return null;
	}

	public Float getValueAttributeFloat() {

		if (valueAttribute != null && valueAttribute.size() == 1) {
			try {
				return Float.valueOf(valueAttribute.get(0));
			} catch (NumberFormatException e) {

			}
		}
		return null;
	}

	/**
	 * возвращает значение атрибута.
	 * @return
	 */
	public Boolean getValueAttributeBoolean() {
		if (valueAttribute != null && valueAttribute.size() == 1) {

			String val = valueAttribute.get(0);
			if (val.equals("0") || val.equalsIgnoreCase("false")) {
				return false;
			}
			if (val.equals("1") || val.equalsIgnoreCase("true")) {
				return true;
			}

			try {

				return Boolean.parseBoolean(val);
			} catch (NumberFormatException e) {

			}
		}
		LOGGER.log(Level.WARNING, "WorkflowVariables attr "+this.name+" have not value. Use false");
		return false;
	}

	public Date getValueAttributeDate() {

		if (typeVar.value == VariablesType.DATE && valueAttribute != null
				&& valueAttribute.size() == 1) {
			SimpleDateFormat dateFormat = new SimpleDateFormat(
					date_time_db_format);
			try {
				Date date = dateFormat.parse(valueAttribute.get(0));
				return date;
			} catch (ParseException e) {
			}
		}

		return null;
	}

	// Получить строковое представление даты со временем в формате приложения
	// (не базы)
	public String getValueAttributeDateTimeString() {

		if (typeVar.value == VariablesType.DATE && valueAttribute != null
				&& valueAttribute.size() == 1) {

			Date date = getValueAttributeDate();
			if (date != null) {

				SimpleDateFormat dateFormatCorrect = new SimpleDateFormat(
						date_time_format);
				return dateFormatCorrect.format(date);
			}

		}

		return null;
	}

	// Получить строковое представление даты в формате приложения (не базы)
	public String getValueAttributeDateString() {

		if (typeVar.value == VariablesType.DATE && valueAttribute != null
				&& valueAttribute.size() == 1) {

			Date date = getValueAttributeDate();
			if (date != null) {

				SimpleDateFormat dateFormatCorrect = new SimpleDateFormat(
						date_format);
				return dateFormatCorrect.format(date);
			}

		}

		return null;
	}

	public List<String> getValueAttributeList() {
		return valueAttribute;
	}

	public void setValueAttributeStr(String valueAttribute) {
		if (this.valueAttribute == null) {
			this.valueAttribute = new ArrayList<String>();
		}
		this.valueAttribute.clear();
		this.valueAttribute.add(valueAttribute);
	}

	public boolean isArray() {
		if (valueAttribute != null && valueAttribute.size() > 1) {
			return true;
		}
		return false;
	}

	public void setWorkflowVariable(WorkflowVariables var) {

		id = var.getId();
		name = var.getName();
		description = var.getDescription();
		typeVar = var.getTypeVar();
		addition = var.getAddition();
		isId = var.isId();
		isMain = var.isMain();
		idTypeProcess = var.getIdTypeProcess();
		isActive = var.isActive();
		options = var.getOptions();

	}

	public HashSet<Permission> getPermision() {
		return permisions;
	}

	public void setPermision(HashSet<Permission> permision) {
		permisions = permision;
	}

	public boolean isPermissionMainView() {

		HashSet<Permission> permision = getPermision();
		Iterator<Permission> it = permision.iterator();
		while (it.hasNext()) {
			Permission next = it.next();
			if (next.value.equalsIgnoreCase(Permission.VIEW_MAIN))
				return true;
		}

		/*
		 * if (getPermision().contains(Permission.VIEW_MAIN)) { return true; }
		 */
		return false;
	}

	public boolean isPermissionAdditionView() {

		HashSet<Permission> permision = getPermision();
		Iterator<Permission> it = permision.iterator();
		while (it.hasNext()) {
			Permission next = it.next();
			if (next.value.equalsIgnoreCase(Permission.VIEW_ADDITION))
				return true;
		}

		/*
		 * if (getPermision().contains(Permission.VIEW_ADDITION)) { return true; }
		 */
		return false;
	}

	public boolean isPermissionEdit() {

		HashSet<Permission> permision = getPermision();
		Iterator<Permission> it = permision.iterator();
		while (it.hasNext()) {
			Permission next = it.next();
			if (next.value.equalsIgnoreCase(Permission.EDIT))
				return true;
		}
		/*
		 * if (permision.contains(Permission.EDIT)) { return true; }
		 */
		return false;
	}

	public void setValueAttribute(ArrayList<String> valueVar) {
		valueAttribute = valueVar;

	}

	@Override
    public String toString() {
        return valueAttribute == null ? null : getNameVariable()+": "
        		+valueAttribute.toString();
    }
}
