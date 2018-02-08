/**
 * 
 */
package com.vtb.domain;

/**
 * Организация для выгрузки в систему Рейтинги.
 * Сделан класс по просьбе Лисовского в письме Жогину от 12.05.2009 14:49
 * @author Andrey Pavlenko
 */
public class OrganisationPartner  extends VtbObject{
	 private String id;

	 private String name;

	 public String getId() {
	 		 return id;
	 }

	 public void setId(String id) {
	 		 this.id = id;
	 }

	 public String getName() {
	 		 return name;
	 }

	 public void setName(String name) {
	 		 this.name = name;
	 }

	public OrganisationPartner(String id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	 

}
