/*
 * Created on 14.07.2008
 * 
 */
package org.uit.director.db.dbobjects;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class WorkflowSubProcess implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Integer idParentTypeProcess;
	Integer idTypeProcess;
	HashMap<Long, Long> mapVarsIn;
	HashMap<Long, Long> mapVarsOut;
	HashMap<Long, Long> mapRoles;
	
	
	/**
	 * @param idParentTypeProcess
	 * @param idTypeProcess
	 * @param mapVarsIn
	 * @param mapVarsOut
	 * @param mapRoles
	 */
	public WorkflowSubProcess(Integer idTypeProcess, Integer idParentTypeProcess, HashMap<Long, Long> mapVarsIn, HashMap<Long, Long> mapVarsOut, HashMap<Long, Long> mapRoles) {
		super();
		this.idParentTypeProcess = idParentTypeProcess;
		this.idTypeProcess = idTypeProcess;
		this.mapVarsIn = mapVarsIn;
		this.mapVarsOut = mapVarsOut;
		this.mapRoles = mapRoles;
	}
	
	
	/**
	 * @param idParentTypeProcess
	 * @param idTypeProcess
	 */
	public WorkflowSubProcess(Integer idParentTypeProcess, Integer idTypeProcess) {
		super();
		this.idParentTypeProcess = idParentTypeProcess;
		this.idTypeProcess = idTypeProcess;
		mapVarsIn = new HashMap<Long, Long>();
		mapVarsOut = new HashMap<Long, Long>();
		mapRoles = new HashMap<Long, Long>();
	}


	public Integer getIdParentTypeProcess() {
		return idParentTypeProcess;
	}
	public Integer getIdTypeProcess() {
		return idTypeProcess;
	}
	public Map<Long, Long> getMapRoles() {
		return mapRoles;
	}
	public Map<Long, Long> getMapVarsIn() {
		return mapVarsIn;
	}
	public Map<Long, Long> getMapVarsOut() {
		return mapVarsOut;
	}
	public void setIdParentTypeProcess(Integer idParentTypeProcess) {
		this.idParentTypeProcess = idParentTypeProcess;
	}
	public void setIdTypeProcess(Integer idTypeProcess) {
		this.idTypeProcess = idTypeProcess;
	}
	public void setMapRoles(HashMap<Long, Long> mapRoles) {
		this.mapRoles = mapRoles;
	}
	public void setMapVarsIn(HashMap<Long, Long> mapVarsIn) {
		this.mapVarsIn = mapVarsIn;
	}
	public void setMapVarsOut(HashMap<Long, Long> mapVarsOut) {
		this.mapVarsOut = mapVarsOut;
	}
	
	
	
	
	

}
