package com.vtb.domain;

import java.util.ArrayList;

/**
 * Группа видов сделки
 * @author Andrey Pavlenko
 *
 */
public class ProductGroup {
	private Long id;
    private String name;
    private ArrayList<Long> limitTypes;
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the limitTypes
	 */
	public ArrayList<Long> getLimitTypes() {
		return limitTypes;
	}
	public ProductGroup(Long id, String name) {
		super();
		this.id = id;
		this.name = name;
		limitTypes = new ArrayList<Long>();
	}
    
    
}
