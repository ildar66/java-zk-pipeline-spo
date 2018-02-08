/*
 * Created on 18.01.2008
 * 
 */
package org.uit.director.db.dbobjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * бин для запуска прцессов
 * @author Ижевцы
 *
 */
public class ProcessPacketBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	String nameTypeProcess;

	Integer idTypeProcess;

	Integer idParentTypeProcess = null;

	int countDays;

	String processXML;

	String imageXML;

	ArrayList<Object[]> paramsForLoadAttributes;

	ArrayList<Object[]> paramsForLoadStages;

	ArrayList<Object[]> ParamsForLoadStagesInRole;

	ArrayList<Object[]> paramsForLoadRoles;

	HashMap<String, HashMap<String, String>> paramsForLoadDataSources;

	ArrayList<Object[]> paramsForLoadRolesPermissions;

	ArrayList<Object[]> paramsForLoadStagesPermissions;

	ArrayList<Object[]> paramsForLoadVarConnections;

	ArrayList<Object[]> paramsForLoadVarNodes;

	ArrayList<Object[]> paramsForLoadSelectVarValues;
	
	ArrayList<Object[]> paramsForLoadRolesNodes;
	
	ArrayList<Object[]> paramsForLoadEdges;
	
	ArrayList<Object[]> paramsForLoadEdgeVars;

	Long idUser;

	String ipAddress;

	Long idTransaction;
	
	HashMap<String, Long> mapIDVariables;
	
	HashMap<String, Long> mapIDRoles;
	
	HashMap<String, Long> mapIDStages;
	
	HashMap<String, Long> mapIDDataSources;

	/**
	 * @return the idTransaction
	 */
	public Long getIdTransaction() {
		return idTransaction;
	}

	/**
	 * @param idTransaction
	 *            the idTransaction to set
	 */
	public void setIdTransaction(Long idTransaction) {
		this.idTransaction = idTransaction;

		String keyName = "ID_TRANSACTION";
		setIdByName(getParamsForLoadAttributes(), keyName, idTransaction);
		setIdByName(getParamsForLoadStages(), keyName, idTransaction);
		setIdByName(getParamsForLoadRoles(), keyName, idTransaction);
		setIdByName(getParamsForLoadStagesInRole(), keyName, idTransaction);
		setIdByName(getParamsForLoadRolesPermissions(), keyName, idTransaction);
		setIdByName(getParamsForLoadStagesPermissions(), keyName, idTransaction);
		setIdByName(getParamsForLoadVarConnections(), keyName, idTransaction);
		setIdByName(getParamsForLoadVarNodes(), keyName, idTransaction);
		setIdByName(getParamsForLoadSelectVarValues(), keyName, idTransaction);
		setIdByName(getParamsForLoadRolesNodes(), keyName, idTransaction);

	}

	/**
	 * 
	 * @param paramsList
	 * @param idx
	 * @param valueId
	 */
	private void setIdByIdx(ArrayList<Object[]> paramsList, int idx, Number valueId) {

		for (int i = 0; i < paramsList.size(); i++) {

			if (paramsList.get(i) instanceof Object[]) {
				Object[] obj = paramsList.get(i);
				obj[idx] = valueId;
			}

		}
	}

	private void setIdByName(ArrayList<Object[]> paramsList, String name, Number id) {

		for (int i = 0; i < paramsList.size(); i++) {

			Object[] obj = paramsList.get(i);
			for (int j = 0; j < obj.length; j++) {
				
				if (obj[j] instanceof String && obj[j].equals(name)) {
					obj[j] = id;
					break;
				}
			}

		}
	}

	/**
	 * @return the countDays
	 */
	public int getCountDays() {
		return countDays;
	}

	/**
	 * @return the imageXML
	 */
	public String getImageXML() {
		return imageXML;
	}

	/**
	 * @return the ipAddress
	 */
	public String getIpAddress() {
		return ipAddress;
	}

	/**
	 * @return the nameTypeProcess
	 */
	public String getNameTypeProcess() {
		return nameTypeProcess;
	}

	/**
	 * @return the paramsForLoadAttributes
	 */
	public ArrayList<Object[]> getParamsForLoadAttributes() {
		return paramsForLoadAttributes;
	}

	/**
	 * @return the paramsForLoadRoles
	 */
	public ArrayList<Object[]> getParamsForLoadRoles() {
		return paramsForLoadRoles;
	}

	/**
	 * @return the paramsForLoadStages
	 */
	public ArrayList<Object[]> getParamsForLoadStages() {
		return paramsForLoadStages;
	}

	/**
	 * @return the paramsForLoadStagesInRole
	 */
	public ArrayList<Object[]> getParamsForLoadStagesInRole() {
		return ParamsForLoadStagesInRole;
	}

	/**
	 * @return the processXML
	 */
	public String getProcessXML() {
		return processXML;
	}

	/**
	 * @return the userName
	 */
	public Long getIdUser() {
		return idUser;
	}

	/**
	 * @param countDays
	 *            the countDays to set
	 */
	public void setCountDays(int countDays) {
		this.countDays = countDays;
	}

	/**
	 * @param imageXML
	 *            the imageXML to set
	 */
	public void setImageXML(String imageXML) {
		this.imageXML = imageXML;
	}

	/**
	 * @param ipAddress
	 *            the ipAddress to set
	 */
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	/**
	 * @param nameTypeProcess
	 *            the nameTypeProcess to set
	 */
	public void setNameTypeProcess(String nameTypeProcess) {
		this.nameTypeProcess = nameTypeProcess;
	}

	/**
	 * @param paramsForLoadAttributes
	 *            the paramsForLoadAttributes to set
	 */
	public void setParamsForLoadAttributes(ArrayList<Object[]> paramsForLoadAttributes) {
		this.paramsForLoadAttributes = paramsForLoadAttributes;
	}

	/**
	 * @param paramsForLoadRoles
	 *            the paramsForLoadRoles to set
	 */
	public void setParamsForLoadRoles(ArrayList<Object[]> paramsForLoadRoles) {
		this.paramsForLoadRoles = paramsForLoadRoles;
	}

	/**
	 * @param paramsForLoadStages
	 *            the paramsForLoadStages to set
	 */
	public void setParamsForLoadStages(ArrayList<Object[]> paramsForLoadStages) {
		this.paramsForLoadStages = paramsForLoadStages;
	}

	/**
	 * @param paramsForLoadStagesInRole
	 *            the paramsForLoadStagesInRole to set
	 */
	public void setParamsForLoadStagesInRole(ArrayList<Object[]> paramsForLoadStagesInRole) {
		ParamsForLoadStagesInRole = paramsForLoadStagesInRole;
	}

	/**
	 * @param processXML
	 *            the processXML to set
	 */
	public void setProcessXML(String processXML) {
		this.processXML = processXML;
	}

	/**
	 * @param userName
	 *            the userName to set
	 */
	public void setIdUser(Long userName) {
		idUser = userName;
	}

	/**
	 * @return the idTypeProcess
	 */
	public int getIdTypeProcess() {
		return idTypeProcess;
	}

	/**
	 * @param idTypeProcess
	 *            the idTypeProcess to set
	 */
	public void setIdTypeProcess(int idTypeProcess) {
		this.idTypeProcess = idTypeProcess;
		String keyName = "ID_TYPE_PROCESS";
		setIdByName(getParamsForLoadAttributes(), keyName, idTypeProcess);
		setIdByName(getParamsForLoadStages(), keyName, idTypeProcess);
		setIdByName(getParamsForLoadRoles(), keyName, idTypeProcess);
		setIdByName(getParamsForLoadStagesInRole(), keyName, idTypeProcess);		
		setIdByName(getParamsForLoadEdges(), keyName, idTypeProcess);
		setIdByName(getParamsForLoadEdgeVars(), keyName, idTypeProcess);

	}

	/**
	 * @return the paramsForLoadDataSources
	 */
	public HashMap<String, HashMap<String, String>> getParamsForLoadDataSources() {
		return paramsForLoadDataSources;
	}

	/**
	 * @param paramsForLoadDataSources2
	 *            the paramsForLoadDataSources to set
	 */
	public void setParamsForLoadDataSources(HashMap<String, HashMap<String, String>> paramsForLoadDataSources2) {
		paramsForLoadDataSources = paramsForLoadDataSources2;
	}

	public Integer getIdParentTypeProcess() {
		return idParentTypeProcess;
	}

	public void setIdParentTypeProcess(Integer idParentTypeProcess) {
		this.idParentTypeProcess = idParentTypeProcess;
	}

	public ArrayList<Object[]> getParamsForLoadRolesPermissions() {
		return paramsForLoadRolesPermissions;
	}

	public ArrayList<Object[]> getParamsForLoadSelectVarValues() {
		return paramsForLoadSelectVarValues;
	}

	public ArrayList<Object[]> getParamsForLoadStagesPermissions() {
		return paramsForLoadStagesPermissions;
	}

	public ArrayList<Object[]> getParamsForLoadVarConnections() {
		return paramsForLoadVarConnections;
	}

	public ArrayList<Object[]> getParamsForLoadVarNodes() {
		return paramsForLoadVarNodes;
	}

	public void setParamsForLoadRolesPermissions(
			ArrayList<Object[]> paramsForLoadRolesPermissions) {
		this.paramsForLoadRolesPermissions = paramsForLoadRolesPermissions;
	}

	public void setParamsForLoadSelectVarValues(
			ArrayList<Object[]> paramsForLoadSelectVarValues) {
		this.paramsForLoadSelectVarValues = paramsForLoadSelectVarValues;
	}

	public void setParamsForLoadStagesPermissions(
			ArrayList<Object[]> paramsForLoadStagesPermissions) {
		this.paramsForLoadStagesPermissions = paramsForLoadStagesPermissions;
	}

	public void setParamsForLoadVarConnections(ArrayList<Object[]> paramsForLoadVarConnections) {
		this.paramsForLoadVarConnections = paramsForLoadVarConnections;
	}

	public void setParamsForLoadVarNodes(ArrayList<Object[]> paramsForLoadVarNodes) {
		this.paramsForLoadVarNodes = paramsForLoadVarNodes;
	}

	public Map<String, Long> getMapIDRoles() {
		if (mapIDRoles == null) {
			mapIDRoles = new HashMap<String, Long>();
		}
		return mapIDRoles;
	}

	public Map<String, Long> getMapIDStages() {
		if (mapIDStages == null) {
			mapIDStages = new HashMap<String, Long>();
		}
		return mapIDStages;
	}

	public Map<String, Long> getMapIDVariables() {
		if (mapIDVariables == null) {
			mapIDVariables = new HashMap<String, Long>();
		}
		return mapIDVariables;
	}

	public void setMapIDRoles(HashMap<String, Long> mapIDRoles) {
		this.mapIDRoles = mapIDRoles;
	}

	public void setMapIDStages(HashMap<String, Long> mapIDStages) {
		this.mapIDStages = mapIDStages;
	}

	public void setMapIDVariables(HashMap<String, Long> mapIDVariables) {
		this.mapIDVariables = mapIDVariables;
	}

	public ArrayList<Object[]> getParamsForLoadRolesNodes() {
		return paramsForLoadRolesNodes;
	}

	public void setParamsForLoadRolesNodes(ArrayList<Object[]> paramsForLoadRolesNodes) {
		this.paramsForLoadRolesNodes = paramsForLoadRolesNodes;
	}
	
	public void setParamsForLoadEdges(ArrayList<Object[]> paramsForLoadEdges) {
		this.paramsForLoadEdges = paramsForLoadEdges;
	}
	
	public void setParamsForLoadEdgeVars(ArrayList<Object[]> paramsForLoadEdgeVars) {
		this.paramsForLoadEdgeVars = paramsForLoadEdgeVars;
	}

	public ArrayList<Object[]> getParamsForLoadEdges() {
		return paramsForLoadEdges;
	}
	
	public ArrayList<Object[]> getParamsForLoadEdgeVars() {
		return paramsForLoadEdgeVars;
	}
}

