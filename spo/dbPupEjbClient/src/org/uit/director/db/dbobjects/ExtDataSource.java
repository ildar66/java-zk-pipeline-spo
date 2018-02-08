/*
 * Created on 18.06.2008
 * 
 */
package org.uit.director.db.dbobjects;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ExtDataSource implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String typeDS;
	Integer idTypeProcess;
	String nameDS;

	HashMap<String, String> paramsDS;

	/**
	 * @param typeDS
	 * @param paramsDS
	 */
	public ExtDataSource(String nameDS, Integer idTypeProcess , String typeDS, HashMap<String, String> paramsDS) {
		super();
		this.typeDS = typeDS;
		this.paramsDS = paramsDS;
		this.nameDS = nameDS;
		this.idTypeProcess = idTypeProcess;
	}

	/**
	 * @return the paramsDS
	 */
	public Map<String, String> getParamsDS() {
		return paramsDS;
	}

	/**
	 * @return the typeDS
	 */
	public String getTypeDS() {
		return typeDS;
	}

	/**
	 * @param paramsDS the paramsDS to set
	 */
	public void setParamsDS(HashMap<String, String> paramsDS) {
		this.paramsDS = paramsDS;
	}

	/**
	 * @param typeDS the typeDS to set
	 */
	public void setTypeDS(String typeDS) {
		this.typeDS = typeDS;
	}

	
	public String getValueParameterDS(String namePar) {
		
		String val = paramsDS.get(namePar.toLowerCase());
		if (val == null) {
			val = paramsDS.get(namePar.toUpperCase());
		}
		return val;
	}

	public Integer getIdTypeProcess() {
		return idTypeProcess;
	}

	public void setIdTypeProcess(Integer idTypeProcess) {
		this.idTypeProcess = idTypeProcess;
	}

	public String getNameDS() {
		return nameDS;
	}

	public void setNameDS(String nameDS) {
		this.nameDS = nameDS;
	}
	
	
}
