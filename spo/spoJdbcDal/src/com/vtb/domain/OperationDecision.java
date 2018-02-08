package com.vtb.domain;

import java.util.List;

/**
 * Порядок принятия решения о проведении операции
 * @author Michail Kuznetsov
 */
public class OperationDecision  extends VtbObject{
	private static final long serialVersionUID = 2L;

	private String accepted;   // кем принимаются
    private String specials;   // особенности
	private String decisionListAsString;   // список решений в виде одной строки
    
	public OperationDecision() {
        super();
    }

	   
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void validate() {
	   //if ((currency == null) || (currency.getCode() == null)) addError("Комиссия. Валюта не определена");
	   //if ((name == null) || (name.getId() == null)) addError("Комиссия. Тип комиссии не определен");
	   //if ((procent_order == null) || (procent_order.getId() == null)) addError("Комиссия. Порядок уплаты процентов не определен");
	}


	public String getAccepted() {
		return accepted;
	}


	public void setAccepted(String accepted) {
		this.accepted = accepted;
	}


	public String getSpecials() {
		return specials;
	}


	public void setSpecials(String specials) {
		this.specials = specials;
	}


	public String getDecisionListAsString() {
		return decisionListAsString;
	}


	public void setDecisionListAsString(List<String> decisionList) {
		decisionListAsString = "";
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		if (decisionList != null) 
			for (String element : decisionList) {
				if (first) {
					sb.append(element);
					first = false;
				} else
					sb.append("\n\n" + element);
			}
		decisionListAsString = sb.toString();
	}
}
