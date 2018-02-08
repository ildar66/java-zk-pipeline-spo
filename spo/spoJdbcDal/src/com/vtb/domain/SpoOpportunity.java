package com.vtb.domain;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * VtbObject "Cлужит для связи заявки, признаков отправке/получения, статусов
 * возврата между СПО и CRM"
 * 
 * @author IShafigullin
 * 
 */
public class SpoOpportunity extends VtbObject {

	private static final long serialVersionUID = 1L;

	private String id = null;// Идентификатор

	public final static String SPO_SEND_OK = "1";// (1 - отправлено)
	private String spoSend = null;// Признак отправки в СПО
	private Date spoSendDate = null;// Дата и время отправки в СПО

	public final static String SPO_ACCEPT_OK = "1";// 1 - принято
	public final static String SPO_ACCEPT_ERROR = "2";// 2 - ошибка загрузки
	public final static String SPO_DONTACCEPT = "0";// 0 - еще не принято
	private String spoAccept = null;// Признак получения системой СПО
	private java.sql.Timestamp spoAcceptDate = null;// Дата и время получения в СПО

	public final static String SPO_TYPE_LIMIT = "L";
	public final static String SPO_TYPE_CREDIT = "K";
	private String spoType = null;// Вид заявки(L – лимит, K - кредит)

	private String opportunityID = null;// Идентификатор лимита/код сделки
	private String accountID = null;// id организации в CRM
	/**
	 * Принято -1, Отказано по стоп фактору -2, Отказано КК -3, Отказ клиента -4
	 */
	public final static String CALL_BACK_OK = "1";
	public final static String CALL_BACK_NO_SF = "2";
	public final static String CALL_BACK_NO_KK = "3";
	public final static String CALL_BACK_NO_KLIENT = "4";
	private String callBack = null;// Статус возврата заявки (результат)
	private Date callBackDate = null;// Дата и время возврата

	@Override
	public boolean equals(Object anObject) {
		if (anObject == null) {
			return false;
		}
		if (!(anObject instanceof SpoOpportunity)) {
			return false;
		}
		SpoOpportunity aSpoOpportunity = (SpoOpportunity) anObject;
		return aSpoOpportunity.getId().equals(getId());
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("SpoOpportunity: id=" + getId() + "; spoSendDate=" + getSpoSendDate());
		// sb.append('\n');
		// sb.append(" IsActive: ");
		// sb.append(getIsActive());

		return sb.toString();
	}


	public SpoOpportunity(String aId) {
		setId(aId);
	}

	public String getAccountID() {
		return accountID;
	}

	public void setAccountID(String accountID) {
		this.accountID = accountID;
	}

	public String getCallBack() {
		return callBack;
	}

	public void setCallBack(String callBack) {
		this.callBack = callBack;
	}

	public Date getCallBackDate() {
		return callBackDate;
	}

	public void setCallBackDate(Date callBackDate) {
		this.callBackDate = callBackDate;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOpportunityID() {
		return opportunityID;
	}

	public void setOpportunityID(String opportunityID) {
		this.opportunityID = opportunityID;
	}

	public String getSpoAccept() {
		return spoAccept;
	}

	public void setSpoAccept(String spoAccept) {
		this.spoAccept = spoAccept;
	}

	public Timestamp getSpoAcceptDate() {
		return spoAcceptDate;
	}

	public void setSpoAcceptDate(Timestamp spoAcceptDate) {
		this.spoAcceptDate = spoAcceptDate;
	}

	public String getSpoSend() {
		return spoSend;
	}

	public void setSpoSend(String spoSend) {
		this.spoSend = spoSend;
	}

	public Date getSpoSendDate() {
		return spoSendDate;
	}

	public void setSpoSendDate(Date spoSendDate) {
		this.spoSendDate = spoSendDate;
	}

	public String getSpoType() {
		return spoType;
	}

	public void setSpoType(String spoType) {
		this.spoType = spoType;
	}

}
