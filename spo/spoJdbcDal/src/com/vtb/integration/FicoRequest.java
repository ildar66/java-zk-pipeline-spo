package com.vtb.integration;

import java.io.Serializable;

/*
 * запрос в Fico.
 * @author Andrey Pavlenko
 */
public class FicoRequest implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private FicoRequestCallType callType;//тип экспертизы
	private Double loanAmount;//сумма заявки без валюты
	private boolean hasMemorandum;//есть ли документ типа кредитный меморандум
	private boolean hasDepositor;//есть ли залоги
	private Integer result=1000;//просто число 1000
	public FicoRequestCallType getCallType() {
		return callType;
	}
	public void setCallType(FicoRequestCallType callType) {
		this.callType = callType;
	}
	public Double getLoanAmount() {
		return loanAmount;
	}
	public void setLoanAmount(Double loanAmount) {
		this.loanAmount = loanAmount;
	}
	public boolean isHasMemorandum() {
		return hasMemorandum;
	}
	public void setHasMemorandum(boolean hasMemorandum) {
		this.hasMemorandum = hasMemorandum;
	}
	public boolean isHasDepositor() {
		return hasDepositor;
	}
	public void setHasDepositor(boolean hasDepositor) {
		this.hasDepositor = hasDepositor;
	}
	public Integer getResult() {
		return result;
	}
	public void setResult(Integer result) {
		this.result = result;
	}
	
}
