package com.vtb.integration;

/*
 * Тип вызова.
 * @author Andrey Pavlenko
 */
public enum FicoRequestCallType {

	PVK("подразделение внутреннего контроля"),SEKZ("СЭКЗ"),ZAL("залоговое подразделение");
	private FicoRequestCallType(String name) {
		this.name = name;
	}
	private String name;
	public String getName() {
		return name;
	}
	
}
