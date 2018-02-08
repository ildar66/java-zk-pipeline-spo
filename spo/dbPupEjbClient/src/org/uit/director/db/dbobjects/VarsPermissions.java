/*
 * Created on 23.09.2008
 * 
 */
package org.uit.director.db.dbobjects;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Объект доступа переменных
 * @author Rustam
 *
 */
public class VarsPermissions implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public static class Permission implements Serializable{
		
		private static final long serialVersionUID = 1L;
		public static final  String VIEW_MAIN = "VIEW_MAIN";
		public static final  String VIEW_ADDITION = "VIEW_ADDITION";
		public static final  String EDIT = "EDIT";
		public static final  String NOT = "NOT";
		public String value;
		
		public Permission(String value) {
			super();
			this.value = value;
		}
		
	};


	HashMap<Long, HashSet<Permission>> varPermissions;


	public VarsPermissions() {
		super();
		varPermissions = new HashMap<Long, HashSet<Permission>>();
	}
	
	public VarsPermissions(Long idVar, Permission perm) {
		super();
		varPermissions = new HashMap<Long, HashSet<Permission>>();
		HashSet<Permission> setPerm = new HashSet<Permission>();
		setPerm.add(perm);
		varPermissions.put(idVar, setPerm);
	}


	public HashMap<Long, HashSet<Permission>> getVarPermissions() {
		return varPermissions;
	}


	public void setVarPermissions(HashMap<Long, HashSet<Permission>> varPermissions) {
		this.varPermissions = varPermissions;
	}
	
	
	
	
	
/*	Long idVar;

	Set<Permission> permission;*/

	/*public Long getIdVar() {
		return idVar;
	}

	public Set<Permission> getPermission() {
		return permission;
	}

	public void setIdVar(Long idVar) {
		this.idVar = idVar;
	}

	public void setPermission(Set<Permission> permission) {
		this.permission = permission;
	}

	*//**
	 * @param idVar
	 * @param permission
	 *//*
	public VarsPermissions(Long idVar, Set<Permission> permission) {
		super();
		this.idVar = idVar;
		this.permission = permission;
	}

	public boolean equals(Object o) {
		VarsPermissions vp = (VarsPermissions) o;
		if (this.idVar.longValue() == vp.idVar.longValue()
				&& this.permission == vp.getPermission())
			return true;
		return false;

	}*/

}
