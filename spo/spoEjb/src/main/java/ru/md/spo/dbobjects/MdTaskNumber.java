package ru.md.spo.dbobjects;

import java.io.Serializable;
import java.util.HashSet;

/**
 * Номер заявки
 * @author Andrey Pavlenko
 */
public class MdTaskNumber implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long id;
	private String number;
	private HashSet<String> orgRole;
	
	public MdTaskNumber() {
		super();
	}
	/**
	 * Возвращает id_mdtask.
	 * @return idid_mdtask
	 */
	public Long getId() {
		return id;
	}
	/**
	 * Устанавливает id_mdtask.
	 * @param id id_mdtask
	 */
	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * Возвращает отображаемый номер.
	 * @return отображаемый номер
	 */
	public String getNumber() {
		return number;
	}
	/**
	 * Устанавливает отображаемый номер.
	 * @param number отображаемый номер
	 */
	public void setNumber(String number) {
		this.number = number;
	}
	/**
	 * @param id id_mdtask
	 * @param number отображаемый номер
	 */
	public MdTaskNumber(Long id, String number) {
		super();
		this.id = id;
		this.number = number;
		this.orgRole = new HashSet<String>();
	}
	/**
	 * @return в качестве кого выступает контрагент по данной сделке, перечень значений через запятую
	 */
	public String getOrgRole() {
		String res = "";
		for(String s : orgRole){
			if(!res.isEmpty())
				res += ", ";
			res += s;
		}
		return res;
	}
	public HashSet<String> getOrgRoleHash() {
		return orgRole;
	}
}
