package org.uit.director.decider;

import java.util.Map;

import org.nfunk.jep.JEP;

public class TransitionAction {

	JEP jep;
	String actionExpression;
	Map<String, Object[]> varsData ;
	
	public String getActionExpression() {
		return actionExpression;
	}
	public void setActionExpression(String actionExpression) {
		this.actionExpression = actionExpression;
	}
	public JEP getJep() {
		return jep;
	}
	public void setJep(JEP jep) {
		this.jep = jep;
	}
	public Map<String, Object[]> getVarsData() {
		return varsData;
	}
	public void setVarsData(Map<String, Object[]> varsData2) {
		varsData = varsData2;
	}
	
	
	
	
	
	
	
	
}
