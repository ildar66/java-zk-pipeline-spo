package com.vtb.domain;



/**
 * This class describes the type's group of stop factors in the system
 * 
 * @author Администратор
 */
public class StopFactorGroup extends VtbObject {
	/**
     * 
     */
    private static final long serialVersionUID = 1L;
    long id_stop_factor_group;
	String name_group;
	
	public long getId_stop_factor_group() {
		return id_stop_factor_group;
	}
	public void setId_stop_factor_group(long id_stop_factor_group) {
		this.id_stop_factor_group = id_stop_factor_group;
	}
	public String getName_group() {
		return name_group;
	}
	public void setName_group(String name_group) {
		this.name_group = name_group;
	}
	
}
