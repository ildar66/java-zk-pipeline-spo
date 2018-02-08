package com.vtb.domain;


/**
 * Single string of task for report 'CRMClaims' 
 * @author Andrey Pavlenko
 */
public class CRMClaimsReportOperation  extends VtbObject{
	private static final long serialVersionUID = 1L;
	
	private String crmCode;
	private String spoSendDate;
	private String spoAcceptDate;
	private String statusCode;
	private String userLogin;
	private String spoNumber;
	private String fio;
	
    /**
     * @return fio
     */
    public String getFio() {
        return fio;
    }
    /**
     * @param fio fio
     */
    public void setFio(String fio) {
        this.fio = fio;
    }
    /**
     * @return crmCode
     */
    public String getCrmCode() {
        return crmCode;
    }
    /**
     * @param crmCode crmCode
     */
    public void setCrmCode(String crmCode) {
        this.crmCode = crmCode;
    }
    /**
     * @return spoSendDate
     */
    public String getSpoSendDate() {
        return spoSendDate;
    }
    /**
     * @param spoSendDate spoSendDate
     */
    public void setSpoSendDate(String spoSendDate) {
        this.spoSendDate = spoSendDate;
    }
    /**
     * @return spoAcceptDate
     */
    public String getSpoAcceptDate() {
        return spoAcceptDate;
    }
    /**
     * @param spoAcceptDate spoAcceptDate
     */
    public void setSpoAcceptDate(String spoAcceptDate) {
        this.spoAcceptDate = spoAcceptDate;
    }

    /**
     * @return statusCode
     */
    public String getStatusCode() {
        return statusCode;
    }
    /**
     * @param statusCode statusCode
     */
    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }
    /**
     * @return userLogin
     */
    public String getUserLogin() {
        return userLogin;
    }
    /**
     * @param userLogin userLogin
     */
    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }
    /**
     * @return spoNumber
     */
    public String getSpoNumber() {
        return spoNumber;
    }
    /**
     * @param spoNumber spoNumber
     */
    public void setSpoNumber(String spoNumber) {
        this.spoNumber = spoNumber;
    }

}

